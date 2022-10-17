package com.mygdx.isometricgame1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class IsometricCircle {
    private static final Color DEFAULT_COLOR = Color.WHITE;
    /**
     * circle radius in pixels
     */
    private final float radius;
    /**
     * x-axis radius for displaying circle as ellipse in isometric projection
     */
    private final float isoRadiusX;
    /**
     * y-axis radius for displaying circle as ellipse in isometric projection
     */
    private final float isoRadiusY;
    // TODO remove transformation duplication between classes. perhaps make it static method
    TiledIsoTransformation transformation;
    private Vector2 position;

    public IsometricCircle(float radius) {
        this.position = new Vector2(0, 0);
        this.radius = radius;
        isoRadiusX = (float) Math.cos(MathUtils.degreesToRadians * 45f) * radius;
        isoRadiusY = isoRadiusX / 2;
        transformation = new TiledIsoTransformation(128, 64);
    }

    public float getRadius() {
        return radius;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public void draw(ShapeDrawer shapeDrawer) {
        Vector2 positionScreen = transformation.transform(position.x, position.y);
        // offset x & y coordinates to put the center of the circle in the iso position
        shapeDrawer.ellipse(positionScreen.x, positionScreen.y, isoRadiusX, isoRadiusY);

    }

    public void draw(ShapeDrawer shapeDrawer, Color color) {
        Vector2 positionScreen = transformation.transform(position.x, position.y);
        shapeDrawer.setColor(color);
        shapeDrawer.ellipse(positionScreen.x, positionScreen.y, isoRadiusX, isoRadiusY);
        shapeDrawer.setColor(DEFAULT_COLOR);
    }
}
