import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String args[]) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String rawEdgeInput = scanner.nextLine();
        String rawPathAndTimeInput = scanner.nextLine();

        try {
            tryToPrintShortestPath(rawEdgeInput, rawPathAndTimeInput);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void tryToPrintShortestPath(String rawEdgeInput, String rawPathAndTimeInput) throws Exception {
        ShortestPathHelper shortestPathHelper = new ShortestPathHelper(rawEdgeInput, rawPathAndTimeInput);
        ShortestPath shortestPath = new ShortestPath(shortestPathHelper.numVertices);
        shortestPath.computeShortestPath(shortestPathHelper.adjacencyList, shortestPathHelper.sourceNodeID, shortestPathHelper.sourceNode);
        if (shortestPath.distTo[shortestPathHelper.destinationNodeID] > shortestPathHelper.maxTravelTime) {
            throw new NoSuitableRouteException("E3");
        } else {
            shortestPathHelper.printShortestPath(shortestPath);
        }
    }
}

class ShortestPath {
    int[] distTo;
    Node[] edgeTo;
    private Set <Integer> settled;
    private PriorityQueue<Node> pq;
    private int numVertices;
    private List<List<Node>> adjacencyList;

    ShortestPath(int numVertices) {
        this.numVertices = numVertices;
        distTo = new int[numVertices];
        edgeTo = new Node[numVertices];
        settled = new HashSet<>();
        pq = new PriorityQueue<>(numVertices, new Node());
    }

    void computeShortestPath(List<List<Node>> adjacencyList, int sourceNodeID, String sourceNode) {
        this.adjacencyList = adjacencyList;

        for (int i = 0; i < numVertices; i++) {
            distTo[i] = Integer.MAX_VALUE;
        }

        pq.add(new Node(sourceNode, sourceNodeID, 0));
        distTo[sourceNodeID] = 0;

        while(settled.size() != numVertices) {
            Node leastCostNode = pq.remove();
            settled.add(leastCostNode.id);
            processNeighbours(leastCostNode);
        }
    }

    private void processNeighbours(Node node) {
        int edgeDistance;
        int newDistance;

        List<Node> neighbourList = adjacencyList.get(node.id);
        for (Node neighbour : neighbourList) {
            if (!settled.contains(neighbour.id)) {
                edgeDistance = neighbour.cost;
                newDistance = distTo[node.id] + edgeDistance;

                if (newDistance < distTo[neighbour.id]) {
                    distTo[neighbour.id] = newDistance;
                    edgeTo[neighbour.id] = node;
                    pq.add(new Node(neighbour.nodeName, neighbour.id, distTo[neighbour.id]));
                }
            }
        }
    }
}

class Node implements Comparator<Node> {
    String nodeName;
    int id;
    int cost;

    Node () {}

    Node(String nodeName, int id, int cost) {
        this.nodeName = nodeName;
        this.id = id;
        this.cost = cost;
    }

    @Override
    public int compare(Node node1, Node node2)
    {
        return Integer.compare(node1.cost, node2.cost);
    }
}

class ShortestPathHelper {
    private Map<String, Integer> nodeNameToID;
    int numVertices;
    String sourceNode;
    int sourceNodeID;
    private String destinationNode;
    int destinationNodeID;
    int maxTravelTime;
    List<List<Node>> adjacencyList;

    ShortestPathHelper(String rawEdgesInput, String rawRouteAndTimeInput) throws Exception {
        String[] rawEdges = getRawEdges(rawEdgesInput);
        String[][] edges = extractEdgesInformation(rawEdges);
        this.nodeNameToID = createNodeNameToIDMapping(edges);
        checkValidInput(rawEdges, edges, rawRouteAndTimeInput, nodeNameToID);
        this.numVertices = nodeNameToID.size();
        this.sourceNode = getSourceNode(rawRouteAndTimeInput);
        this.sourceNodeID = nodeNameToID.get(sourceNode);
        this.destinationNode = getDestinationNode(rawRouteAndTimeInput);
        this.destinationNodeID = nodeNameToID.get(destinationNode);
        this.maxTravelTime = getMaxTravelTime(rawRouteAndTimeInput);
        this.adjacencyList = buildAdjacencyList(edges, nodeNameToID);
    }

    private static String[] getRawEdges(String rawNodes) {
        return rawNodes.split(" ");
    }

    private static String[][] extractEdgesInformation(String[] rawEdges) {
        String [][] edges = new String[rawEdges.length][3];
        String patternToCaptureEdge = ".([A-Z]),([A-Z]),(\\d+).";
        Pattern pattern = Pattern.compile(patternToCaptureEdge);

        for (int i = 0; i < edges.length; i++) {
            Matcher matcher = pattern.matcher(rawEdges[i]);
            while(matcher.find()) {
                edges[i][0] = matcher.group(1);
                edges[i][1] = matcher.group(2);
                edges[i][2] = matcher.group(3);
            }
        }

        return edges;
    }

    private static Map<String, Integer> createNodeNameToIDMapping(String[][] edges) {
        HashMap<String, Integer> nameToIDMap = new HashMap<>();
        int IDCounter = 0;

        for (String[] edge : edges) {
            String node1 = edge[0];
            String node2 = edge[1];
            if (!nameToIDMap.containsKey(node1)) {
                nameToIDMap.put(node1, IDCounter++);
            }
            if (!nameToIDMap.containsKey(node2)) {
                nameToIDMap.put(node2, IDCounter++);
            }
        }

        return nameToIDMap;
    }

