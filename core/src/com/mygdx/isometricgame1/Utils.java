package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Utils {
    public static final int TILE_WIDTH = 128;
    public static final int TILE_HEIGHT = 64;
    // public static final float SCREEN_OFFSET_CORRECTION_X = 0.5f;
    // public static final float SCREEN_OFFSET_CORRECTION_Y = -0.5f;
    public static final float SCREEN_OFFSET_CORRECTION_X = 0f;
    public static final float SCREEN_OFFSET_CORRECTION_Y = -0f;

    public static Vector2 clickPositionToIso(float clickX, float clickY, OrthographicCamera camera) {
        // get mouse position in world space
        // TODO source https://www.youtube.com/watch?v=Zy8bvij2ioQ&t=627s
        // TODO read https://clintbellanger.net/articles/isometric_math/
        Vector3 mousePos = new Vector3(clickX, clickY, 0);
        mousePos = camera.unproject(mousePos);
        float mapX = -(mousePos.y / TILE_HEIGHT - mousePos.x / TILE_WIDTH) + SCREEN_OFFSET_CORRECTION_X;
        float mapY = (mousePos.x / TILE_WIDTH + mousePos.y / TILE_HEIGHT) + SCREEN_OFFSET_CORRECTION_Y;

        // Gdx.app.log("mapX", "" + mapX);
        // Gdx.app.log("mapY", "" + mapY);

        return new Vector2(mapX, mapY);
    }
}
