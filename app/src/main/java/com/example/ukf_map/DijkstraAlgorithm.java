package com.example.ukf_map;

public class DijkstraAlgorithm {
    private final int NO_PARENT = -1;
    private int[] distance, parents, route;
    private boolean[] visited;
    private int numberOfNodes, startNode, finalNode, countNodesForFinalRoute = 0;
    private int[][] graph;
    private String path = "", finalPath = "";

    public DijkstraAlgorithm(int[][] graph, int startNode, int finalNode) {
        //this.graph = graph;
       // this.startNode = startNode;
        //this.finalNode = finalNode;
        dijkstra(graph, startNode, finalNode);
    }

    private void dijkstra(int[][] graphMap, int startNode, int finalNode) {
        numberOfNodes = graphMap[0].length;
        distance = new int[numberOfNodes];
        visited = new boolean[numberOfNodes];
        parents = new int[numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++) { // zatiaľ nebol žiaden uzol preskúmaný ani žiadna vzdialenosť
            distance[i] = Integer.MAX_VALUE;
            parents[i] = Integer.MAX_VALUE;
            visited[i] = false;
        }
        distance[startNode] = 0; //začiatočný uzol nemá žiadnu vzdialenosť od sám seba
        parents[startNode] = NO_PARENT; //začiatočný uzol nemá rodiča
        for (int i = 1; i < numberOfNodes; i++) {
            int nextNode = -1;
            int shortestDistance = Integer.MAX_VALUE;
            for (int j = 0; j < numberOfNodes; j++) {
                if (!visited[j] && distance[j] < shortestDistance) { //hľadám nepreskúmaný uzol s najmenšou vzdialenosťou
                    nextNode = j;
                    shortestDistance = distance[j];
                }
            }
            if(nextNode == finalNode) break;
            visited[nextNode] = true;
            for (int k = 0; k < numberOfNodes; k++) {
                int edgeDistance = graphMap[nextNode][k];
                if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < distance[k])) {
                    parents[k] = nextNode;
                    distance[k] = shortestDistance + edgeDistance;
                }
            }
        }
        getPaths(parents, finalNode, startNode);
    }

    private void getPaths(int[] parents, int end, int start) {
        getPathRecursion(end, parents);
        finalPath = path.substring(0, path.length()-1);
        System.out.println("Trasa: " + finalPath);
        String[] helper = finalPath.split("-");
        route = new int[helper.length];
        for(int i = 0; i < helper.length; i++) {
            route[i] = Integer.parseInt(helper[i]);
        }
    }

    private void getPathRecursion(int currentNode, int[] parents) {
        if (currentNode == NO_PARENT) {
            return;
        }
        getPathRecursion(parents[currentNode], parents);
        path += currentNode + "-";
        countNodesForFinalRoute++;
    }

    public int[] returnPath() {
        return route;
    }
}
