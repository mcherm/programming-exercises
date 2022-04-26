use std::fs;
use serde::Deserialize;
use serde_json;
use rug::Integer;
use std::time::Instant;
use rayon::prelude::*;


#[allow(non_snake_case)]
#[derive(Deserialize)]
struct MapDescription {
    nodes: Vec<String>,
    adjacencyMatrix: Vec<Vec<u8>>,
}


fn load_map_description() -> Result<MapDescription, Box<dyn std::error::Error>> {
    let s = fs::read_to_string("inputdata/small-map.json")?;
    let answer = serde_json::from_str(&s)?;
    return Ok(answer);
}




type Matrix = Vec<Vec<Integer>>;
type Node<'a> = &'a str;

/// Performs a matrix multiply of m1 and m2.
fn matrix_multiply(m1: &Matrix, m2: &Matrix) -> Matrix {
    assert!(m1.len() == m1[0].len() && m1.len() == m2.len());
    let size = m1.len();

    let answer = (0..size).into_par_iter().map(|i| {
        (0..size).map(|j| {
            (0..size).map(|k| &m1[i][k] * &m2[k][j]).sum()
        }).collect()
    }).collect();

    // --- Return the result ---
    answer
}


/// Puts a given matrix to a power (efficiently).
fn matrix_power(m: &Matrix, power: u32) -> Matrix {
    // --- Track input matrix to powers of two ---
    let mut power_of_two = m.clone();

    // --- Start with an identity matrix (for length-1 paths you just sit on start_position) ---
    let mut output_matrix = identity_matrix(m.len());

    // --- Go through powers of 2, multiplying them in if the digit is a 1 ---
    let binary_digits = format!("{:b}", power);
    for binary_digit in binary_digits.chars().rev() {
        if binary_digit == '1' {
            output_matrix = matrix_multiply(&output_matrix, &power_of_two);
        }

        power_of_two = matrix_multiply(&power_of_two, &power_of_two);
    }

    // --- Return it ---
    output_matrix
}


/// Creates and returns an identity matrix (1's along the diagonal) of the given size.
fn identity_matrix(size: usize) -> Matrix {
    (0..size).map(|i| {
        (0..size).map(|j| {
            if i == j {Integer::from(1)} else {Integer::ZERO}
        }).collect()
    }).collect()
}


impl MapDescription {
    fn get_adj_matrix(&self) -> Matrix {
        self.adjacencyMatrix.iter().map(|row| {
            row.iter().map(|item| Integer::from(*item)).collect()
        }).collect()
    }

    fn node_to_num(&self, node: Node) -> Option<usize> {
        self.nodes.iter().position(|x| x == node)
    }
}


/// Given a map and a start position and number of steps, this returns the count
/// of paths of that length starting from that location.
fn count_paths<'a>(map: &'a MapDescription, start_position: Node<'a>, num_steps: u32) -> Integer {
    let adj_matrix = map.get_adj_matrix();

    // --- Put the matrix to a power ---
    let output_matrix = matrix_power(&adj_matrix, num_steps - 1);

    // --- Sum up the correct row and return that ---
    output_matrix[map.node_to_num(start_position).unwrap()].iter().sum()
}


fn main() -> Result<(), Box<dyn std::error::Error>> {
    let map = load_map_description()?;

    let path_len = 10000000;
    let start_time = Instant::now();
    let path_count = count_paths(&map, &"A", path_len);
    let execute_seconds = start_time.elapsed().as_secs();
    println!("For length {} there are {} paths.", path_len, path_count);
    println!("Taking {} seconds.", execute_seconds);
    return Ok(());
}
