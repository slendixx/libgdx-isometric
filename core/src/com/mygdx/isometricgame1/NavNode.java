package com.mygdx.isometricgame1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.ShapeDrawer;


public class NavNode {
    private Vector2 position;
    private int index;
    private IsometricCircle circle;
    private boolean isObstacle;

    private float labelScreenX;
    private float labelScreenY;

    public NavNode(float x, float y) {
        position = new Vector2(x, y);
        circle = new IsometricCircle(10f);
        circle.setPosition(position);
        isObstacle = false;
        TiledIsoTransformation transformation = new TiledIsoTransformation(128, 64);
        Vector2 positionScreen = transformation.transform(position.x, position.y);
        labelScreenX = positionScreen.x;
        labelScreenY = positionScreen.y;
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public void setIsObstacle(boolean isObstacle) {
        this.isObstacle = isObstacle;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "x:" + position.x + " y:" + position.y;
    }

    public void draw(ShapeDrawer shapeDrawer, SpriteBatch batch, BitmapFont font, boolean inPath) {
        circle.draw(shapeDrawer, inPath ? Color.GREEN : Color.WHITE);
        font.draw(batch, "" + index, labelScreenX, labelScreenY);
    }

    public void draw(ShapeDrawer shapeDrawer, SpriteBatch batch, BitmapFont font, boolean inPath, String label) {
        circle.draw(shapeDrawer, inPath ? Color.GREEN : Color.WHITE);
        font.draw(batch, label, labelScreenX, labelScreenY);
    }

}
