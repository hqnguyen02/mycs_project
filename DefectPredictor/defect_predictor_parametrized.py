from datetime import datetime
from pydriller.repository import Repository
from pydriller.domain.commit import Commit, ModificationType
from collections import defaultdict
from repominer.mining.base import BaseMiner, FixingCommitClassifier
from pydriller import ModificationType
from typing import Dict, Generator, List
from sklearn.preprocessing import MinMaxScaler
from pydriller.metrics.process.contributors_count import ContributorsCount
import pytz

import os
import nltk
import re

# Important: downloading resources for NLTK (Taken from repominer)
try:
    nltk.data.find('tokenizers/punkt')
except LookupError:
    nltk.download('punkt')

try:
    nltk.data.find('corpora/stopwords')
except LookupError:
    nltk.download('stopwords')


class PythonMiner(BaseMiner):
    """
    A class that mines a Git repository for bug-fixing commits for python files.
    """
    def __init__(self, config):
        super().__init__(
            config["url_to_repo"],
            config["clone_repo_to"],
            config["branch"]
        )
        self.FixingCommitClassifier = FixingCommitClassifier
        self.clone_repo_to = config["clone_repo_to"]
        self.python_files = []
        self.from_date = datetime.strptime(config["from_date"], "%Y-%m-%d")
        self.to_date = datetime.strptime(config["to_date"], "%Y-%m-%d")
        self.fixing_commits = self.get_fixing_commits() 
        
    def discard_undesired_fixing_commits(self, commits):
        """
        Filters out fixing commits that do not modify any Python files.
        Parameters:
            commits (list): A list of commit hashes (strings) that are considered bug-fixing.
        Returns:
            dict: A dictionary mapping valid commit hashes to a list of modified Python file paths.
        """
        self.sort_commits(commits)
        modified_files = defaultdict(list)
        to_remove = []

        for commit in Repository(self.path_to_repo, since=self.from_date, to=self.to_date).traverse_commits():
            i = 0
            while i < len(commit.modified_files):
                mf = commit.modified_files[i]
                if (
                    mf.change_type == ModificationType.MODIFY
                    and mf.new_path
                    and mf.new_path.endswith('.py')
                ):
                    modified_files[commit.hash].append(mf.new_path)
                    break
                i += 1
            if i == len(commit.modified_files):
                to_remove.append(commit.hash)
        commits = [commit_hash for commit_hash in commits if commit_hash not in to_remove]
        return modified_files

    def get_fixing_commits(self, num_workers=8) -> Dict[str, List[str]]:
        """
        Return a list of bug-fixing commit hash, categorized as fixing "conditionals", "configuration data",
        "dependencies", "documentation", "idempotency", "security", "service", "syntax".

        This method returns the commits whose message indicates defective scripts.
        `Note:` Beside returning the list of bug-fixing commits, it also updates the attribute ``fixing_commits``.

        Reference: https://github.com/radon-h2020/radon-repository-miner/tree/master

        Parameters
        ----------
        num_workers : int
            Number of threads. Default 8.

        Returns
        -------
        List[str]
            A dictionary of bug-fixing commits hashes and boolean values for every fixing labels.
            {'hash1': ['SERVICE', 'SYNTAX', ...]}
        """

        commits_labels = {}
        commits = []

        for commit in Repository(self.path_to_repo, only_in_branch=self.branch, num_workers=num_workers).traverse_commits():

            if commit.hash in self.fixing_commits:
                continue

            fcc = self.FixingCommitClassifier(commit)

            if fcc.fixes_syntax():
                commits_labels.setdefault(commit.hash, []).append('SYNTAX')

            if commit.hash in commits_labels:
                commits.append(commit.hash)

        if commits:
            # Discard commits that do not touch IaC files
            self.discard_undesired_fixing_commits(commits)

            # Update the list of fixing commits
            self.fixing_commits.extend(commits)

            # Sort fixing_commits in ascending order of date
            self.sort_commits(self.fixing_commits)

            for sha in list(commits_labels.keys()):
                if sha not in commits:  # It means it was an undesired commit
                    del commits_labels[sha]

        return commits_labels

    def ignore_file(self, path_to_file, content):
        """
        Determines whether a file should be ignored based on its file extension.

        Parameters:
            path_to_file (str): The path to the file being considered.
            content (str): The content of the file.

        Returns:
            bool: True if the file is not a Python file.
        """
        return not path_to_file.endswith('.py')
    
    def get_modified_files(self):
        """
        Retrieves modified Python files using discard_undesired_fixing_commits.
        
        Returns:
            dict: A dictionary mapping commit hashes to lists of modified Python file paths.
        """ 
        commits = [commit.hash for commit in Repository(
            self.path_to_repo, 
            clone_repo_to=self.clone_repo_to, 
            only_in_branch=self.branch,
            since=self.from_date,  
            to=self.to_date 
        ).traverse_commits()]
        self.python_files = self.discard_undesired_fixing_commits(commits)
        return self.python_files
    
    def get_commit_date(self, commit_hash):
        """Fetches the commit date for a given commit hash using PyDriller.

        Parameters:
            commit_hash (str): The hash of the commit whose date is needed.

        Returns:
            datetime: The author date of the matching commit, or None if not found.
        """
        repo = Repository(self.path_to_repo)  

        for commit in repo.traverse_commits():

            if commit.hash == commit_hash:

                return commit.author_date  
            
        return None

    def is_test_file(self, file_path: str) -> bool:
        """
        Determines whether a given Python file is likely a test file or irrelevant.

        Parameters:
            file_path (str): The path to the file being evaluated.

        Returns:
            bool: True if the file is a test file or irrelevant, False otherwise.
        """
        if not file_path.endswith('.py'):

            return True
        file_path = file_path.lower()
        return (
            '/test/' in file_path or
            '\\test\\' in file_path or
            file_path.startswith('test_') or
            file_path.endswith('_test.py') or
            'test_' in os.path.basename(file_path)
        )

    def calculate_recency(self):
        """
        Tracks how recent a commit is for each modified Python file.

        This method calculates a recency score for each file that was modified in a bug-fix commit within the given time range.

        Returns
        -------
        Dict[str, datetime]
            A dictionary where the key is the file path and the value is the date of the most recent commit that modified the file.
        """

        recency_days = {}
        recency_dates = {}

        # Ensure to_date is timezone-aware
        to_date_utc = self.to_date.replace(tzinfo=pytz.UTC) if self.to_date.tzinfo is None else self.to_date

        # Iterate through the Python files modified in fixing commits
        for commit in Repository(self.path_to_repo, since=self.from_date, to=self.to_date).traverse_commits():

            if commit.hash in self.fixing_commits:

                for modified_file in commit.modified_files:

                    # Checks if a file is modified & if it's a python file & if it's not a testfile
                    if modified_file.change_type == ModificationType.MODIFY and modified_file.new_path.endswith('.py') and not self.is_test_file(modified_file.new_path) and not self.is_test_file(modified_file.new_path):
                        file_path = modified_file.new_path
                        commit_date_utc = commit.author_date.replace(tzinfo=pytz.UTC) if commit.author_date.tzinfo is None else commit.author_date
                        
                        # Update recency_dates only if the commit date is more recent
                        if file_path not in recency_dates or commit_date_utc > recency_dates[file_path]:
                            recency_dates[file_path] = commit_date_utc

        # Calculate recency in days
        for file_path, recent_date in recency_dates.items():
            recency_days[file_path] = (to_date_utc - recent_date).days

        return recency_days


    def calculate_frequency(self):
        """
        Tracks how frequently a Python file was modified in bug-fix commits.

        This method calculates a total frequency score for each Python file that was modified all bug-fix commits.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the total number of times it was modified.
        """

        frequency_scores = defaultdict(int)

        # Iterate through the Python files modified in fixing commits
        for commit in Repository(self.path_to_repo, since=self.from_date, to=self.to_date).traverse_commits():
            
            if commit.hash in self.fixing_commits:
                
                for modified_file in commit.modified_files:

                    # Checks if a file is modified & if it's a python file & if it's not a testfile
                    if modified_file.change_type == ModificationType.MODIFY and modified_file.new_path.endswith('.py') and not self.is_test_file(modified_file.new_path) and not self.is_test_file(modified_file.new_path):
                        frequency_scores[modified_file.new_path] += 1

        return frequency_scores

    def calculate_lines_modified(self):
        """
        Calculates the total number of lines modified (added + deleted) for each Python file
        modified in all bug-fix commits.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the total number of modified lines
            (i.e., lines added + lines deleted) for that file.
        """
        
        modified_scores = defaultdict(int)

        # Iterate through the Python files modified in fixing commits
        for commit in Repository(self.path_to_repo, since=self.from_date, to=self.to_date).traverse_commits():
            
            if commit.hash in self.fixing_commits:
                
                for modified_file in commit.modified_files:

                    # Checks if a file is modified & if it's a python file & if it's not a testfile
                    if modified_file.change_type == ModificationType.MODIFY and modified_file.new_path.endswith('.py') and not self.is_test_file(modified_file.new_path) and not self.is_test_file(modified_file.new_path):
                        modified_scores[modified_file.new_path] += modified_file.added_lines + modified_file.deleted_lines

        return modified_scores
    

    def calculate_loc(self):
        """
        Calculates the Lines of Code (LOC) for each Python file modified in bug-fix commits.

        This method calculates the LOC score for each Python file that was modified in a bug-fix commit
        by using the `nloc` attribute from the `pydriller` `ModifiedFile` object.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the number of lines of code (LOC) in that file.
        """

        loc_scores = defaultdict(int)
        processed_files = set()  # To keep track of files that have already been processed

        # Iterate through the Python files modified in fixing commits
        for commit in Repository(self.path_to_repo, since=self.from_date, to=self.to_date).traverse_commits():

            if commit.hash in self.fixing_commits:  # Check if this commit is a fixing commit

                for modified_file in commit.modified_files: 

                    # Checks if a file is modified & if it's a python file & if it's not a testfile
                    if modified_file.change_type == ModificationType.MODIFY and modified_file.new_path.endswith('.py') and not self.is_test_file(modified_file.new_path) and not self.is_test_file(modified_file.new_path):
                        file_path = modified_file.new_path

                        if file_path not in processed_files:  # Process the file only once
                            processed_files.add(file_path) 
                            loc_scores[file_path] = modified_file.nloc  # Store the lines of code
                            
        return loc_scores

    def calculate_cyclomatic_complexity(self):
        """
        Calculates the cyclomatic complexity for each Python file modified in bug-fix commits.

        This method calculates the complexity score for each Python file that was modified in a bug-fix commit
        by using the `complexity` attribute from the `pydriller` `ModifiedFile` object.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the cyclomatic complexity of that file.
        """

        complexity_scores = defaultdict(int)
        processed_files = set()  # To keep track of files that have already been processed

        # Iterate through the Python files modified in fixing commits
        for commit in Repository(self.path_to_repo, since=self.from_date, to=self.to_date).traverse_commits():

            if commit.hash in self.fixing_commits:  # Check if this commit is a fixing commit

                for modified_file in commit.modified_files:

                    # Checks if a file is modified & if it's a python file & if it's not a testfile
                    if modified_file.change_type == ModificationType.MODIFY and modified_file.new_path.endswith('.py') and not self.is_test_file(modified_file.new_path) and not self.is_test_file(modified_file.new_path):
                        file_path = modified_file.new_path

                        if file_path not in processed_files:  # Process the file only once
                            processed_files.add(file_path)  # Mark the file as processed
                            complexity_scores[file_path] = modified_file.complexity

        return complexity_scores

    def calculate_minor_contributor(self):
        """
        Calculates the number of minor contributors for each Python file modified in bug-fix commits.

        This method calculates the number of minor contributors for each Python file that was modified in a bug-fix commit
        by using the `.count_minor` function from `pydriller`

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the number of minor contributors of that file.
        """

        metric = ContributorsCount(path_to_repo=self.path_to_repo,
        since=self.from_date,
        to=self.to_date)

        minor = metric.count_minor()
        return minor

    def normalize_recency(self, recency_scores):
        """
        Normalizes the dictionary of recency scores

        Parameters
        ----------
        recency_scores : Dict[str, int]
            A dictionary where the key is the file path and the value is the date of the most recent commit that modified the file.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the date of the most recent commit that modified the file but normalized.
        """
        
        max_recency = max(recency_scores.values()) # Get the max value
        normalized_recency = {
            file: (1 - (recency / max_recency)) for file, recency in recency_scores.items() # Divide each score by max value
        }
        return normalized_recency
    
    def normalize_frequency(self, frequency_scores):
        """
        Normalizes the dictionary of frequency scores using MinMaxScaler

        Parameters
        ----------
        frequency_scores : Dict[str, int]
            A dictionary where the key is the file path and the value is the total number of times it was modified.

        Returns
        -------
        Dict[str, int]
              A dictionary where the key is the file path and the value is the total number of times it was modified but normalized.
        """
        scaler = MinMaxScaler()
        frequency_values = list(frequency_scores.values())
        frequency_values = [[val] for val in frequency_values]  # Reshape to 2D array

        # Normalize
        normalized_frequency = scaler.fit_transform(frequency_values)

        # Re-map normalized values back to frequency_scores dictionary
        normalized_frequency_scores = {
            key: normalized_frequency[i][0] for i, key in enumerate(frequency_scores.keys())
        }

        return normalized_frequency_scores

    def normalize_modified_lines(self, modified_scores):
        """
        Normalizes the dictionary of modified scores

        Parameters
        ----------
        modified_scores : Dict[str, int]
            A dictionary where the key is the file path and the value is the total number of modified lines
            (i.e., lines added + lines deleted) for that file.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the total number of modified lines
            (i.e., lines added + lines deleted) for that file but normalized.
        """

        max_modified_lines = max(modified_scores.values()) # Get the max value
        normalized_modified_lines = {
            file: score / max_modified_lines for file, score in modified_scores.items() # Divide each score by max value
        }
        return normalized_modified_lines
    
    def normalize_loc(self, loc_scores):
        """
        Normalizes the dictionary of loc scores

        Parameters
        ----------
        loc_scores : Dict[str, int]
            A dictionary where the key is the file path and the value is the number of lines of code (LOC) in that file.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the number of lines of code (LOC) in that file but normalied.
        """

        max_loc = max(loc_scores.values()) # Get the max value
        normalized_loc = {
            file: score / max_loc for file, score in loc_scores.items() # Divide each score by max value
        }
        return normalized_loc

    def normalize_complexity(self, complexity_scores):
        """
        Normalizes the dictionary of complexity scores

        Parameters
        ----------
        complexity_scores : Dict[str, int]
            A dictionary where the key is the file path and the value is the cyclomatic complexity of that file.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the cyclomatic complexity of that file but normalized.
        """
         
        max_complexity = max(complexity_scores.values()) # Get the max value
        normalized_complexity = {
            file: score / max_complexity for file, score in complexity_scores.items() # Divide each score by max value
        }
        return normalized_complexity

    def normalize_minor_contributor(self, minor_contributor_scores):
        """
        Normalizes the dictionary of minor contributor score

        Parameters
        ----------
        minor_contributor_scores : Dict[str, int]
            A dictionary where the key is the file path and the value is the number of minor contributors of that file.

        Returns
        -------
        Dict[str, int]
            A dictionary where the key is the file path and the value is the number of minor contributors of that file but normalized.
        """
         
        max_minor_contributor = max(minor_contributor_scores.values()) # Get the max value
        if (max_minor_contributor == 0) :
            return minor_contributor_scores
        normalized_minor_contributor = {
            file: score / max_minor_contributor for file, score in minor_contributor_scores.items() # Divide each score by max value
        }
        return normalized_minor_contributor

    def get_normalized_metrics(self):
        """
        Calculates and normalizes all relevant metrics for files modified in fixing commits.

        Returns
        -------
        Dict[str, Dict[str, float]]
            A nested dictionary where the outer key is the file path and the inner dictionary
            contains the normalized scores for 'recency', 'frequency', 'modified_lines',
            'loc', 'complexity', and 'minor_contributors'. Returns an empty dict if no
            fixing commits or relevant files are found.
        """
        # Get the raw scores for each parameter
        recency_scores = self.calculate_recency()
        frequency_scores = self.calculate_frequency()
        modified_scores = self.calculate_lines_modified()
        loc_scores = self.calculate_loc()
        complexity_scores = self.calculate_cyclomatic_complexity()
        minor_contributor_scores = self.calculate_minor_contributor() # This returns Dict[filepath, count]

        # Check if any scores were generated, otherwise return empty
        if not (recency_scores or frequency_scores or modified_scores or loc_scores or complexity_scores or minor_contributor_scores):
             print(f"Warning: No metrics calculated for repo {self.path_to_repo} between {self.from_date} and {self.to_date}. Skipping normalization.")
             return {}

        # --- Normalization ---
        normalized_recency = self.normalize_recency(recency_scores)
        normalized_frequency = self.normalize_frequency(frequency_scores)
        normalized_modified_lines = self.normalize_modified_lines(modified_scores)
        normalized_loc = self.normalize_loc(loc_scores)
        normalized_complexity = self.normalize_complexity(complexity_scores)
        normalized_minor_contributor = self.normalize_minor_contributor(minor_contributor_scores)

        # --- Combine into a single structure ---
        combined_metrics = defaultdict(dict)
        all_files = set(recency_scores.keys()) | set(frequency_scores.keys()) | \
                    set(modified_scores.keys()) | set(loc_scores.keys()) | \
                    set(complexity_scores.keys()) | set(minor_contributor_scores.keys())

        for file_path in all_files:
            # Use .get() with default value 0.0 for metrics that might be missing for a file
            combined_metrics[file_path]['recency'] = normalized_recency.get(file_path, 0.0)
            combined_metrics[file_path]['frequency'] = normalized_frequency.get(file_path, 0.0)
            combined_metrics[file_path]['modified_lines'] = normalized_modified_lines.get(file_path, 0.0)
            combined_metrics[file_path]['loc'] = normalized_loc.get(file_path, 0.0)
            combined_metrics[file_path]['complexity'] = normalized_complexity.get(file_path, 0.0)
            combined_metrics[file_path]['minor_contributors'] = normalized_minor_contributor.get(file_path, 0.0) # Use the normalized dict here

        return dict(combined_metrics) # Convert back to regular dict
    
    def calculate_risk(self, w1, w2, w3, w4, w5, w6):
        """
        Calculates a risk score for each Python file based on multiple factors:
        recency, frequency, keyword frequency, number of lines modified, lines of code, and number of minor contributors

        r = Recency score (recent changes matter more)
        f = Frequency score (higher frequency means more unstable)
        m = Number of lines modified (large modification indicates more risk)
        loc = Lines of code (larger files tend to be harder to maintain)
        c = Cyclomatic complexity
        mc = Number of minor contributors
        
        Risk is calculated using the formula:
        Score = (.225 * r) + ( .3 * f ) + ( .15 * m ) + ( .1 * loc ) + ( .125 * c ) + (0.1 * mc)

        Returns
        -------
        Dict[str, float]
            A dictionary where the key is the file path and the value is the calculated risk score for that file.
        """

        risk_scores = defaultdict(float)
        processed_files = set()  # To keep track of files that have already been processed

        # Get the score for each parameter by calling calculate functions
        recency_score = self.calculate_recency()
        frequency_score = self.calculate_frequency()
        modified_score = self.calculate_lines_modified()
        loc_score = self.calculate_loc()
        complexity_score = self.calculate_cyclomatic_complexity()
        minor_contributor_score = self.calculate_minor_contributor()

        # Get the normalized score for each parameter by calling normalize functions
        normalized_recency = self.normalize_recency(recency_score)
        normalized_frequency = self.normalize_frequency(frequency_score)
        normalized_modified_lines = self.normalize_modified_lines(modified_score)
        normalized_loc = self.normalize_loc(loc_score)
        normalized_complexity = self.normalize_complexity(complexity_score)
        normalized_minor_contributor = self.normalize_minor_contributor(minor_contributor_score)

         # Iterate through the Python files modified in fixing commits
        for commit in Repository(self.path_to_repo, since=self.from_date, to=self.to_date).traverse_commits():

            if commit.hash in self.fixing_commits:  # Check if this commit is a fixing commit

                for modified_file in commit.modified_files:
                    
                    # Checks if a file is modified & if it's a python file & if it's not a testfile
                    if modified_file.change_type == ModificationType.MODIFY and modified_file.new_path.endswith('.py') and not self.is_test_file(modified_file.new_path) and not self.is_test_file(modified_file.new_path):
                        file_path = modified_file.new_path

                        if file_path not in processed_files:
                            r = normalized_recency.get(file_path, 0.0)
                            f = normalized_frequency.get(file_path, 0.0)
                            m = normalized_modified_lines.get(file_path, 0.0)
                            loc = normalized_loc.get(file_path, 0.0)
                            c = normalized_complexity.get(file_path, 0.0)
                            mc = normalized_minor_contributor.get(file_path, 0.0) 
                            risk_score = ( w1 * r ) + ( w2 * f ) + ( w3 * m ) + ( w4 * loc ) + ( w5 * c ) + ( w6 * mc )
                            risk_scores[file_path] =  risk_score
                            processed_files.add(file_path)

        return risk_scores



