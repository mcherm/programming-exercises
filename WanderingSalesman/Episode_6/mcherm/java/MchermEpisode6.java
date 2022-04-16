import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Code for Episode 6 of the Wandering Salesman problem.
 *
 * Use threads.
 */
public class MchermEpisode6 {

    private static JsonNode mapData;
    private static Map<String, Integer> nodeToPosition;
    private static ExecutorService threadPool;


    private static class Matrix {
        public final int numNodes;
        public final BigInteger[][] data;

        /**
         * Make it pretty for output.
         */
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (int i = 0; i < numNodes; i++) {
                sb.append("  [");
                for (int j = 0; j < numNodes; j++) {
                    sb.append(data[i][j]);
                    sb.append(", ");
                }
                sb.append("],\n");
            }
            sb.append("]");
            return sb.toString();
        }

        /**
         * Create a numNodes x numNodes Matrix populated entirely with
         * value.
         *
         * @param numNodes the side size of this square matrix
         * @param value the single value (or null) which will be used to populate all
         *              cells of the matrix
         */
        public Matrix(final int numNodes, final BigInteger value) {
            this.numNodes = numNodes;
            data = new BigInteger[numNodes][];
            for (int i = 0; i < numNodes; i++) {
                data[i] = new BigInteger[numNodes];
                for (int j = 0; j < numNodes; j++) {
                    data[i][j] = value;
                }
            }
        }

        /**
         * Performs a matrix multiply of this with other and returns the new resulting
         * matrix.
         */
        public Matrix matrixMultiply(final Matrix other) {
            assert other.numNodes == this.numNodes;
            final Matrix result = new Matrix(numNodes, null);
            for (int i = 0; i < numNodes; i++) {
                final BigInteger[][] m1 = this.data;
                final BigInteger[][] m2 = other.data;
                final int iVal = i;
                final List<Callable<BigInteger>> tasks = new ArrayList<>();
                for (int j = 0; j < numNodes; j++) {
                    final int jVal = j;
                    tasks.add(() -> {
                        BigInteger value = BigInteger.ZERO;
                        for (int k = 0; k < numNodes; k++) {
                            value = value.add(m1[iVal][k].multiply(m2[k][jVal]));
                        }
                        return value;
                    });
                }
                final List<Future<BigInteger>> results;
                try {
                    results = threadPool.invokeAll(tasks);
                } catch(InterruptedException err) {
                    throw new RuntimeException("Thread was interrupted.");
                }
                for (int j = 0; j < numNodes; j++) {
                    try {
                        result.data[i][j] = results.get(j).get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Thread was interrupted.");
                    } catch (ExecutionException e) {
                        throw new RuntimeException("Exception doing multiply.");
                    }
                }
            }
            return result;
        }

        /**
         * Returns the sum of items in the given row.
         */
        public BigInteger sumOfRow(final int row) {
            BigInteger result = BigInteger.ZERO;
            for (int i = 0; i < numNodes; i++) {
                result = result.add(data[row][i]);
            }
            return result;
        }

        /**
         * Calculates this matrix to a power, using binary exponentiation.
         *
         * DESIGN: We will express power in binary, then repeatedly square this matrix to get
         * powers of two. We'll add together the powers of two that are 1s in the binary
         * number and that gives our answer.
         */
        public Matrix matrixPower(final int power) {
            // --- Create an identity matrix ---
            Matrix result = new Matrix(numNodes, BigInteger.ZERO);
            for (int i = 0; i < numNodes; i++) {
                result.data[i][i] = BigInteger.ONE;
            }

            // --- Set up to calculate <this> to various powers of two by repeated squaring ---
            int powerOfTwo = 1;
            Matrix matrixToPowerOfTwo = this;

            // --- Determine the binary digits of power ---
            final String binaryDigits = Integer.toBinaryString(power);

            // --- Loop through the binary digits ---
            while (true) { // will exit the loop when powerOfTwo reaches the length of the string
                final char binaryDigit = binaryDigits.charAt(binaryDigits.length() - powerOfTwo);
                if (binaryDigit == '1') {
                    result = result.matrixMultiply(matrixToPowerOfTwo);
                }
                powerOfTwo += 1;
                if (powerOfTwo > binaryDigits.length()) {
                    break; // break here instead of in the loop condition to avoid doing an extra matrix multiply
                }
                matrixToPowerOfTwo = matrixToPowerOfTwo.matrixMultiply(matrixToPowerOfTwo);
            }

            // --- Return the result ---
            return result;
        }
    }


    private static BigInteger countPaths(String startNode, int pathLength) {
        // --- This algorithm only works on paths of length 2 and up, so hard-code length 1 ---
        if (pathLength == 1) {
            return BigInteger.ONE;
        }

        // --- Read in the initial matrix ---
        final int numNodes = mapData.get("nodes").size();
        final Matrix adjMatrix = new Matrix(numNodes, null);
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                final String value = mapData.get("adjacencyMatrix").get(i).get(j).asText();
                adjMatrix.data[i][j] = new BigInteger(value);
            }
        }

        // --- Perform exponentiation ---
        // we go to the power of pathLength - 1 because doing sumOfRow below gives us one extra level.
        final Matrix outputMatrix = adjMatrix.matrixPower(pathLength - 1);

        // --- Return the answer for this start node ---
        return outputMatrix.sumOfRow(nodeToPosition.get(startNode));
    }


    public static void main(String[] args) throws Exception {
        // --- Set up thread pool ---
        threadPool = Executors.newFixedThreadPool(9);

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
        final int pathLength = 2000000;
        final long startTime = System.currentTimeMillis();
        BigInteger pathCount = countPaths("A", pathLength);
        final long endTime = System.currentTimeMillis();
        System.out.println("Considering paths of length " + pathLength + ", there are " + pathCount + " of them.");
        final long seconds = (endTime - startTime) / 1000;
        System.out.println("Taking " + seconds + " seconds.");

        // --- shut down the thread pools ---
        threadPool.shutdown();
    }
}
