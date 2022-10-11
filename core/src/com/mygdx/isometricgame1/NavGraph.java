package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class NavGraph implements IndexedGraph<NavNode>, ObstacleManager {

    NavHeuristic heuristic;
    Array<NavNode> nodes;
    Array<NavConnection> connections;
    ObjectMap<NavNode, Array<Connection<NavNode>>> map;
    private int lastNodeIndex;

    public NavGraph() {
        heuristic = new NavHeuristic();
        nodes = new Array<>();
        connections = new Array<>();
        map = new ObjectMap<>();
        lastNodeIndex = 0;
    }

    public Array<NavConnection> getConnections() {
        return connections;
    }

    public Array<NavNode> getNodes() {
        return nodes;
    }

    public void addNode(NavNode node) {
        node.setIndex(lastNodeIndex);
        lastNodeIndex++;
        nodes.add(node);
    }

    public void connectNodes(NavNode from, NavNode to) {
        NavConnection connection = new NavConnection(from, to);
        if (!map.containsKey(from)) {
            map.put(from, new Array<Connection<NavNode>>());
        }
        map.get(from).add(connection);
        connections.add(connection);
    }

    public GraphPath<NavNode> findPath(NavNode start, NavNode goal) {
        GraphPath<NavNode> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(start, goal, heuristic, path);
        return path;
    }

    @Override
    public int getIndex(NavNode node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }

    @Override
    public Array<Connection<NavNode>> getConnections(NavNode from) {
        if (map.containsKey(from)) {
            return map.get(from);
        }
        return new Array<>(0);
    }

    @Override
    public boolean tileIsObstacle(int x, int y, int gameMapWidth) {
        return nodes.get(y * gameMapWidth + x).isObstacle();
    }
}
