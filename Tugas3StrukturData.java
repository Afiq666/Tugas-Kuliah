import java.util.*;

public class GraphSearch {
    
    static class Graph {
        private Map<String, List<String>> adjList = new HashMap<>();
        
        public void addEdge(String source, String dest) {
            adjList.putIfAbsent(source, new ArrayList<>());
            adjList.get(source).add(dest);
        }
        
        public List<String> getNeighbors(String node) {
            return adjList.getOrDefault(node, new ArrayList<>());
        }
    }
    
    // DFS menggunakan Stack
    public static boolean dfs(Graph graph, String start, String target) {
        System.out.println("\n=== DFS ===");
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<>();
        stack.push(start);
        int step = 1;
        
        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (visited.contains(current)) continue;
            
            System.out.println("Langkah " + step++ + ": " + current);
            visited.add(current);
            
            if (current.equals(target)) {
                System.out.println("DAPAT DITEMUKAN!");
                return true;
            }
            
            List<String> neighbors = graph.getNeighbors(current);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                if (!visited.contains(neighbors.get(i))) {
                    stack.push(neighbors.get(i));
                }
            }
        }
        System.out.println("TIDAK DAPAT DITEMUKAN!");
        return false;
    }
    
    // BFS menggunakan Queue
    public static boolean bfs(Graph graph, String start, String target) {
        System.out.println("\n=== BFS ===");
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);
        int step = 1;
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            System.out.println("Langkah " + step++ + ": " + current);
            
            if (current.equals(target)) {
                System.out.println("BERHASIL DITEMUKAN!");
                return true;
            }
            
            for (String neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        System.out.println("TIDAK DAPAT DITEMUKAN!");
        return false;
    }
    
    public static void main(String[] args) {
        Graph graph = new Graph();
        
        // Struktur graf: a1 -> a2, a3 -> a4, a5, a6, a7 -> a8
        graph.addEdge("a1", "a2");
        graph.addEdge("a1", "a3");
        graph.addEdge("a2", "a4");
        graph.addEdge("a2", "a5");
        graph.addEdge("a3", "a6");
        graph.addEdge("a3", "a7");
        graph.addEdge("a5", "a8");
        
        System.out.println("Graf: a1 -> [a2, a3], a2 -> [a4, a5], a3 -> [a6, a7], a5 -> [a8]");
        
        Scanner scanner = new Scanner(System.in);
        System.out.print(" Mencari node : ");
        String target = scanner.nextLine();
        
        dfs(graph, "a1", target);
        bfs(graph, "a1", target);
        
        scanner.close();
    }
}