#
# Solution to the Wandering Salesman problem, Episode 2.
#
# Same design, but we'll skip creating the paths and just count them.
#

import json

map_data = json.load(open("../../../inputdata/small-map.json"))

def count_paths(start_position, num_steps):
    """Returns a count of paths."""
    if num_steps < 1:
        raise ValueError("Path must have at least one step.")
    elif num_steps == 1:
        return 1
    else:
        return sum(count_paths(neighbor, num_steps - 1) for neighbor in map_data["neighbors"][start_position])

path_count = count_paths("A", 15)
print(f"There are {path_count} paths.")