class FixingCommitClassifier:

    def __init__(self, commit: Commit):
        """
        The class constructor.

        Parameters
        ----------
        commit: Commit
            The commit to analyze.

        Raises
        ------
        TypeError
            If commit is None
        """

        if commit is None:
            raise TypeError('Expected a pydriller.domain.commit.Commit object.')

        self.commit = commit
        self.sentences = []  # will be list of tokens list

        for sentence in nltk.sent_tokenize(commit.msg):
            # split into words
            tokens = nltk.tokenize.word_tokenize(sentence)

            # remove all tokens that are not alphabetic
            tokens = [word.strip() for word in tokens if word.isalpha()]

            self.sentences.append(tokens)

    def fixes_syntax(self):
        """
        Return True if the commit fixes a syntax issue.

        Returns
        -------
        bool
            True if the commit fixes syntax. False, otherwise.
        """

        for sentence in self.sentences:
            sentence = ' '.join(sentence)

            if self.has_defect_pattern(sentence):
                return True

        return False

    def has_defect_pattern(self, text: str) -> bool:
        """
        Check if the given text contains any defect-related patterns.
        
        Parameters
        ----------
        text: str
            The text to check for defect patterns.

        Returns
        -------
        bool
            True if defect-related patterns are found, False otherwise.
        """
        
        string_pattern = ['error', 'bug', 'fix', 'issue', 'mistake', 'incorrect', 'fault', 'defect', 'flaw', 'update', 'modified']
        return any(word in text.lower() for word in string_pattern)