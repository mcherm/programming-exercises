#
# Solution to the Wandering Salesman problem, Episode 4.
#
# Change to an iterative design
#

import json
import numpy

map_data = json.load(open("../../../inputdata/small-map.json"))

def count_paths(start_position, num_steps):
    # --- initialize length one paths ---
    paths_from_here = {node: 1 for node in map_data["nodes"]}
    # --- loop through the steps ---
    for steps in range(num_steps - 1):
        paths_from_here = {
            node: sum(paths_from_here[neighbor] for neighbor in map_data["neighbors"][node])
            for node in map_data["nodes"]
        }
    # --- return the right answer ---
    return paths_from_here[start_position]


path_len = 200000
start_time = time.time()
path_count = count_paths("A", path_len)
end_time = time.time()
print(f"For paths of length {path_len} there are {path_count} paths.")
seconds = end_time - start_time
print(f"Taking {seconds} seconds.")
