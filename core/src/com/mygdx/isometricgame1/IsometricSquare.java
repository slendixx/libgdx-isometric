package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class IsometricSquare {

    //TODO remove duplication among isometric shape classes
    private static Color DEFAULT_COLOR = Color.WHITE;
    private float[] vertices;
    private Vector2 position;
    private TiledIsoTransformation transformation;

    public IsometricSquare(int tileX, int tileY) {
        float x0 = tileX;
        float y0 = tileY;
        float x1 = tileX;
        float y1 = tileY + 1;
        float x2 = tileX + 1;
        float y2 = tileY + 1;
        float x3 = tileX + 1;
        float y3 = tileY;

        position = new Vector2(tileX + 0.5f, tileY + 0.5f);

        transformation = new TiledIsoTransformation(Utils.TILE_WIDTH, Utils.TILE_HEIGHT);
        vertices = new float[8];
        Vector2 positionScreen = transformation.transform(x0, y0);
        vertices[0] = positionScreen.x;
        vertices[1] = positionScreen.y;
        positionScreen = transformation.transform(x1, y1);
        vertices[2] = positionScreen.x;
        vertices[3] = positionScreen.y;
        positionScreen = transformation.transform(x2, y2);
        vertices[4] = positionScreen.x;
        vertices[5] = positionScreen.y;
        positionScreen = transformation.transform(x3, y3);
        vertices[6] = positionScreen.x;
        vertices[7] = positionScreen.y;

    }

    public void draw(ShapeDrawer shapeDrawer) {
        shapeDrawer.polygon(vertices);
    }

    public void draw(ShapeDrawer shapeDrawer, Color color) {
        shapeDrawer.setColor(color);
        shapeDrawer.polygon(vertices);
        shapeDrawer.setColor(DEFAULT_COLOR);
    }

    public Vector2 getPosition() {
        return position;
    }

}
