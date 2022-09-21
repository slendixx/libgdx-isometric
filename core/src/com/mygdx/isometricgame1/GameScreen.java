package com.mygdx.isometricgame1;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    private IsometricGame1 game;
    private OrthographicCamera camera;
    private final int VIEWPORT_WIDTH = 1600;
    private final int VIEWPORT_HEIGHT = 900;
    private final int TILE_WIDTH = 128;
    private final int TILE_HEIGHT = 64;
    private TiledMap map;
    private MapRenderer mapRenderer;
    private final float cameraSpeed = 20;
    private Vector2 positionScreen; // general use buffer for transformed iso coordinates
    private TiledIsoTransformation transformation;
    // TODO refactor these and other constants into game class to remove duplication
    private final float SCREEN_OFFSET_CORRECTION_X = 0.5f;
    private final float SCREEN_OFFSET_CORRECTION_Y = -0.5f;
    private static final float ARRIVED_TO_TARGET_POSITION_DISTANCE = 0.1f;

    // ant animation
    private final int FACING_DIRECTION_AMOUNT = 16;
    private final int SPRITE_DIRECTION_AMOUNT = 9;
    private TextureAtlas antWalkingAtlas = new TextureAtlas(Gdx.files.internal("sprites/ant/ant-walking.atlas"));
    private TextureAtlas antIdleAtlas = new TextureAtlas(Gdx.files.internal("sprites/ant/ant-idle-0.atlas"));
    private HashMap<Integer, Animation<TextureRegion>> antWalkingAnimations;
    private HashMap<Integer, Animation<TextureRegion>> antIdleAnimations;
    private final float WALKING_FRAME_DURATION = 1 / 26f;
    private final float IDLE_FRAME_DURATION = 1 / 10f;
    private float elapsedTime = 0;
    private Vector2 antPosition;
    private Vector2 antDirectionVector;
    private int antFacingDirection; // 0-15
    private UnitState AntState = UnitState.IDLE;
    private Vector2 antTargetPosition;
    private final float ANT_SPEED = 1.4f;
    private final int WALKING_FRAME_COLS = 7;
    private final int WALKING_FRAME_ROWS = 25;
    private final int IDLE_FRAME_COLS = 13;
    private final int IDLE_FRAME_ROWS = 7;

    // TODO try to incorporate a viewport to prevent distortion when resizing screen
    public GameScreen(
            IsometricGame1 game) {
        this.game = game;
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.setToOrtho(false);
        map = new TmxMapLoader().load("maps/map-1.tmx");
        mapRenderer = new MapRenderer(map);
        transformation = new TiledIsoTransformation(TILE_WIDTH, TILE_HEIGHT);

        // ant
        antWalkingAnimations = new HashMap<Integer, Animation<TextureRegion>>();
        // init directions 0-8
        for (int i = 0; i < SPRITE_DIRECTION_AMOUNT; i++) {
            antWalkingAnimations.put(i, new Animation<TextureRegion>(WALKING_FRAME_DURATION,
                    antWalkingAtlas.findRegions("walking-" + i), PlayMode.LOOP));
        }
        // init directions with flipped sprites for directions 9-15
        int animationIndex = 9;
        // we need to store again references to sprites for directions 1-7
        for (int i = 7; i > 0; i--) {
            antWalkingAnimations.put(animationIndex++, antWalkingAnimations.get(i));
        }

        antIdleAnimations = new HashMap<Integer, Animation<TextureRegion>>();
        for (int i = 0; i < SPRITE_DIRECTION_AMOUNT; i++) {
            antIdleAnimations.put(i, new Animation<TextureRegion>(IDLE_FRAME_DURATION,
                    antIdleAtlas.findRegions("idle-" + i), PlayMode.LOOP));
        } // init directions with flipped sprites for directions 9-15
        animationIndex = 9;
        // we need to store again references to sprites for directions 1-7
        for (int i = 7; i > 0; i--) {
            antIdleAnimations.put(animationIndex++, antIdleAnimations.get(i));
        }

        antPosition = new Vector2(15, 10);
        antDirectionVector = new Vector2();
        antTargetPosition = new Vector2();
        antFacingDirection = 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined);

        // ant walking
        elapsedTime += delta;
        if (AntState == UnitState.WALKING) {
            antPosition.x += ANT_SPEED * antDirectionVector.x * delta;
            antPosition.y += ANT_SPEED * antDirectionVector.y * delta;

            // Gdx.app.log("position", "x:" + antPosition.x + " y:" + antPosition.y);
            float distanceToTarget = antPosition.dst(antTargetPosition);

            if (distanceToTarget <= ARRIVED_TO_TARGET_POSITION_DISTANCE) {
                /*
                 * TODO this check will probably have to be adjusted to take the unit size into
                 * consideration when I start checkin for collisions
                 */
                // TODO add idle animation
                AntState = UnitState.IDLE;
            }
        }

        game.spriteBatch.begin();
        mapRenderer.render(game.spriteBatch);
        // renderIdleWorker(game.spriteBatch, workerFacingDirection, idleFrames,
        // workerPosition);
        renderAnt(game.spriteBatch, antPosition, antFacingDirection, AntState, antWalkingAnimations, elapsedTime);
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
            antTargetPosition = clickPositionToIso(Gdx.input.getX(), Gdx.input.getY(), camera);
            // Gdx.app.log("target position", "x:" + antTargetPosition.x + " y:" +
            // antTargetPosition.y);
            AntState = UnitState.WALKING;
            antDirectionVector.set(antTargetPosition);
            antDirectionVector.sub(antPosition).nor();
            float angle = MathUtils.radiansToDegrees * MathUtils.atan2(antDirectionVector.y, antDirectionVector.x);

            if (angle >= 146.25 && angle < 168.75)
                antFacingDirection = 15;
            else if (angle >= 168.75 && angle < 180.0 || angle >= -180 && angle < -168.75)
                antFacingDirection = 14;
            else if (angle >= -168.75 && angle < -146.25)
                antFacingDirection = 13;
            else if (angle >= -146.25 && angle < -123.75)
                antFacingDirection = 12;
            else if (angle >= -123.75 && angle < -101.25)
                antFacingDirection = 11;
            else if (angle >= -101.25 && angle < -78.75)
                antFacingDirection = 10;
            else if (angle >= -78.75 && angle < -56.25)
                antFacingDirection = 9;
            else if (angle >= -56.25 && angle < -33.75)
                antFacingDirection = 8;
            else if (angle >= -33.75 && angle < -11.25)
                antFacingDirection = 7;
            else if (angle >= -11.25 && angle < 11.25)
                antFacingDirection = 6;
            else if (angle >= -11.25 && angle < 33.75)
                antFacingDirection = 5;
            else if (angle >= 33.75 && angle < 56.25)
                antFacingDirection = 4;
            else if (angle >= 56.25 && angle < 78.75)
                antFacingDirection = 3;
            else if (angle >= 78.75 && angle < 101.25)
                antFacingDirection = 2;
            else if (angle >= 101.25 && angle < 123.75)
                antFacingDirection = 1;
            else if (angle >= 123.75 && angle < 146.25)
                antFacingDirection = 0;
        }

    }

    private void renderAnt(SpriteBatch batch, Vector2 position, int facingDirection, UnitState state,
            HashMap<Integer, Animation<TextureRegion>> antWalkingAnimations, float elapsedTime) {

        boolean flipX = false;
        if (facingDirection >= 9 && facingDirection < FACING_DIRECTION_AMOUNT) {
            flipX = true;
        }
        // TODO overload transform method to receive a Vector2
        positionScreen = transformation.transform(position.x, position.y);

        TextureRegion currentFrame = null;
        int animationCols = 0;
        int animationRows = 0;
        float animationOffsetX = 0f;
        float animationOffsetY = 0f;

        switch (state) {
            case IDLE:
                currentFrame = antIdleAnimations.get(facingDirection).getKeyFrame(elapsedTime);
                animationCols = IDLE_FRAME_COLS;
                animationRows = IDLE_FRAME_ROWS;
                animationOffsetX = 0.6f;
                animationOffsetY = 0.0f;
                break;
            case WALKING:
                currentFrame = antWalkingAnimations.get(facingDirection).getKeyFrame(elapsedTime);
                animationCols = WALKING_FRAME_COLS;
                animationRows = WALKING_FRAME_ROWS;
                animationOffsetX = 0.6f;
                animationOffsetY = -0.05f;
                break;
        }

        batch.draw(currentFrame.getTexture(), positionScreen.x - currentFrame.getRegionWidth() * animationOffsetX,
                positionScreen.y + currentFrame.getRegionHeight() * animationOffsetY,
                0, 0,
                currentFrame.getRegionWidth(),
                currentFrame.getRegionHeight(), 1, 1, 0,
                currentFrame.getRegionX(), currentFrame.getRegionY(), currentFrame.getRegionWidth(),
                currentFrame.getRegionHeight(), flipX, false);
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

    private Vector2 clickPositionToIso(float clickX, float clickY, OrthographicCamera camera) {
        // get mouse position in world space
        // TODO source https://www.youtube.com/watch?v=Zy8bvij2ioQ&t=627s
        // TODO read https://clintbellanger.net/articles/isometric_math/
        Vector3 mousePos = new Vector3(clickX, clickY, 0);
        mousePos = camera.unproject(mousePos);
        float mapX = -(mousePos.y / TILE_HEIGHT - mousePos.x / TILE_WIDTH) + SCREEN_OFFSET_CORRECTION_X;
        float mapY = (mousePos.x / TILE_WIDTH + mousePos.y / TILE_HEIGHT) + SCREEN_OFFSET_CORRECTION_Y;

        return new Vector2(mapX, mapY);
    }
}
