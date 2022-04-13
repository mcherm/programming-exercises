import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;

/**
 * Code for Episode 2 of the Wandering Salesman problem.
 *
 * Here we'll just count the paths instead of creating the lists.
 */
public class MchermEpisode2 {

    private static JsonNode mapData;

    private static int countPaths(String startNode, int pathLength) {
        if (pathLength == 1) {
            return 1;
        } else {
            int result = 0;
            for (final JsonNode neighbor : mapData.get("neighbors").get(startNode)) {
                final int continuedPaths = countPaths(neighbor.asText(), pathLength - 1);
                result += continuedPaths;
            }
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        // --- Read the map data ---
        final FileInputStream in = new FileInputStream("./WanderingSalesman/inputdata/small-map.json");
        mapData = new ObjectMapper().readTree(in);

        // --- call the function and print results ---
        int pathLength = 18;
        int pathCount = countPaths("A", pathLength);
        System.out.println("Considering paths of length " + pathLength + ", there are " + pathCount + " of them.");
    }
}
