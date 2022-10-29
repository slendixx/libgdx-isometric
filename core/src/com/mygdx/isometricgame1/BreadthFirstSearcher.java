package com.mygdx.isometricgame1;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;

public class BreadthFirstSearcher {
    private final int NODE_COUNT;
    Array<NavNode> nodes;
    private NavGraph graph;
    private Queue<Integer> queue;
    private boolean[] visitedNodes;

    public BreadthFirstSearcher(NavGraph graph) {
        this.graph = graph;
        NODE_COUNT = graph.getNodeCount();
        nodes = graph.getNodes();
    }

    public void identifySubGraphs() {
        int subgraphId = 0;
        visitedNodes = new boolean[NODE_COUNT];
        queue = new Queue<Integer>();

        for (int i = 0; i < nodes.size; i++) {
            if (nodes.get(i).isObstacle() || visitedNodes[i])
                continue;
            exploreSubgraph(i, subgraphId++);
        }


    }

    public void exploreSubgraph(int startNodeIndex, int subgraphId) {
        visitedNodes[startNodeIndex] = true;
        queue.addLast(startNodeIndex);
        nodes.get(startNodeIndex).setSubgraphId(subgraphId);
        int visitedIndex;

        while (queue.size != 0) {
            visitedIndex = queue.removeFirst();
            Array<Connection<NavNode>> connections = graph.getConnections(nodes.get(visitedIndex));
            for (Connection<NavNode> connection : connections) {
                NavNode adjacentNode = connection.getToNode();
                //if adjacent is not obstacle, set it's subgraphId.
                if (adjacentNode.isObstacle() || visitedNodes[adjacentNode.getIndex()]) {
                    continue;
                }
                visitedNodes[adjacentNode.getIndex()] = true;
                queue.addLast(adjacentNode.getIndex());
                adjacentNode.setSubgraphId(subgraphId);

                //TODO keep reading on https://favtutor.com/blogs/breadth-first-search-java
            }
        }
    }


}
