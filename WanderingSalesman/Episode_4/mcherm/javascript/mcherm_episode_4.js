//
// Implementation of Episode 4 of the Wandering Salesman problem
//

const fs = require('fs')

const mapData = JSON.parse(fs.readFileSync("../../../inputdata/small-map.json"));

function countPaths(startNode, length) {
    // --- make a list of counts for each node (for length 1) ---
    let counts = {};
    for (const node of mapData.nodes) {
        counts[node] = BigInt(1);
    }

    // --- find the next level of counts as many times as needed ---
    for (let i = 0; i < length - 1; i++) {
        const newCounts = {}
        for (const node of mapData.nodes) {
            let newCount = BigInt(0);
            for (const neighbor of mapData.neighbors[node]) {
                newCount += counts[neighbor];
            }
            newCounts[node] = newCount;
        }
        counts = newCounts;
    }

    // --- Return the correct answer ---
    return counts[startNode];
}


const pathLen = 300000;
const startTime = performance.now();
const pathCount = countPaths("A", pathLen);
const endTime = performance.now();
console.log(`For length ${pathLen} there are ${pathCount} paths.`);
const seconds = (endTime - startTime) / 1000;
console.log(`That took ${seconds} seconds.`);
