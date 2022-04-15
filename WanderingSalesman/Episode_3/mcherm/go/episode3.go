package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
)

type CacheKey struct {
	startPosition string
	numSteps      int
}

var mapData interface{}
var cache map[CacheKey]int

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

// Returns the number of paths in the map mapData that begin at node startPosition
// and have numSteps items.
func countPaths(startPosition string, numSteps int) int {
	// --- check if it's in the cache ---
	var cacheKey = CacheKey{startPosition: startPosition, numSteps: numSteps}
	cachedValue, ok := cache[cacheKey]
	if ok {
		return cachedValue
	}

	// --- it's not, so find the answer ---
	var answer int
	if numSteps == 1 {
		answer = 1
	} else {
		neighbors := getNeighbors(startPosition)
		result := 0
		for _, neighbor := range neighbors {
			result += countPaths(neighbor, numSteps-1)
		}
		answer = result
	}

	// --- add to cache and return ---
	cache[cacheKey] = answer
	return answer
}

func main() {
	mapData = readMap()
	cache = make(map[CacheKey]int)

	startPosition := "A"
	numSteps := 36
	numPaths := countPaths(startPosition, numSteps)

	// Perform work
	fmt.Printf("In %d steps there are %d paths.", numSteps, numPaths)
}
