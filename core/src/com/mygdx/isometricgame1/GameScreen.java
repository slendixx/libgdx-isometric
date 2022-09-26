package com.mygdx.isometricgame1;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private final float cameraSpeed = 20;
    private TiledIsoTransformation transformation;
    private Ant ant;
    private ArrayList<Rock> rocks;

    // shapeDrawer
    Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
    Texture shapeDrawerColorWhite;
    Texture shapeDrawerColorRed;
    TextureRegion shapeDrawerTextureWhite;
    TextureRegion shapeDrawerTextureRed;
    ShapeDrawer shapeDrawerWhite;
    ShapeDrawer shapeDrawerRed;

    // TODO try to incorporate a viewport to prevent distortion when resizing screen
    public GameScreen(
            IsometricGame1 game) {
        this.game = game;
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.setToOrtho(false);
        map = new TmxMapLoader().load("maps/map-1.tmx");
        mapRenderer = new MapRenderer(map);
        transformation = new TiledIsoTransformation(Utils.TILE_WIDTH, Utils.TILE_HEIGHT);
        ant = new Ant(this.game.spriteBatch, transformation);
        rocks = new ArrayList<Rock>();
        rocks.add(
                new Rock(this.game.spriteBatch, transformation, 2, 2));

        rocks.add(
                new Rock(this.game.spriteBatch, transformation, 5, 4));

        // init shape drawer
        // TODO refactor this into initShapeDrawer
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        shapeDrawerColorWhite = new Texture(pixmap);
        shapeDrawerTextureWhite = new TextureRegion(shapeDrawerColorWhite, 0, 0, 1, 1);
        shapeDrawerWhite = new ShapeDrawer(game.spriteBatch, shapeDrawerTextureWhite);
        pixmap.setColor(Color.RED);
        pixmap.drawPixel(0, 0);
        shapeDrawerColorRed = new Texture(pixmap);
        shapeDrawerTextureRed = new TextureRegion(shapeDrawerColorRed, 0, 0, 1, 1);
        shapeDrawerRed = new ShapeDrawer(game.spriteBatch, shapeDrawerTextureRed);
        pixmap.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined);

        ant.update(delta, rocks);

        game.spriteBatch.begin();
        mapRenderer.render(game.spriteBatch);
        boolean collides = false;
        for (Rock rock : rocks) {
            rock.getSquare().draw(shapeDrawerWhite);
            rock.draw();

            if (ant.intersects(rock.getSquare()))
                collides = true;
        }
        if (collides) {
            ant.getCircle().draw(shapeDrawerRed);
        } else {
            ant.getCircle().draw(shapeDrawerWhite);
        }
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
        shapeDrawerColorWhite.dispose();
    }

}
