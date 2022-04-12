#
# Solution to the Wandering Salesman problem, Episode 3.
#
# Same design, but we'll add caching.
#

import json

map_data = json.load(open("../../../inputdata/small-map.json"))

count_paths_cache = {}

def count_paths(start_position, num_steps):
    """Returns a count of paths."""
    # --- See if it's in the cache ---
    cached_result = count_paths_cache.get((start_position, num_steps), None)
    if cached_result is not None:
        return cached_result
    # --- Find the answer ---
    if num_steps < 1:
        raise ValueError("Path must have at least one step.")
    elif num_steps == 1:
        answer = 1
    else:
        answer = sum(count_paths(neighbor, num_steps - 1) for neighbor in map_data["neighbors"][start_position])
    # --- Cache it and return it ---
    count_paths_cache[(start_position, num_steps)] = answer
    return answer


path_len = 1
while True:
    path_count = count_paths("A", path_len)
    print(f"For paths of length {path_len} there are {path_count} paths.")
    count_paths_cache = {}
    path_len += 1