    private String getSourceNode(String rawRouteAndTimeInput) {
        return rawRouteAndTimeInput.substring(0, 1);
    }

    private String getDestinationNode(String rawRouteAndTimeInput) {
        return rawRouteAndTimeInput.substring(3, 4);
    }

    private int getMaxTravelTime(String rawRouteAndTimeInput) {
        return Integer.parseInt(rawRouteAndTimeInput.split(",")[1]);
    }

    private static List<List<Node>> buildAdjacencyList(String[][] edges, Map<String, Integer> mapNodeNameToID) {
        List<List<Node>> adjacencyList = initializeAdjacencyList(mapNodeNameToID.size());

        for (String[] edge : edges) {
            addEdgeToAdjacencyList(edge, adjacencyList, mapNodeNameToID);
        }

        return adjacencyList;
    }

    private static List<List<Node>> initializeAdjacencyList(int size) {
        List<List<Node>> adjacencyList = new ArrayList<>(size);

        for(int  i = 0; i < size; i++) {
            List<Node> newAdjacentNodesList = new ArrayList<>();
            adjacencyList.add(newAdjacentNodesList);
        }

        return adjacencyList;
    }

    private static void addEdgeToAdjacencyList(String[] edge, List<List<Node>> adjacencyList, Map<String, Integer> mapNodeNameToID) {
        String node1 = edge[0];
        String node2 = edge[1];
        int cost = Integer.parseInt(edge[2]);
        adjacencyList.get(mapNodeNameToID.get(node1)).add(new Node(node2, mapNodeNameToID.get(node2), cost));
        adjacencyList.get(mapNodeNameToID.get(node2)).add(new Node(node1, mapNodeNameToID.get(node1), cost));
    }

    private static void checkValidInput(String[] rawEdges, String[][] edges, String routeInput, Map<String, Integer> mapping) throws InputSyntaxException, LogicalInputException {
        if (invalidRouteAndTimeFormat(routeInput) || invalidEdgeInputFormat(rawEdges)) {
            throw new InputSyntaxException("E1");
        } else if (hasDuplicateEdgeEntries(edges, mapping) || invalidStartAndEndNodes(routeInput, mapping)) {
            throw new LogicalInputException("E2");
        }
    }

    private static boolean hasDuplicateEdgeEntries(String[][] edges, Map<String, Integer> mapNodeNameToID) {
        int numVertices = mapNodeNameToID.size();
        boolean[][] duplicateCheck = new boolean[numVertices][numVertices];
        boolean duplicate = false;

        for (String[] edge : edges) {
            String node1 = edge[0];
            String node2 = edge[1];
            boolean node1ToNode2Check = duplicateCheck[mapNodeNameToID.get(node1)][mapNodeNameToID.get(node2)];
            boolean node2ToNode1Check = duplicateCheck[mapNodeNameToID.get(node2)][mapNodeNameToID.get(node1)];

            if (node1ToNode2Check || node2ToNode1Check) {
                duplicate = true;
                break;
            }

            duplicateCheck[mapNodeNameToID.get(node1)][mapNodeNameToID.get(node2)] = true;
            duplicateCheck[mapNodeNameToID.get(node2)][mapNodeNameToID.get(node1)] = true;
        }

        return duplicate;
    }

    private static boolean invalidEdgeInputFormat(String[] rawEdges) {
        boolean notValid = false;
        String regex = "\\[[A-Z],[A-Z],\\d+]";

        for (String rawEdge : rawEdges) {
            if (!rawEdge.matches(regex)) {
                notValid = true;
                break;
            }
        }

        return notValid;
    }

    private static boolean invalidRouteAndTimeFormat(String routeInput) {
        boolean notValid = false;

        String regex = "[A-Z]->[A-Z],\\d+";
        if (!routeInput.matches(regex)) {
            notValid = true;
        }

        return notValid;
    }

    private static boolean invalidStartAndEndNodes(String routeInput, Map<String, Integer> mappings) {
        boolean notValid = false;

        String node1 = routeInput.substring(0, 1);
        String node2 = routeInput.substring(3, 4);

        if (!mappings.containsKey(node1) || !mappings.containsKey(node2)) {
            notValid = true;
        }

        return notValid;
    }

    void printShortestPath(ShortestPath shortestPath) {
        Object[] backwardsPath = pathTo(shortestPath, this.nodeNameToID.get(destinationNode)).toArray();

        for (int i = backwardsPath.length-1; i >= 0; i--) {
            System.out.print(backwardsPath[i] + "->");
        }
        System.out.println(destinationNode);
    }

    private static Stack<String> pathTo(ShortestPath shortestPath, int v) {
        Stack<String> path = new Stack<>();
        for (Node e = shortestPath.edgeTo[v]; e != null; e = shortestPath.edgeTo[e.id]) {
            path.push(e.nodeName);
        }

        return path;
    }
}

class InputSyntaxException extends Exception {
    InputSyntaxException(String errorMessage) {
        super(errorMessage);
    }
}

class LogicalInputException extends Exception {
    LogicalInputException(String errorMessage) {
        super(errorMessage);
    }
}

class NoSuitableRouteException extends Exception {
    NoSuitableRouteException(String errorMessage) {
        super(errorMessage);
    }
}