package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Rock {

    private static Texture texture;
    // TODO remove duplication of this field
    private SpriteBatch batch;
    private Vector2 position;
    private Vector2 positionScreen;
    private TiledIsoTransformation transformation;

    public Rock(SpriteBatch batch, TiledIsoTransformation transformation) {
        texture = new Texture(Gdx.files.internal("sprites/rock/rock-0.png"));
        position = new Vector2(0, 0);
        this.batch = batch;
        this.transformation = transformation;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void draw() {
        positionScreen = transformation.transform(position.x, position.y);
        batch.draw(texture, positionScreen.x, positionScreen.y);
    }

}
