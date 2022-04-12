#
# Solution to the Wandering Salesman problem, Episode 1.
#
# Basic design: recursively visit each neighbor, constructing the path.
#

import json

map_data = json.load(open("../../../inputdata/small-map.json"))

def paths(start_position, num_steps):
    """Returns a list of paths."""
    if num_steps < 1:
        return ValueError("Path must have at least one step.")
    elif num_steps == 1:
        return [[start_position]]
    else:
        return [
            [start_position] + path
            for neighbor in map_data["neighbors"][start_position]
            for path in paths(neighbor, num_steps - 1)
        ]

path_list = paths("A", 13)
print(path_list)
print(f"There are {len(path_list)} paths.")
