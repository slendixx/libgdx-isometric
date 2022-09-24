package com.mygdx.isometricgame1;

import com.badlogic.gdx.math.Vector2;

public class TiledIsoTransformation {

    private Vector2 uniti;
    private Vector2 unitj;
    private Vector2 transformX;
    private Vector2 transformY;
    private Vector2 invUniti;
    private Vector2 invUnitj;

    public TiledIsoTransformation(int tileWidth, int tileHeight) {
        /*
         * TODO research why removing the division by 2 on the tile height fixes the
         * tile position
         */

        /*
         * The isometric transformation was adjusted to match the one used by tiled by
         * default
         */
        uniti = new Vector2(0.5f * tileWidth, -0.5f * tileHeight);
        unitj = new Vector2(0.5f * tileWidth, 0.5f * tileHeight);
        // uniti = new Vector2(1.0f, 0.5f);
        // unitj = new Vector2(-1.0f, 0.5f);

        /*
         * treat uniti and unitj as transformation matrix and inverse to
         * translate from screen to iso coords
         * coordinates.
         * 
         * iso to screen matrix:
         * A = (a b)
         * (c d)
         * 
         * screen to iso matrix: inv(A)
         * -1
         * B = (a b) = 1/det(B) * (d -b)
         * (c d) (-c a)
         */

        float invDeterminant = 1 / ((uniti.x * unitj.y) - (unitj.x * uniti.y));

        invUniti = new Vector2(invDeterminant * unitj.y, invDeterminant * -uniti.y);
        invUnitj = new Vector2(invDeterminant * unitj.x, invDeterminant * -uniti.x);

        transformX = new Vector2();
        transformY = new Vector2();
    }

    // TODO receive vector 2 as argument
    public Vector2 transform(float isoX, float isoY) {
        // scalar multiplication of x * uniti & y * unitj
        transformX.set(uniti).scl(isoX);
        transformY.set(unitj).scl(isoY);
        // add both terms together
        return transformX.add(transformY);
    }

    public Vector2 untransform(float screenX, float screenY) {
        transformX.set(invUniti).scl(screenX);
        transformY.set(invUnitj).scl(screenY);
        // add both terms together
        return transformX.add(transformY);
    }
}
