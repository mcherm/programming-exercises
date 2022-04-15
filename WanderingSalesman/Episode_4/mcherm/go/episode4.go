package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"math/big"
	"time"
)

type CacheKey struct {
	startPosition string
	numSteps      int
}

var mapData interface{}
var nodeToPosition map[string]int

func readMap() interface{} {
	// Read file
	fileData, err := ioutil.ReadFile("inputdata/small-map.json")
	if err != nil {
		log.Fatal("Error reading file: ", err)
	}

	// Parse as JSON
	var mapData interface{}
	err = json.Unmarshal(fileData, &mapData)
	if err != nil {
		log.Fatal("Error during Unmarshal(): ", err)
	}

	// return result
	return mapData
}

// Reads mapData to find the neighbors of a given node.
func getNeighbors(node string) []string {
	neighborMap := mapData.(map[string]interface{})["neighbors"]
	neighborList := neighborMap.(map[string]interface{})[node]
	neighborIs := neighborList.([]interface{})

	var result = make([]string, len(neighborIs))
	for i, v := range neighborIs {
		result[i] = v.(string)
	}

	return result
}

// Reads mapData to find the list of nodes.
func getNodes() []string {
	nodesI := mapData.(map[string]interface{})["nodes"]
	nodeIs := nodesI.([]interface{})
	var result = make([]string, len(nodeIs))
	for i, v := range nodeIs {
		result[i] = v.(string)
	}
	return result
}

// Returns the number of paths in the map mapData that begin at node startPosition
// and have numSteps items.
func countPaths(startPosition string, numSteps int) big.Int {
	// --- Initialize counts to all ones ---
	nodes := getNodes()
	numNodes := len(nodes)
	counts := make([]big.Int, numNodes)
	for i := 0; i < numNodes; i++ {
		counts[i].Set(big.NewInt(1))
	}

	// --- loop through pathLengths, updating counts each time ---
	for pathLength := 1; pathLength < numSteps; pathLength++ {
		// --- find the nextCounts ---
		nextCounts := make([]big.Int, numNodes)
		for nodeNum := 0; nodeNum < numNodes; nodeNum++ {
			nextCounts[nodeNum].Set(big.NewInt(0))
			neighbors := getNeighbors(nodes[nodeNum])
			for _, neighbor := range neighbors {
				pathsFromHere := counts[nodeToPosition[neighbor]]
				nextCounts[nodeNum].Add(&nextCounts[nodeNum], &pathsFromHere)
			}
		}

		// --- copy it into counts ---
		for nodeNum := 0; nodeNum < numNodes; nodeNum++ {
			counts[nodeNum].Set(&nextCounts[nodeNum])
		}
	}

	return counts[nodeToPosition[startPosition]]
}

func main() {
	// --- initialize stuff ---
	mapData = readMap()
	nodeToPosition = make(map[string]int)
	nodes := getNodes()
	for position, node := range nodes {
		nodeToPosition[node] = position
	}

	// --- Perform the work ----
	startPosition := "A"
	numSteps := 300000
	start := time.Now()
	numPaths := countPaths(startPosition, numSteps)
	duration := time.Since(start)

	// --- Create output ---
	fmt.Printf("In %d steps there are %s paths.\n", numSteps, numPaths.Text(10))
	fmt.Printf("Took %f seconds.\n", duration.Seconds())
}
