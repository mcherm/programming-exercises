//
// Implementation of Episode 2 of the Wandering Salesman problem
//

const fs = require('fs')

const mapData = JSON.parse(fs.readFileSync("../../../inputdata/small-map.json"));

function countPaths(startNode, length) {
    if (length === 1) {
        return 1
    } else {
        let count = 0;
        for (const neighbor of mapData.neighbors[startNode]) {
            count += countPaths(neighbor, length - 1);
        }
        return count;
    }
}

const pathLen = 18;
const pathCount = countPaths("A", pathLen);
console.log(`For length ${pathLen} there are ${pathCount} paths.`);

