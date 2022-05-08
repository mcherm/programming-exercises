#
# Solution to the Wandering Salesman problem, Episode 5, using libraries.
#
# Change to an iterative design
#

import json
import time
import numpy

map_data = json.load(open("../../../inputdata/small-map.json"))

def count_paths(start_position, num_steps):
    # --- initialize matrix ---
    matrix = numpy.array(map_data["adjacencyMatrix"], dtype='object')

    # --- perform powers ---
    powerMatrix = numpy.linalg.matrix_power(matrix, num_steps - 1)

    # --- sum the slice ---
    start_num = map_data["nodes"].index(start_position)
    return sum(powerMatrix[start_num])


path_len = 500000
start_time = time.time()
path_count = count_paths("A", path_len)
end_time = time.time()
print(f"For paths of length {path_len} there are {path_count} paths.")
seconds = end_time - start_time
print(f"Taking {seconds} seconds.")
