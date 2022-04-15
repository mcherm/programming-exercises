package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
)

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
func getNeighbors(mapData interface{}, node string) []string {
	neighborMap := mapData.(map[string]interface{})["neighbors"]
	neighborList := neighborMap.(map[string]interface{})[node]
	neighborIs := neighborList.([]interface{})

	var result = make([]string, len(neighborIs))
	for i, v := range neighborIs {
		result[i] = v.(string)
	}

	return result
}

// Returns the list of paths (each path is a slice of strings) in the map mapData
// that begin at node startPosition and have numSteps items.
func getPaths(mapData interface{}, startPosition string, numSteps int) [][]string {
	if numSteps == 1 {
		return [][]string{{startPosition}}
	} else {
		neighbors := getNeighbors(mapData, startPosition)
		result := make([][]string, 0)
		for _, neighbor := range neighbors {
			var continuedPaths [][]string = getPaths(mapData, neighbor, numSteps-1)
			for _, continuedPath := range continuedPaths {
				newPath := []string{startPosition}
				newPath = append(newPath, continuedPath...)
				result = append(result, newPath)
			}
		}
		return result
	}
}

func main() {
	var mapData = readMap()

	startPosition := "A"
	numSteps := 11
	pathList := getPaths(mapData, startPosition, numSteps)

	// Perform work
	fmt.Println("Paths:")
	fmt.Println(pathList)
	fmt.Printf("In %d steps there are %d paths.", numSteps, len(pathList))
}
