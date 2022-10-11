package com.mygdx.isometricgame1;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;

import java.util.ArrayList;

public class PathSmoother {
    private final int MIN_NODE_COUNT = 3;
    private final int STARTING_OFFSET = 2;
    private RayCaster rayCaster;

    public PathSmoother(RayCaster rayCaster) {
        this.rayCaster = rayCaster;
    }

    public GraphPath<NavNode> smooth(GraphPath<NavNode> path) {
        /*
         *//*
         * for i < nodes.length - 2{
         * newPath.add(nodes[i])
         * nextNodeIndexOffset = 2
         * visibleNodeWasFound = false
         * if(i + nextNodeIndex >= n){
         * break
         * }
         * do{
         * if rayCaster.areVisible(nodes[i], nodes[i+nextNodeIndexOffset]){
         * visibleNodeWasFound = true
         * newPath.set(i+1, nodes[i+nextNodeIndexOffset])
         * nextNodeIndexOffset++
         * }
         * }while(visibleNodeWasFound)
         * }
         *//*
        int nodeAmount = path.getCount();
        if (nodeAmount < MIN_NODE_COUNT)
            return path;

        ArrayList<NavNode> newPath = new ArrayList<>();
        int nextNodeOffset = STARTING_NEXT_NODE_OFFSET;
        boolean visibleNodeWasFound;
        boolean reiteratingSameNode;

        for (int i = 0; i < nodeAmount - STARTING_NEXT_NODE_OFFSET; i++) {

            if (i + nextNodeOffset >= nodeAmount)
                break;

            newPath.add(path.get(i));
            nextNodeOffset = STARTING_NEXT_NODE_OFFSET;
            reiteratingSameNode = false;

            do {
                visibleNodeWasFound = false;

                if (i + nextNodeOffset >= nodeAmount)
                    break;

                if (rayCaster.nodesHaveVisibility(path.get(i), path.get(i + nextNodeOffset))) {
                    visibleNodeWasFound = true;
                    if (!reiteratingSameNode)
                        newPath.add(path.get(i + nextNodeOffset));
                    else
                        newPath.set(i + 1, path.get(i + nextNodeOffset));

                    reiteratingSameNode = true;
                    nextNodeOffset++;
                }
            } while (visibleNodeWasFound);
        }

        GraphPath<NavNode> smoothedPath = new DefaultGraphPath<NavNode>();
        for (NavNode node : newPath) {
            // Gdx.app.log("node", node.getPosition().toString());
            smoothedPath.add(node);

        }
        Gdx.app.log("path:", "");
        for (NavNode node : path) {
            Gdx.app.log("x:", "" + node.getPosition().x);
            Gdx.app.log("y:", "" + node.getPosition().y);
        }
        Gdx.app.log("new path:", "");
        for (NavNode node : newPath) {
            Gdx.app.log("x:", "" + node.getPosition().x);
            Gdx.app.log("y:", "" + node.getPosition().y);
        }
        return smoothedPath;*/

        int pathLength = path.getCount();
        if (pathLength < MIN_NODE_COUNT)
            return path;
        ArrayList<NavNode> newPath = new ArrayList<>();
        newPath.add(path.get(0));
        boolean reiterating;
        boolean beingReiterated;
        int indexOffset;
        int lastVisitedIndex = 0;

        while (true) {
            beingReiterated = false;
            indexOffset = STARTING_OFFSET;
            if (lastVisitedIndex + indexOffset >= pathLength) {
                break;
            }

            do {
                reiterating = false;
                if (lastVisitedIndex + indexOffset >= pathLength) {
                    break;
                }
                if (rayCaster.nodesHaveVisibility(path.get(lastVisitedIndex), path.get(lastVisitedIndex + indexOffset))) {
                    if (beingReiterated) {
                        newPath.set(newPath.size() - 1, path.get(lastVisitedIndex + indexOffset));
                    } else {
                        newPath.add(path.get(lastVisitedIndex + indexOffset));
                    }
                    reiterating = true;
                    beingReiterated = true;
                    indexOffset++;
                }
            } while (reiterating);
            lastVisitedIndex += indexOffset - 1;
        }
        if (lastVisitedIndex == pathLength - 2) {
            newPath.add(path.get(pathLength - 1));
        }

        GraphPath<NavNode> smoothedPath = new DefaultGraphPath<NavNode>();
        for (NavNode node : newPath) {
            // Gdx.app.log("node", node.getPosition().toString());
            smoothedPath.add(node);
        }
        return smoothedPath;
    }
}
