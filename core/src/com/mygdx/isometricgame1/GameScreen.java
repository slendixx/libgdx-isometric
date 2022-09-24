package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.ScreenUtils;

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
    private Rock rock;

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
        rock = new Rock(this.game.spriteBatch, transformation);
        rock.setPosition(10, 10);
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
        rock.getCircle().draw(shapeRenderer);
        shapeRenderer.end();
        game.spriteBatch.begin();
        rock.draw();
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
    }

}
