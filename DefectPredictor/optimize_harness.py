from datetime import datetime
from defect_predictor_parametrized import PythonMiner
import os
import json
import pandas as pd
from collections import defaultdict


def compare_with_ground_truth(predicted_files, ground_truth_file, config_url, config_start_date, config_end_date):
    """
    Compares the output of defect predictor model with the ground truth CSV file filtered by URL and dates.
    """
    # Load ground truth
    df = pd.read_csv(ground_truth_file)
    
    # Convert date columns in ground truth to datetime (YYYY-MM-DD format)
    df['from_date'] = pd.to_datetime(df['from_date'], format='%Y-%m-%d')  
    df['to_date'] = pd.to_datetime(df['to_date'], format='%Y-%m-%d')      
    
    # Convert config.json dates to datetime (YYYY-MM-DD)
    config_from = pd.to_datetime(config_start_date, format='%Y-%m-%d') 
    config_to = pd.to_datetime(config_end_date, format='%Y-%m-%d')  
    
    # Filter ground truth entries by URL and date range
    mask = (
        (df['github_url'] == config_url) &
        (config_from == df['from_date']) &
        (config_to == df['to_date'])
    )
    df_filtered = df[mask]
    
    # Collect all modified files from filtered entries
    ground_truth_files = set()
    for file_list in df_filtered['risky_files']:
        files = [os.path.basename(f.strip()) for f in file_list.split(',')]
        ground_truth_files.update(files)
    
    # Flatten predicted files dictionary to get a set of modified file paths
    predicted_files_set = set()
    for file_list in predicted_files.values():
        predicted_files_set.update([os.path.basename(f) for f in file_list])
    
    # Calculate true positives, false positives, and false negatives
    true_positives = predicted_files_set.intersection(ground_truth_files)
    false_positives = predicted_files_set - ground_truth_files
    false_negatives = ground_truth_files - predicted_files_set
    
    # Compute evaluation metrics
    accuracy = len(true_positives) / (len(true_positives) + len(false_positives) + len(false_negatives)) if (len(true_positives) + len(false_positives) + len(false_negatives)) > 0 else 0
    
    return accuracy


def calculate_risk_from_cached(cached_metrics, w1, w2, w3, w4, w5, w6):
    """
    Calculates risk scores using pre-calculated and normalized metrics.

    Parameters
    ----------
    cached_metrics : Dict[str, Dict[str, float]]
        Nested dictionary with file paths as keys and inner dicts containing
        normalized metric scores ('recency', 'frequency', etc.).
    w1..w6 : float
        Weights for the respective metrics.

    Returns
    -------
    Dict[str, float]
        A dictionary mapping file paths to their calculated risk scores.
    """
    risk_scores = defaultdict(float)
    for file_path, metrics in cached_metrics.items():
        r = metrics.get('recency', 0.0)
        f = metrics.get('frequency', 0.0)
        m = metrics.get('modified_lines', 0.0)
        loc = metrics.get('loc', 0.0)
        c = metrics.get('complexity', 0.0)
        mc = metrics.get('minor_contributors', 0.0)

        risk_score = (w1 * r) + (w2 * f) + (w3 * m) + (w4 * loc) + (w5 * c) + (w6 * mc)
        risk_scores[file_path] = risk_score

    return dict(risk_scores) # Convert back to regular dict


