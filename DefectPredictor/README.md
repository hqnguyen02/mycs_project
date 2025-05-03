# csc495-project

Repo for CSC495 Software Testing Team Project on Defect Prediction.

<h2>Parameters in the Formula</h2>

- Recency (Date committed)
- Frequency (How often the files are changed)
- Number of lines modified
- Lines of code in a file
- Cyclomatic Complexity
- Number of minor contributors

<h2>Weight Formula</h2>
Risk Score=w1​⋅R+w2​⋅F+w3​⋅M+w4​⋅L+w5​⋅C

- R = Recency score (recent changes matter more)
  - 45%
- F = Frequency score (higher frequency means more unstable)
  - 17.5%
- M = Number of lines modified
  - 7.5%
- L = Lines of Code
  - 7.5%
- C = Cyclomatic Complexity
  - 10.%
- MC = Number of minor contributors
  - 12.5%

<h2>Data Collection</h2>

- RepoMiner
  - Gets all commits
    - Date
      - Calculate recency
    - Commit message
    - Files
    - Frequency
- Pydriller
  - Calculates a lot of the parameters for the files in the commits
    - Lines added
    - Lines deleted
    - Lines of Code
    - Cyclomatic Complexity
    - Number of minor contributors

<h2>Timeline</h2>

- Sprint 1: Research and develop a prototype (demo) for calculating files hotspot for bugs of one Github repo

- Sprint 2: Implement calculate_recency() and calculate_frequency()

- Sprint 3: Debug and implement calculate_loc(), calculate_cyclomatic_complexity() calculate_lines_modified(), and calculate_risk()

- Sprint 4: Fix calculate_recency(), big update on README.md, normalize data, and redistribute weight
- Sprint 5: Exclude test files in risk calculation, document code extensively, implement calculate_minor()

- Sprint 6: Fix calculate minor(), implement weight optimization function

- Wrap Up: Implement caching factor scores for faster optimization, finish report

<h2>Running the Project</h2>

<h3>Installation</h3>

Clone the repo: `https://github.ncsu.edu/sfeng9/csc495-project.git`

Change the current working directory: `cd csc495-project`

<h3>Project Structure</h3>

<b>.gitignore</b>: Specifies intentionally untracked files (e.g., .venv)

<b>Dockerfile</b>: Defines the Docker container environment for running the project

<b>Presentation 1.pdf</b>: Slides for presentation 1

<b>Presentation 2.pdf</b>: Slides for presentation 2

<b>Presentation 3.pdf</b>: Slides for presentation 3

<b>Presentation 4.pdf</b>: Slides for presentation 4

<b>Presentation 5.pdf</b>: Slides for presentation 5

<b>Presentation 6.pdf</b>: Slides for presentation 6

<b>Presentation Final.pdf</b>: Slides for final presentation


<b>README.md</b>: This file: Project overview, setup, and usage instructions

<b>Report.pdf</b>: Final project report document

<b>config.json</b>: Configuration for single repository analysis (used by harness.py)

<b>config1.json</b>: Configuration for repository 1 (used by optimize_harness.py)

<b>config2.json</b>: Configuration for repository 2 (used by optimize_harness.py)

<b>config3.json</b>: Configuration for repository 3 (used by optimize_harness.py)

<b>config4.json</b>: Configuration for repository 4 (used by optimize_harness.py)

<b>defect_predictor.py</b>: Main Python script containing the PythonMiner class for metric calculation and risk scoring (using optimized weights)

<b>defect_predictor_parametrized.py</b>: Version of PythonMiner allowing weights to be passed as arguments (used by optimize_harness.py)

<b>ground_truth.csv</b>: Ground truth data (risky files) for various repositories/timeframes (used by harness.py)

<b>harness.py</b>: Script to run defect prediction on a single config (config.json) and compare results against ground_truth.csv

<b>optimize_ground_truth.csv</b>: Subset of ground truth data specifically for the repositories/timeframes in config1-4.json

<b>optimize_harness.py</b>: Script to test multiple weight sets (from weightsets.csv) across multiple configs (config1-4.json) using caching, comparing against optimize_ground_truth.csv to find the best weights

<b>requirements.txt</b>: Lists Python dependencies needed for the project

<b>test_functions.py</b>: Script to test and display the output of individual metric calculation functions in defect_predictor.py

<b>weightsets.csv</b>: Contains different sets of weights to be tested by optimize_harness.py. Can be modified to test your own sets of weights.

<h3>Build the docker file</h3>

Create a docker image (see Dockerfile): `docker build -t defect-predictor .`

<h3>Run the project</h3>

Run the docker container from the image: `docker run --rm -it defect-predictor`

<h3>Expected Outputs</h3>

There are 3 files you can use to run the project: <b>test_functions.py</b>, <b>harness.py</b>, and <b>optimize_harness.py</b>.

The <b>test_functions.py</b> file tests all factor score-calculating functions individually and returns each score.

The <b>harness.py</b> file is provided by the teaching staff and returns the accuracy of the predictor on calculating the risk scores for a single repository/configuration (<b>config.json</b>).

The <b>optimize_harness.py</b> file returns the most optimized set of weights in a given list of sets (<b>weightsets.csv</b>) by calculating the average accuracy of the predictor on multiple repositories/configurations (<b>config1.json</b> ~ <b>config4.json</b>). This set of weights can then be used in <b>bug_detection.py</b> to produce the most accurate results.

To run them, open the <b>Dockerfile</b> and change the last CMD line to `CMD ["python", "<file-name>"]` with the file-name being the one of the three files you want to run.

<h3>Login and run (if needed)</h3>

If your changes are not being implemented when building the docker image, you might need to run a bash shell in it: `docker run -it --rm defect-predictor bash`

Inside the bash, install nano: `apt-get install nano`

Then you can update whatever file you need to (such as config.json) with: `nano <file-name>`
