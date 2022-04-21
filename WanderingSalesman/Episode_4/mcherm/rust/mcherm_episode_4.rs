use std::fs;
use std::collections::HashMap;
use serde::Deserialize;
use serde_json;
use rug::Integer;
use std::time::Instant;


#[derive(Deserialize)]
struct MapDescription {
    nodes: Vec<String>,
    neighbors: HashMap<String,Vec<String>>,
}


fn load_map_description() -> Result<MapDescription, Box<dyn std::error::Error>> {
    let s = fs::read_to_string("inputdata/small-map.json")?;
    let answer = serde_json::from_str(&s)?;
    return Ok(answer);
}



type Node<'a> = &'a str;


/// Given a map and a start position and number of steps, this returns the count
/// of paths of that length starting from that location.
fn count_paths<'a>(map: &'a MapDescription, start_position: Node<'a>, num_steps: u32) -> Integer {
    // --- Start with steps_from for paths of length 1 ---
    let mut steps_from: HashMap<Node<'a>,Integer> = HashMap::new();
    let mut path_len: u32 = 1; // The length that steps_from is valid for
    for node in map.nodes.iter() {
        steps_from.insert(node, Integer::from(1));
    }

    // --- Keep stepping forward steps_from to the next path_len until reaching num_steps ---
    while path_len < num_steps {
        let mut next_steps_from: HashMap<Node<'a>,Integer> = HashMap::new();
        for node in map.nodes.iter() {
            let mut steps_from_this_node = Integer::ZERO;
            let neighbors = map.neighbors.get(node).unwrap();
            for neighbor in neighbors {
                steps_from_this_node += steps_from.get(neighbor.as_str()).unwrap();
            }
            next_steps_from.insert(node, steps_from_this_node);
        }
        steps_from = next_steps_from;
        path_len += 1;
    }

    // --- Return the result ---
    steps_from.get(start_position).unwrap().clone()
}



fn main() -> Result<(), Box<dyn std::error::Error>> {
    let map = load_map_description()?;

    let path_len = 300000;
    let start_time = Instant::now();
    let path_count = count_paths(&map, &"A", path_len);
    let execute_seconds = start_time.elapsed().as_secs();
    println!("For length {} there are {} paths.", path_len, path_count);
    println!("Taking {} seconds.", execute_seconds);
    return Ok(());
}
