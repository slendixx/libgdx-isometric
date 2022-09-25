package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Rock {
    /*
     * Rocks are obstacles. obstacles are placed on the center of their
     * tile and occupy the entire tile
     */
    private static final float OBSTACLE_POSITION_OFFSET_X = 0.5f;
    private static final float OBSTACLE_POSITION_OFFSET_Y = 0.5f;
    private static TextureRegion textureRegion;
    private final float TEXTURE_OFFSET_Y = 0.4f;
    private final float TEXTURE_OFFSET_X = 0.5f;
    // TODO remove duplication of batch field
    private SpriteBatch batch;
    private Vector2 position;
    private Vector2 positionScreen;
    private TiledIsoTransformation transformation;
    private IsometricSquare square;

    public Rock(SpriteBatch batch, TiledIsoTransformation transformation, int positionX, int positionY) {
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("sprites/rock/rock-0.png")));
        position = new Vector2(0, 0);
        this.position.set(positionX + OBSTACLE_POSITION_OFFSET_X, positionY + OBSTACLE_POSITION_OFFSET_Y);
        this.batch = batch;
        this.transformation = transformation;
        square = new IsometricSquare((int) Math.floor(position.x), (int) Math.floor(position.y));
    }

    public IsometricSquare getSquare() {
        return square;
    }

    public void draw() {

        positionScreen = transformation.transform(position.x, position.y);
        batch.draw(textureRegion.getTexture(), positionScreen.x - textureRegion.getRegionWidth() * TEXTURE_OFFSET_X,
                positionScreen.y - textureRegion.getRegionHeight() * TEXTURE_OFFSET_Y,
                textureRegion.getRegionWidth() / 2, textureRegion.getRegionHeight() / 2,
                textureRegion.getRegionWidth(),
                textureRegion.getRegionHeight(), 0.75f, 0.75f, 0, textureRegion.getRegionX(),
                textureRegion.getRegionY(),
                textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), false, false);
    }

}
