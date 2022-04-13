import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Code for Episode 1 of the Wandering Salesman problem.
 *
 * The strategy we will use is to construct the path recursively.
 */
public class MchermEpisode1 {

    private static JsonNode mapData;

    private static List<List<String>> getPaths(String startNode, int pathLength) {
        if (pathLength == 1) {
            final List<String> path = Arrays.asList(startNode);
            return Arrays.asList(path);
        } else {
            final List<List<String>> result = new ArrayList<>();
            for (final JsonNode neighbor : mapData.get("neighbors").get(startNode)) {
                final List<List<String>> continuedPaths = getPaths(neighbor.asText(), pathLength - 1);
                for (final List<String> continuedPath : continuedPaths) {
                    final List<String> path = new ArrayList<String>(continuedPath.size() + 1);
                    path.add(startNode);
                    path.addAll(continuedPath);
                    result.add(path);
                }
            }
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        // --- Read the map data ---
        final FileInputStream in = new FileInputStream("./WanderingSalesman/inputdata/small-map.json");
        mapData = new ObjectMapper().readTree(in);

        // --- call the function and print results ---
        int pathLength = 14;
        List<List<String>> paths = getPaths("A", pathLength);
        System.out.println(paths);
        System.out.println("Considering paths of length " + pathLength + ", there are " + paths.size() + " of them.");
    }
}
