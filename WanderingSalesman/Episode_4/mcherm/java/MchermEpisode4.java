import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.math.BigInteger;

/**
 * Code for Episode 4 of the Wandering Salesman problem.
 *
 * Transform it to be iterative instead of recursive.
 */
public class MchermEpisode4 {

    private static JsonNode mapData;
    private static Map<String, Integer> nodeToPosition;


    private static BigInteger countPaths(String startNode, int pathLength) {
        // --- Initialize the counts for paths of size 1 ---
        final int numNodes = mapData.get("nodes").size();
        BigInteger[] counts = new BigInteger[numNodes];
        Arrays.fill(counts, BigInteger.ONE);

        // --- Increment according to the pathLength ---
        for (int i=1; i<pathLength; i++) {
            BigInteger[] nextCounts = new BigInteger[numNodes];
            int nodePos = 0;
            for (final JsonNode node : mapData.get("nodes")) {
                BigInteger pathCount = BigInteger.ZERO;
                for (final JsonNode neighbor : mapData.get("neighbors").get(node.asText())) {
                    final BigInteger continuedPaths = counts[nodeToPosition.get(neighbor.asText())];
                    pathCount = pathCount.add(continuedPaths);
                }
                nextCounts[nodePos] = pathCount;
                nodePos++;
            }
            counts = nextCounts;
        }

        // --- Return the correct result ---
        return counts[nodeToPosition.get(startNode)];
    }


    public static void main(String[] args) throws Exception {
        // --- Read the map data ---
        final FileInputStream in = new FileInputStream("./WanderingSalesman/inputdata/small-map.json");
        mapData = new ObjectMapper().readTree(in);

        // --- Initialize nodeToPosition ---
        nodeToPosition = new HashMap<>();
        int pos = 0;
        for (final JsonNode jsonNode : mapData.get("nodes")) {
            nodeToPosition.put(jsonNode.asText(), pos);
            pos++;
        }

        // --- call the function and print results ---
        int pathLength = 200000;
        final long startTime = System.currentTimeMillis();
        BigInteger pathCount = countPaths("A", pathLength);
        final long endTime = System.currentTimeMillis();
        System.out.println("Considering paths of length " + pathLength + ", there are " + pathCount + " of them.");
        final long seconds = (endTime - startTime) / 1000;
        System.out.println("Taking " + seconds + " seconds.");
    }
}
