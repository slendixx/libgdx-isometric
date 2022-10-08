package com.mygdx.isometricgame1;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class NavConnection implements Connection<NavNode> {
    private NavNode from;
    private NavNode to;
    private float cost;

    private IsometricLine line;

    public NavConnection(NavNode from, NavNode to) {
        this.from = from;
        this.to = to;
        cost = Vector2.dst(from.getPosition().x, from.getPosition().y, to.getPosition().x, to.getPosition().y);
        line = new IsometricLine(from.getPosition(), to.getPosition());
    }

    public void draw(ShapeDrawer shapeDrawer, boolean inPath) {
        line.draw(shapeDrawer, inPath ? Color.GREEN : Color.WHITE);
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public NavNode getFromNode() {
        return from;
    }

    @Override
    public NavNode getToNode() {
        return to;
    }
}
