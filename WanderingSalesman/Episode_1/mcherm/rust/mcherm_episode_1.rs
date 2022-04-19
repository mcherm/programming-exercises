use std::fs;
use std::collections::HashMap;
use serde::Deserialize;
use serde_json;


#[derive(Deserialize)]
struct MapDescription {
    neighbors: HashMap<String,Vec<String>>
}


fn load_map_description() -> Result<MapDescription, Box<dyn std::error::Error>> {
    let s = fs::read_to_string("../../../inputdata/small-map.json")?;
    let answer = serde_json::from_str(&s)?;
    return Ok(answer);
}


type Node<'a> = &'a str;
type Path<'a> = Vec<Node<'a>>;


/// Given a map and a start position and number of steps, this returns all
/// paths of that length starting from that location.
fn get_paths<'a>(map: &'a MapDescription, start_position: Node<'a>, num_steps: u32) -> Vec<Path<'a>> {
    match num_steps {
        1 => vec![vec![start_position.clone()]],
        _ => {
            let mut answer = vec![];
            let neighbors = map.neighbors.get(start_position).unwrap();
            for neighbor in neighbors {
                let paths_from_here: Vec<Path> = get_paths(map, neighbor, num_steps - 1);
                for mut path_from_here in paths_from_here {
                    path_from_here.insert(0, start_position);
                    answer.push(path_from_here);
                }
            }
            answer
        },
    }
}


fn main() -> Result<(), Box<dyn std::error::Error>> {
    let map = load_map_description()?;

    let path_len = 14;
    let paths: Vec<Path> = get_paths(&map, &"A", path_len);
    println!("The paths are: {:?}", paths);
    println!("For length {} there are {} paths.", path_len, paths.len());
    return Ok(());
}
