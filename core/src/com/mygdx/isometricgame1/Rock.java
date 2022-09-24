package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Rock {

    private static TextureRegion textureRegion;
    private final float TEXTURE_OFFSET_Y = 0.07f;
    private final float TEXTURE_OFFSET_X = 0.5f;
    // TODO remove duplication of batch field
    private SpriteBatch batch;
    private Vector2 position;
    private Vector2 positionScreen;
    private TiledIsoTransformation transformation;

    private final float RADIUS = 80;
    private IsometricCircle circle;

    public IsometricCircle getCircle() {
        return circle;
    }

    public Rock(SpriteBatch batch, TiledIsoTransformation transformation) {
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("sprites/rock/rock-0.png")));
        position = new Vector2(0, 0);
        this.batch = batch;
        this.transformation = transformation;
        circle = new IsometricCircle(RADIUS);
        circle.setPosition(position);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        circle.setPosition(position);
    }

    public void draw() {

        positionScreen = transformation.transform(position.x, position.y);
        batch.draw(textureRegion.getTexture(), positionScreen.x - textureRegion.getRegionWidth() * TEXTURE_OFFSET_X,
                positionScreen.y - textureRegion.getRegionHeight() * TEXTURE_OFFSET_Y, 0, 0,
                textureRegion.getRegionWidth(),
                textureRegion.getRegionHeight(), 1, 1, 0, textureRegion.getRegionX(), textureRegion.getRegionY(),
                textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), false, false);
    }

}