def optimize_weights():
    """
    Runs the PythonMiner with different sets of weights on 4 different configurations, then calculates the average accuracy for each set and finds the best set.
    """

    # Load weight sets file
    weight_sets_file = "weightsets.csv"
    ground_truth_file = "optimize_ground_truth.csv"
    
    # Load configuration from config.json
    config_files = ["config1.json", "config2.json", "config3.json", "config4.json"]
    
    df_weights = pd.read_csv(weight_sets_file)

    metrics_cache = {} # Cache to store metrics for each config file

    # --- Phase 1: Calculate and Cache Metrics for each Config ---
    print("--- Phase 1: Calculating and Caching Metrics ---")
    for config_file in config_files:
        print(f"\nProcessing Config: {config_file}...")
        try:
            with open(config_file, "r") as file:
                config = json.load(file)

            # Initialize the PythonMiner class
            print("  Initializing Miner and calculating metrics...")
            miner = PythonMiner(config)

            # Calculate and store normalized metrics in the cache
            normalized_metrics = miner.get_normalized_metrics()
            if normalized_metrics:
                metrics_cache[config_file] = {
                    "metrics": normalized_metrics,
                    "config": config # Store config details for comparison later
                }
                print(f"  Metrics calculated and cached for {len(normalized_metrics)} files.")
            else:
                 print(f"  No metrics could be calculated for {config_file}. Skipping.")
                 metrics_cache[config_file] = None # Mark as None if no metrics

        except FileNotFoundError:
            print(f"  Error: Config file {config_file} not found. Skipping.")
            metrics_cache[config_file] = None
        except Exception as e:
            print(f"  Error processing {config_file}: {e}. Skipping.")
            metrics_cache[config_file] = None # Mark as None on error

    # --- Phase 2: Test Weight Sets using Cached Metrics ---
    print("\n\n--- Phase 2: Testing Weight Sets ---")

    best_avg_accuracy = 0
    best_weights = None
    results = []
    
    # Iterate through each row (weight set) in the CSV
    for index, weights in df_weights.iterrows():
        w1, w2, w3, w4, w5, w6 = weights['w1'], weights['w2'], weights['w3'], weights['w4'], weights['w5'], weights['w6']
        current_weights = {'w1': w1, 'w2': w2, 'w3': w3, 'w4': w4, 'w5': w5, 'w6': w6}
        print(f"\nTesting Weights (Set {index + 1}): {current_weights}")

        accuracies_for_this_set = []

        # Iterate through each configuration file
        for config_file, cached_data in metrics_cache.items():
            if cached_data is None:
                continue # Skip configs that failed in Phase 1

            cached_metrics = cached_data["metrics"]
            config = cached_data["config"]
            config_url = config['url_to_repo']
            config_start_date = config['from_date']
            config_end_date = config['to_date']
    
            # Initialize the PythonMiner class with the configurations
            miner = PythonMiner(config)

            # Calculate risk scores using cached metrics and current weights
            risk_scores = calculate_risk_from_cached(cached_metrics, w1, w2, w3, w4, w5, w6)

            if risk_scores:
                # Get top 3 risky files (basenames)
                top_3_risky = sorted(risk_scores.items(), key=lambda x: x[1], reverse=True)[:3]
                top_3_files = {os.path.basename(file) for file, _ in top_3_risky}
                
                # Compare with ground truth
                accuracy = compare_with_ground_truth(
                    {"predicted": list(top_3_files)},  
                    ground_truth_file,
                    config_url,
                    config_start_date,
                    config_end_date
                )

                if accuracy is not None:
                    print(f"    Accuracy for {config_file}: {accuracy:.2f}")
                    accuracies_for_this_set.append(accuracy)
                else:
                    print(f"    No accuracy found for {config_file}")
            
            else:
                print("\nNo risk scores calculated.")

        if accuracies_for_this_set:
            avg_accuracy = sum(accuracies_for_this_set) / len(accuracies_for_this_set)
            print(f"Average Accuracy for Weight Set {index + 1}: {avg_accuracy:.2f}")
            
            # Check if this is the best average accuracy found so far
            if avg_accuracy > best_avg_accuracy:
                best_avg_accuracy = avg_accuracy
                best_weights = current_weights

            results.append({"weights": current_weights, "avg_accuracy": avg_accuracy})

        else:
            print(f"    No valid accuracy scores calculated for Weight Set {index + 1}.")
            results.append({"weights": current_weights, "avg_accuracy": 0.0})

    print("\n\n--- Optimization Complete ---\n")

    # Print summary of results
    print("Summary of Average Accuracies per Weight Set:")
    for result in sorted(results, key=lambda x: x['avg_accuracy'], reverse=True):
        print(f"  Weights: {result['weights']} -> Avg Accuracy: {result['avg_accuracy']:.2f}")

    # Print the best result
    if best_weights:
        
        print("\nBest Performing Weight Set:")
        print(f"  Weights: {best_weights}")
        print(f"  Highest Average Accuracy: {best_avg_accuracy:.2f}")
    else:
        print("\nNo best weights found.")

# Execute the optimize_weight function
if __name__ == "__main__":
    optimize_weights()