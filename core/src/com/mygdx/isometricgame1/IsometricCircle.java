package com.mygdx.isometricgame1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class IsometricCircle {
    private Vector2 position;
    private float radiusX;
    private float radiusY;
    // TODO remove transformation duplication between classes. perhaps make it
    // static method
    TiledIsoTransformation transformation;
    private final float offsetX = 0.5f;
    private final float offsetY = 0.5f;

    public IsometricCircle(float circleRadius) {
        this.position = new Vector2(0, 0);
        radiusX = (float) Math.cos(MathUtils.degreesToRadians * 45f) * circleRadius;
        radiusY = radiusX / 2;
        transformation = new TiledIsoTransformation(128, 64);

    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void draw(ShapeDrawer shapeDrawer) {
        Vector2 positionScreen = transformation.transform(position.x, position.y);
        // offset x & y coordinates to put the center of the circle in the iso position
        shapeDrawer.ellipse(positionScreen.x, positionScreen.y, radiusX, radiusY);
    }

}
