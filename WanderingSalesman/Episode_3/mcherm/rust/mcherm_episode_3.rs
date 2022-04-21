use std::fs;
use std::collections::HashMap;
use serde::Deserialize;
use serde_json;
use rug::Integer;


//
// This will use memoization.
//
// Look, I COULD just add "use cached::proc_macro::cached;" and then stick
// "#[cached]" in front of my function. But then you wouldn't get to see
// how it works. So we'll code this by hand.
//


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

type Cache = HashMap<(String,u32),Integer>;


/// Given a map and a start position and number of steps, this returns the count
/// of paths of that length starting from that location.
fn count_paths<'a>(cache: &mut Cache, map: &'a MapDescription, start_position: Node<'a>, num_steps: u32) -> Integer {
    let key = (start_position.to_string(), num_steps);
    let cache_result = cache.get(&key);
    match cache_result {
        Some(cached_value) => cached_value.clone(),
        None => {
            let answer = match num_steps {
                1 => Integer::from(1),
                _ => {
                    let neighbors = map.neighbors.get(start_position).unwrap();
                    neighbors.iter().map(|n| count_paths(cache, map, n, num_steps - 1)).sum()
                },
            };
            cache.insert(key, answer.clone());
            answer
        },
    }
}



fn main() -> Result<(), Box<dyn std::error::Error>> {
    let map = load_map_description()?;
    let mut cache: Cache = HashMap::new();

    let path_len = 6088;
    let path_count = count_paths(&mut cache, &map, &"A", path_len);
    println!("For length {} there are {} paths.", path_len, path_count);
    return Ok(());
}
