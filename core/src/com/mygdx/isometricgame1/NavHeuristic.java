package com.mygdx.isometricgame1;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class NavHeuristic implements Heuristic<NavNode> {

    @Override
    public float estimate(NavNode current, NavNode goal) {
        return Vector2.dst(current.getPosition().x, current.getPosition().y, goal.getPosition().x, goal.getPosition().y);
    }
}
