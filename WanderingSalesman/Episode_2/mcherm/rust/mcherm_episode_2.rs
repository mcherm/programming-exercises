use std::fs;
use std::collections::HashMap;
use serde::Deserialize;
use serde_json;


#[derive(Deserialize)]
struct MapDescription {
    neighbors: HashMap<String,Vec<String>>
}


fn load_map_description() -> Result<MapDescription, Box<dyn std::error::Error>> {
    let s = fs::read_to_string("inputdata/small-map.json")?;
    let answer = serde_json::from_str(&s)?;
    return Ok(answer);
}


type Node<'a> = &'a str;


/// Given a map and a start position and number of steps, this returns the count
/// of paths of that length starting from that location.
fn count_paths<'a>(map: &'a MapDescription, start_position: Node<'a>, num_steps: u32) -> u64 {
    match num_steps {
        1 => 1,
        _ => {
            let neighbors = map.neighbors.get(start_position).unwrap();
            neighbors.iter().map(|n| count_paths(map, n, num_steps - 1)).sum()
        },
    }
}


fn main() -> Result<(), Box<dyn std::error::Error>> {
    let map = load_map_description()?;

    let path_len = 16;
    let path_count = count_paths(&map, &"A", path_len);
    println!("For length {} there are {} paths.", path_len, path_count);
    return Ok(());
}
