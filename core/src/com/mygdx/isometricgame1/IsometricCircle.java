package com.mygdx.isometricgame1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class IsometricCircle {
    private Vector2 position;
    private float width;
    private float height;
    // TODO remove transformation duplication between classes. perhaps make it
    // static method
    TiledIsoTransformation transformation;
    private final float offsetX = 0.5f;
    private final float offsetY = 0.5f;

    public IsometricCircle(float circleRadius) {
        this.position = new Vector2(0, 0);
        width = 2 * (float) Math.cos(MathUtils.degreesToRadians * 45f) * circleRadius;
        height = width / 2;
        transformation = new TiledIsoTransformation(128, 64);

    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public void draw(ShapeRenderer renderer) {
        Vector2 positionScreen = transformation.transform(position.x, position.y);
        renderer.setColor(Color.WHITE);
        // offset x & y coordinates to put the center of the circle in the iso position
        renderer.ellipse(positionScreen.x - (width * offsetX), positionScreen.y - (height * offsetY), width, height);
    }

}