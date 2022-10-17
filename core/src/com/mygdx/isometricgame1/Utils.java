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

    /**
     * Applies vector-based collision on the provided Vector2 instance
     *
     * @param circle   collision shape of the Vector2
     * @param square   collision shape of the obstacle
     * @param position the Vector2 to apply collision to
     * @return true if collision happened. false otherwise
     */
    public static boolean collide(IsometricCircle circle, IsometricSquare square, Vector2 position) {

        Vector2 squarePosition = square.getPosition();
        Vector2 circlePosition = circle.getPosition();

        Vector2 nearestPoint = new Vector2(
                (float) Math.max(squarePosition.x - 0.5, Math.min(squarePosition.x + 0.5, position.x)),
                (float) Math.max(squarePosition.y - 0.5, Math.min(squarePosition.y + 0.5, position.y)));

        float nearestY = 128 * (nearestPoint.y - circlePosition.y);
        float nearestX = 128 * (nearestPoint.x - circlePosition.x);
        Vector2 directionToNearest = new Vector2(nearestX, nearestY);
        double distanceToNearestPoint = Math.sqrt(nearestX * nearestX +
                nearestY * nearestY);

        if (distanceToNearestPoint <= circle.getRadius() + 1) {

            position.sub(directionToNearest.nor().scl((float) (circle.getRadius() -
                    distanceToNearestPoint) / 128));
            return true;
        }
        return false;
    }

    /**
     * Applies vector-based collision on the provided Vector2 instance.
     *
     * @param circle   collision shape of the Vector2
     * @param tileX    y coordinate of obstacle tile
     * @param tileX    x coordinate of obstacle tile
     * @param position the Vector2 to apply collision to
     * @return true if collision happened. false otherwise
     */
    public static boolean collide(IsometricCircle circle, int tileX, int tileY, Vector2 position) {

        Vector2 circlePosition = circle.getPosition();

        Vector2 nearestPoint = new Vector2(
                Math.max(tileX, Math.min(tileX + 1.0f, position.x)),
                Math.max(tileY, Math.min(tileY + 1.0f, position.y)));
        float nearestX = (nearestPoint.x - circlePosition.x);
        float nearestY = (nearestPoint.y - circlePosition.y);
        Vector2 directionToNearest = new Vector2(nearestX, nearestY);
        double distanceToNearestPoint = Math.sqrt(nearestX * nearestX +
                nearestY * nearestY);

        if (distanceToNearestPoint <= circle.getRadius() + 1) {

            position.sub(directionToNearest.nor().scl((float) (circle.getRadius() -
                    distanceToNearestPoint) / 128));
            return true;
        }
        return false;
    }
}
