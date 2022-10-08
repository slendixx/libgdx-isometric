package com.mygdx.isometricgame1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class IsometricLine {
    private static Color DEFAULT_COLOR = Color.WHITE;
    private float startScreenX;
    private float startScreenY;
    private float endScreenX;
    private float endScreenY;

    public IsometricLine(Vector2 start, Vector2 end) {
        TiledIsoTransformation transformation = new TiledIsoTransformation(128, 64);
        Vector2 positionScreen = transformation.transform(start.x, start.y);
        startScreenX = positionScreen.x;
        startScreenY = positionScreen.y;
        positionScreen = transformation.transform(end.x, end.y);
        endScreenX = positionScreen.x;
        endScreenY = positionScreen.y;
    }

    public void draw(ShapeDrawer shapeDrawer) {
        shapeDrawer.line(startScreenX, startScreenY, endScreenX, endScreenY);
    }

    public void draw(ShapeDrawer shapeDrawer, Color color) {
        shapeDrawer.setColor(color);
        shapeDrawer.line(startScreenX, startScreenY, endScreenX, endScreenY);
        shapeDrawer.setColor(DEFAULT_COLOR);
    }

}
