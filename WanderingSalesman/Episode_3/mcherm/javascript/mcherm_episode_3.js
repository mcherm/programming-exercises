//
// Implementation of Episode 3 of the Wandering Salesman problem
//

const fs = require('fs')

const mapData = JSON.parse(fs.readFileSync("../../../inputdata/small-map.json"));

memoCache = {};

function countPaths(startNode, length) {
    // --- check if it's in the cache ---
    if (startNode in memoCache && length in memoCache[startNode]) {
        return memoCache[startNode][length];
    }
    // --- work out the answer ---
    let answer;
    if (length === 1) {
        answer = BigInt(1);
    } else {
        answer = BigInt(0);
        for (const neighbor of mapData.neighbors[startNode]) {
            answer += countPaths(neighbor, length - 1);
        }
    }
    // --- store the answer and return it ---
    if (!(startNode in memoCache)) {
        memoCache[startNode] = {};
    }
    memoCache[startNode][length] = answer;
    return answer
}


const pathLen = 5012;
const pathCount = countPaths("A", pathLen);
console.log(`For length ${pathLen} there are ${pathCount} paths.`);
