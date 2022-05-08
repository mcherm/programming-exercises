#
# Solution to the Wandering Salesman problem, Episode 5, without libraries.
#
# Change to an iterative design
#

import json
import time


map_data = json.load(open("../../../inputdata/small-map.json"))


def identity_matrix(size):
    """Returns a square identity matrix of the given size."""
    return [
        [
            1 if col == row else 0
            for col in range(size)
        ]
        for row in range(size)
    ]


def matrix_multiply(m1, m2):
    """Given two square matrices of the same size, this returns m1 x m2."""
    size = len(m1)
    assert size == len(m2)
    return [
        [
            sum(m1[row][k] * m2[k][col] for k in range(size))
            for col in range(size)
        ]
        for row in range(size)
    ]


def matrix_power(matrix, power):
    """Given a square matrix, this returns that matrix to the given (positive integer) power."""
    size = len(matrix)
    result = identity_matrix(size)

    matrix_to_power_of_two = matrix

    # --- Loop through the binary digits (backward) ---
    binary_digits = f"{power:b}"
    for digit in reversed(binary_digits):
        if digit == "1":
            result = matrix_multiply(result, matrix_to_power_of_two)
        matrix_to_power_of_two = matrix_multiply(matrix_to_power_of_two, matrix_to_power_of_two)

    return result


def count_paths(start_position, num_steps):
    # --- initialize matrix ---
    matrix = map_data["adjacencyMatrix"]

    # --- perform powers ---
    powerMatrix = matrix_power(matrix, num_steps - 1)

    # --- sum the slice ---
    start_num = map_data["nodes"].index(start_position)
    return sum(powerMatrix[start_num])


path_len = 300000
start_time = time.time()
path_count = count_paths("A", path_len)
end_time = time.time()
print(f"For paths of length {path_len} there are {path_count} paths.")
seconds = end_time - start_time
print(f"Taking {seconds} seconds.")
