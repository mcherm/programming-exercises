//
// Implementation of Episode 1 of the Wandering Salesman problem
//

const fs = require('fs')

const mapData = JSON.parse(fs.readFileSync("../../../inputdata/small-map.json"));

function listPaths(startNode, length) {
    if (length === 1) {
        return [[startNode]]
    } else if (length === 2) {
        const paths = [];
        for (const neighbor of mapData.neighbors[startNode]) {
            paths.push( [startNode, neighbor] );
        }
        return paths;
    } else {
        const paths = [];
        for (const neighbor of mapData.neighbors[startNode]) {
            for (const path of listPaths(neighbor, length - 1)) {
                path.unshift(startNode);
                paths.push(path);
            }
        }
        return paths;
    }
}

const pathLen = 13;
const pathList = listPaths("A", pathLen);
console.log(pathList);
console.log(`For length ${pathLen} there are ${pathList.length} paths.`);

