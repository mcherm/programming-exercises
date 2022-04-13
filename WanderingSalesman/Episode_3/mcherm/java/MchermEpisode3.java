import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.math.BigInteger;

/**
 * Code for Episode 3 of the Wandering Salesman problem.
 *
 * We'll add caching.
 */
public class MchermEpisode3 {

    private static JsonNode mapData;
    private static Map<StartNodeAndPathLength, BigInteger> cache;

    /**
     * An immutable string-int tuple.
     */
    private static class StartNodeAndPathLength {
        public final String startNode;
        public final int pathLength;

        public StartNodeAndPathLength(final String startNode, final int pathLength) {
            this.startNode = startNode;
            this.pathLength = pathLength;
        }

        @Override
        public int hashCode() {
            return startNode.hashCode() ^ pathLength;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof  StartNodeAndPathLength) {
                StartNodeAndPathLength other = (StartNodeAndPathLength) obj;
                return startNode.equals(other.startNode) && pathLength == other.pathLength;
            } else {
                return false;
            }
        }
    }


    private static BigInteger countPaths(String startNode, int pathLength) {
        // --- Try doing a lookup in the cache first ---
        final StartNodeAndPathLength key = new StartNodeAndPathLength(startNode, pathLength);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        // --- Calculate the result ---
        final BigInteger answer;
        if (pathLength == 1) {
            answer = BigInteger.ONE;
        } else {
            BigInteger pathCount = BigInteger.ZERO;
            for (final JsonNode neighbor : mapData.get("neighbors").get(startNode)) {
                final BigInteger continuedPaths = countPaths(neighbor.asText(), pathLength - 1);
                pathCount = pathCount.add(continuedPaths);
            }
            answer = pathCount;
        }

        // --- Store it in the cache and return it ---
        cache.put(key, answer);
        return answer;
    }

    public static void main(String[] args) throws Exception {
        // --- Read the map data ---
        final FileInputStream in = new FileInputStream("./WanderingSalesman/inputdata/small-map.json");
        mapData = new ObjectMapper().readTree(in);
        cache = new HashMap<>();

        // --- call the function and print results ---
        int pathLength = 6510;
        BigInteger pathCount = countPaths("A", pathLength);
        System.out.println("Considering paths of length " + pathLength + ", there are " + pathCount + " of them.");
    }
}
