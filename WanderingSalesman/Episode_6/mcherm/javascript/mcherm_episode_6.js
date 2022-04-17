//
// Implementation of Episode 5 of the Wandering Salesman problem
//

import * as fs from 'fs';

const mapData = JSON.parse(fs.readFileSync("../../../inputdata/small-map.json"));

/*
 * Calculates m1 x m2 and returns that matrix.
 */
async function matrixMultiply(m1, m2) {
    // --- Initialize a blank array for the results ---
    const SIZE = m1.length;
    const result = [];
    for (let i = 0; i < SIZE; i++) {
        result[i] = [];
    }

    // --- Perform matrix multiplication ---
    const rowPromises = [];
    for (let i = 0; i < SIZE; i++) {
        rowPromises[i] = new Promise((resolve) => {
            for (let j = 0; j < SIZE; j++) {
                let value = BigInt(0);
                for (let k = 0; k < SIZE; k++) {
                    value += m1[i][k] * m2[k][j];
                }
                result[i][j] = value;
            }
            resolve();
        });
    }
   await Promise.all(rowPromises);

    // --- Return the result ---
    return result;
}


/*
 * Returns the sum of row rowNum of the matrix.
 */
function sumRow(matrix, rowNum) {
    return matrix[rowNum].reduce((x, y) => x + y, BigInt(0));
}



async function countPaths(startNode, length) {
    // --- Read map as adjacencyMatrix ---
    const adjacencyMatrix = mapData.adjacencyMatrix.map(row => row.map(x => BigInt(x)));
    const SIZE = adjacencyMatrix.length;
    let powerOfTwoMatrix = adjacencyMatrix;

    // --- The matrix to the power starts out as an identity matrix ---
    let resultMatrix = Array.from({length: SIZE}, (x,i) => Array.from({length: SIZE}, (x,j) => i === j ? BigInt(1) : BigInt(0)));

    // --- Multiply by adjacencyMatrix length - 1 times ---
    if (length > 1) {
        const binaryDigits = (length - 1).toString(2);
        let digitNum = binaryDigits.length - 1;
        while(true) { // will reduce digitNum each time through and exit when digitNum === 0
            const binaryDigit = binaryDigits[digitNum];

            if (binaryDigit === "1") {
                resultMatrix = await matrixMultiply(resultMatrix, powerOfTwoMatrix);
            }

            if (digitNum === 0) {
                break;
            }
            digitNum -= 1;
            powerOfTwoMatrix = await matrixMultiply(powerOfTwoMatrix, powerOfTwoMatrix);
        }
    }

    // --- The result is the sum of the appropriate row ---
    const rowNum = mapData.nodes.indexOf(startNode);
    return sumRow(resultMatrix, rowNum);
}


const pathLen = 2500000;
const startTime = performance.now();
const pathCount = await countPaths("A", pathLen);
const endTime = performance.now();
console.log(`For length ${pathLen} there are ${pathCount} paths.`);
const seconds = (endTime - startTime) / 1000;
console.log(`That took ${seconds} seconds.`);
