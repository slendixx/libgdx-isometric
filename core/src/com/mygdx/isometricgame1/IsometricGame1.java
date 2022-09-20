package com.mygdx.isometricgame1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IsometricGame1 extends Game {
    public SpriteBatch spriteBatch;
    public SpriteBatch hudBatch;
    public BitmapFont font;

    private static BitmapFont initFont() {
        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        return font;
    }

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        font = initFont();
        setScreen(new GameScreen(this));

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {

        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteBatch.dispose();
        font.dispose();
    }
}
