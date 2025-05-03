from datetime import datetime
from bug_detection import PythonMiner
import os
import json
import pandas as pd

def harness_function():
    """
    A harness function to execute the PythonMiner class and get outputs.
    """
    # Load configuration from config.json
    config_file = "config.json"
    with open(config_file, "r") as file:
        config = json.load(file)
    
    # Initialize the PythonMiner class with the configurations
    miner = PythonMiner(config)
    
    # Retrieve modified Python files
    print("Retrieving modified Python files...")
    modified_files = miner.get_modified_files()

    # Flatten modified files dictionary to remove commit-level grouping
    all_predicted_files = set()
    for file_list in modified_files.values():
        all_predicted_files.update(file_list)

    print("Modified files debug output:", all_predicted_files)

    # Test calculate_recency()
    print("\n\nCalculating recency scores...")
    recency_dates = miner.calculate_recency()
    # print recency score
    print("\nRecency Scores (File Name -> Recency Value):")
    for file_name, recency_value in recency_dates.items():
        print(f"{file_name} -> {recency_value}")

    # Test calculate_frequency()
    print("\n\nCalculating frequency scores...")
    frequency_scores = miner.calculate_frequency()
    # print frequency score
    print("\nFrequency Scores (File Name -> Frequency Value):")
    for file_name, frequency_value in frequency_scores.items():
        print(f"{file_name} -> {frequency_value}")

    # Test calculate_lines_modified()
    print("\n\nCalculating modified scores...")
    modified_scores = miner.calculate_lines_modified()
    # print modified score
    print("\nModified Scores (File Name -> Modified Value):")
    for file_name, modified_value in modified_scores.items():
        print(f"{file_name} -> {modified_value}")

    # Test calculate_loc()
    print("\n\nCalculating LOC (lines of code) scores...")
    loc_scores = miner.calculate_loc()
    # print loc score
    print("\nLOC (lines of code) Scores (File Name -> LOC Value):")
    for file_name, loc_value in loc_scores.items():
        print(f"{file_name} -> {loc_value}")

    # Test calculate_cyclomatic_complexity()
    print("\n\nCalculating cyclomatic complexity scores...")
    complexity_scores = miner.calculate_cyclomatic_complexity()
    # print complexity score
    print("\nComplexity Scores (File Name -> Complexity Value):")
    for file_name, complexity_value in complexity_scores.items():
        print(f"{file_name} -> {complexity_value}")

    # Test calculate_minor_count()
    print("\n\nCalculating minor contributor scores...")
    minor_contributors = miner.calculate_minor_contributor()
    # print complexity score
    print("\nMinorCount Scores (File Name -> Minor Value):")
    for file_name, minor_value in minor_contributors.items():
        print(f"{file_name} -> {minor_value}")

    # Test calculate_risk()
    print("\n\nCalculating risk scores...")
    risk_scores = miner.calculate_risk()
    # print risk score
    print("\nRisk Scores (File Name -> Risk Value):")
    for file_name, risk_value in risk_scores.items():
        print(f"{file_name} -> {risk_value:.2f}")

    top_risky_files = sorted(risk_scores.items(), key=lambda x: x[1], reverse=True)[:3]
    print("\nTop 3 Risky Files:")
    for file_name, risk_value in top_risky_files:
        print(f"{file_name} -> {risk_value:.2f}")

# Execute the harness function
if __name__ == "__main__":
    harness_function()    
    