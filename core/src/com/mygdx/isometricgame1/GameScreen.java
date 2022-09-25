package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.ScreenUtils;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameScreen implements Screen {

    private IsometricGame1 game;
    private OrthographicCamera camera;
    // TODO refactor these and other constants into game class to remove duplication
    private final int VIEWPORT_WIDTH = 1600;
    private final int VIEWPORT_HEIGHT = 900;
    private TiledMap map;
    private MapRenderer mapRenderer;
    private ShapeRenderer shapeRenderer;
    private final float cameraSpeed = 20;
    private TiledIsoTransformation transformation;
    private Ant ant;
    private Rock rock1;
    private Rock rock2;
    private Rock rock3;

    // shapeDrawer
    Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
    Texture shapeDrawerDrawTexture;
    TextureRegion shapeDrawerTextureRegion;
    ShapeDrawer shapeDrawer;

    // TODO try to incorporate a viewport to prevent distortion when resizing screen
    public GameScreen(
            IsometricGame1 game) {
        this.game = game;
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.setToOrtho(false);
        map = new TmxMapLoader().load("maps/map-1.tmx");
        mapRenderer = new MapRenderer(map);
        shapeRenderer = new ShapeRenderer();
        transformation = new TiledIsoTransformation(Utils.TILE_WIDTH, Utils.TILE_HEIGHT);
        ant = new Ant(this.game.spriteBatch, transformation);
        rock1 = new Rock(this.game.spriteBatch, transformation, 2, 2);
        rock2 = new Rock(this.game.spriteBatch, transformation, 4, 5);
        rock3 = new Rock(this.game.spriteBatch, transformation, 3, 7);

        // init shape drawer
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        shapeDrawerDrawTexture = new Texture(pixmap);
        pixmap.dispose();
        shapeDrawerTextureRegion = new TextureRegion(shapeDrawerDrawTexture, 0, 0, 1, 1);
        shapeDrawer = new ShapeDrawer(game.spriteBatch, shapeDrawerTextureRegion);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined);

        ant.update(delta);
        game.spriteBatch.begin();
        mapRenderer.render(game.spriteBatch);
        game.spriteBatch.end();
        // render ant circle
        // TODO research way to render shaped in a SpriteBatch
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        ant.getCircle().draw(shapeRenderer);
        shapeRenderer.end();
        game.spriteBatch.begin();
        rock1.getSquare().draw(shapeDrawer);
        rock1.draw();
        rock2.getSquare().draw(shapeDrawer);
        rock2.draw();
        rock3.getSquare().draw(shapeDrawer);
        rock3.draw();
        ant.draw();
        game.spriteBatch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.set(camera.position.x, camera.position.y + cameraSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.set(camera.position.x, camera.position.y - cameraSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.set(camera.position.x + cameraSpeed, camera.position.y, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.set(camera.position.x - cameraSpeed, camera.position.y, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) {
            Gdx.app.log("zoom", "" + camera.zoom);
            camera.zoom += 0.25f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) {
            Gdx.app.log("zoom", "" + camera.zoom);
            camera.zoom -= 0.25f;
        }

        if (Gdx.input.justTouched()) {
            ant.updateFacingDirection(Utils.clickPositionToIso(Gdx.input.getX(), Gdx.input.getY(), camera));
        }

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // TODO dispose of EVERYTHING
        ant.dispose();
        shapeDrawerDrawTexture.dispose();
    }

}
