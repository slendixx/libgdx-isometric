package com.mygdx.isometricgame1;

import javax.swing.text.Position;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.isometricgame1.FacingDirection;

public class GameScreen implements Screen {
    /**
     *
     */
    private IsometricGame1 game;
    private OrthographicCamera camera;
    private final int VIEWPORT_WIDTH = 1600;
    private final int VIEWPORT_HEIGHT = 900;
    private final int TILE_WIDTH = 128;
    private final int TILE_HEIGHT = 64;
    private TiledMap map;
    private MapRenderer mapRenderer;
    private final float cameraSpeed = 20;
    private int mapWidth;
    private int mapHeight;
    private Vector2 positionScreen;
    private TiledIsoTransformation transformation;
    // TODO refactor these and other constants into game class to remove duplication
    private final float SCREEN_OFFSET_CORRECTION_X = 0.5f;
    private final float SCREEN_OFFSET_CORRECTION_Y = -0.5f;
    // idle animation
    private TextureRegion[] idleFrames;
    private Texture idleSheet;
    private final int IDLE_COLS = 5;
    private final int IDLE_ROWS = 1;
    private Texture workerTexture;
    private Vector2 workerPosition;
    private FacingDirection workerFacingDirection = FacingDirection.SOUTH;
    private Vector2 workerDirection;
    private UnitState workerState = UnitState.IDLE;
    private Vector2 workerTargetPosition;
    private final float WORKER_SPEED = 2.5f;
    private static final float ARRIVED_TO_TARGET_DISTANCE = 0.1f;

    // TODO try to incorporate a viewport to prevent distortion when resizing screen
    public GameScreen(IsometricGame1 game) {
        this.game = game;
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.setToOrtho(false);
        map = new TmxMapLoader().load("maps/map-1.tmx");
        mapRenderer = new MapRenderer(map);
        MapProperties props = map.getProperties();
        mapWidth = (int) props.get("width");
        mapHeight = (int) props.get("height");

        workerPosition = new Vector2(15, 10);
        transformation = new TiledIsoTransformation(TILE_WIDTH, TILE_HEIGHT);
        workerTexture = new Texture(Gdx.files.internal("sprites/worker.png"));
        idleSheet = new Texture(Gdx.files.internal("sprites/worker-idle.png"));
        /*
         * Create a 2d array of texture regions from the sprite sheet
         */
        TextureRegion[][] sheetBuffer = TextureRegion.split(idleSheet, idleSheet.getWidth() / IDLE_COLS,
                idleSheet.getHeight() / 1);
        idleFrames = new TextureRegion[IDLE_COLS * IDLE_ROWS];
        int frameIndex = 0;
        for (int i = 0; i < IDLE_ROWS; i++) {
            for (int j = 0; j < IDLE_COLS; j++) {
                idleFrames[frameIndex++] = sheetBuffer[i][j];
            }
        }
        workerDirection = new Vector2();
        workerTargetPosition = new Vector2();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined);
        positionScreen = transformation.transform(workerPosition.x, workerPosition.y);

        if (workerState == UnitState.WALKING) {
            // update worker position given its moving direction
            workerPosition.x += WORKER_SPEED * workerDirection.x * delta;
            workerPosition.y += WORKER_SPEED * workerDirection.y * delta;

            // check if worker has reached target position
            float distanceToTarget = workerPosition.dst(workerTargetPosition);
            // Gdx.app.log("distance to target", "" + distanceToTarget);
            if (distanceToTarget <= ARRIVED_TO_TARGET_DISTANCE) {
                /*
                 * TODO this will probably have to be adjusted to take the uniti size into
                 * consideration when I start checkin for collisions
                 */
                workerState = UnitState.IDLE;
            }

        }
        game.spriteBatch.begin();
        mapRenderer.render(game.spriteBatch);
        renderIdleWorker(game.spriteBatch, workerFacingDirection, idleFrames, workerPosition);
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
            workerTargetPosition = clickPositionToIso(Gdx.input.getX(), Gdx.input.getY(), camera);

            Gdx.app.log("targetLocation", "x:" + workerTargetPosition.x + ",y: " + workerTargetPosition.y);
            workerState = UnitState.WALKING;
            workerDirection.set(workerTargetPosition);
            workerDirection.sub(workerPosition).nor();
            float workerAngle = MathUtils.radiansToDegrees * MathUtils.atan2(workerDirection.y, workerDirection.x);
            Gdx.app.log("worker angle", "" + workerAngle);
            if (workerAngle >= -22.5f && workerAngle < 22.5f)
                workerFacingDirection = FacingDirection.SOUTHEAST;
            else if (workerAngle >= 22.5f && workerAngle < 67.5f)
                workerFacingDirection = FacingDirection.EAST;
            else if (workerAngle >= 67.5f && workerAngle < 112.5f)
                workerFacingDirection = FacingDirection.NORTHEAST;
            else if (workerAngle >= 112.5f && workerAngle < 157.5f)
                workerFacingDirection = FacingDirection.NORTH;
            else if (workerAngle >= 157.5f && workerAngle < 180.0f || workerAngle >= -180f && workerAngle < -157.5f)
                workerFacingDirection = FacingDirection.NORTHWEST;
            else if (workerAngle >= -157.5f && workerAngle < -112.5f)
                workerFacingDirection = FacingDirection.WEST;
            else if (workerAngle >= -112.5f && workerAngle < -67.5f)
                workerFacingDirection = FacingDirection.SOUTHWEST;
            else if (workerAngle >= -67.5f && workerAngle < -22.5f)
                workerFacingDirection = FacingDirection.SOUTH;

            Gdx.app.log("worker direction", "x:" + workerDirection.x + ",y: " + workerDirection.y);

            // projectedClick = viewport.project(new Vector2(clickX, clickY));
        }
        /*
         * game.hudBatch.begin();
         * game.font.draw(game.hudBatch, "clickX: " + clickX, 100, 100);
         * game.font.draw(game.hudBatch, "projected clickX: " + projectedClick.x, 100,
         * 85);
         * game.font.draw(game.hudBatch, "clickY: " + clickY, 100, 60);
         * game.font.draw(game.hudBatch, "projected clickY: " + projectedClick.y, 100,
         * 45);
         * game.hudBatch.end();
         */
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

    private void renderIdleWorker(SpriteBatch batch, FacingDirection facingDirection, TextureRegion[] frames,
            Vector2 positionIso) {
        positionScreen = transformation.transform(positionIso.x, positionIso.y);
        TextureRegion frame = null;
        boolean flipX = false;
        switch (facingDirection) {
            case NORTH:
                frame = frames[0];
                break;
            case NORTHEAST:
                frame = frames[1];
                break;
            case EAST:
                frame = frames[2];
                break;
            case SOUTHEAST:
                frame = frames[3];
                break;
            case SOUTH:
                frame = frames[4];
                break;
            case SOUTHWEST:
                frame = frames[3];
                flipX = true;
                break;
            case WEST:
                frame = frames[2];
                flipX = true;
                break;
            case NORTHWEST:
                frame = frames[1];
                flipX = true;
                break;
        }
        Texture texture = frames[0].getTexture();
        batch.draw(texture, positionScreen.x, positionScreen.y, frame.getRegionWidth() / 2, 0,
                texture.getWidth() / IDLE_COLS,
                texture.getHeight() / IDLE_ROWS, 4, 4, 0,
                frame.getRegionX(), frame.getRegionY(), frame.getRegionWidth(),
                frame.getRegionHeight(), flipX, false);
    }

}
