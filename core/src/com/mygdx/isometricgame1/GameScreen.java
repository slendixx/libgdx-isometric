package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameScreen implements Screen {

    /**
     * to be subtracted to world-border-matching coordinates to prevent them from
     * wrapping back to zero.
     */
    private final float ANTI_WRAP_MARGIN = 0.1f;
    private final int VIEWPORT_WIDTH = 1600;
    private final int VIEWPORT_HEIGHT = 900;
    private final int MAP_WIDTH;
    private final int MAP_HEIGHT;
    private final float cameraSpeed = 20;
    // TODO refactor these and other constants into game class to remove duplication
    private final boolean showFPS = false;
    // shapeDrawer
    Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
    Texture shapeDrawerColorWhite;
    Texture shapeDrawerColorRed;
    TextureRegion shapeDrawerTextureWhite;
    TextureRegion shapeDrawerTextureRed;
    ShapeDrawer shapeDrawerWhite;
    ShapeDrawer shapeDrawerRed;
    // EntityManager
    EntityManager entityManager;
    Ant ant;
    private IsometricGame1 game;
    private ExtendViewport viewport;
    private OrthographicCamera camera;
    private TiledMap map;
    private MapRenderer mapRenderer;
    private TiledIsoTransformation transformation;
    private byte[] navGrid;
    private NavGraph navGraph;
    private GraphPath<NavNode> path;
    private RayCaster rayCaster;
    private PathSmoother pathSmoother;

    // TODO try to incorporate a viewport to prevent distortion when resizing screen
    public GameScreen(
            IsometricGame1 game) {
        this.game = game;
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.setToOrtho(false);
        viewport = new ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        map = new TmxMapLoader().load("maps/export/map-1.tmx");
        /*
         * MapProperties props = map.getProperties();
         * Iterator<String> iter = props.getKeys();
         * while (iter.hasNext()) {
         * String key = iter.next();
         * Gdx.app.log(key, "" + props.get(key));
         * }
         */
        MAP_HEIGHT = (Integer) map.getProperties().get("height");
        MAP_WIDTH = (Integer) map.getProperties().get("width");

        mapRenderer = new MapRenderer(map);
        transformation = new TiledIsoTransformation(Utils.TILE_WIDTH, Utils.TILE_HEIGHT);

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

        entityManager = new EntityManager(this, game.spriteBatch, shapeDrawerWhite);

        entityManager.parseMap(map);


        ant = entityManager.getAnt();

        //update nav grid to reflect rock positions
        /* for (Rock rock : rocks) {
         *//*  TODO better obstacle entity management
                Rocks and other obstacles could have an index property that reflects their tile position.
                Entity manager could store them in a hash map that is indexed by this formula:
                    y * MAP_WIDTH + x

                This could prevent me from having to use this "collision" matrix to build my nav graph.
             *//*
            navGrid[(int) (rock.position.y * MAP_WIDTH + rock.position.x)] = 1;
        }*/
        //init navGraph
        navGraph = new NavGraph();
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                navGraph.addNode(new NavNode(x + 0.5f, y + 0.5f));
            }
        }
        Array<NavNode> nodes = navGraph.getNodes();
        //set occupied nodes as obstacles
        for (Rock rock : entityManager.getRocks()) {
            nodes.get(((int) rock.position.y * MAP_WIDTH + (int) rock.position.x)).setIsObstacle(true);
        }
        //connect adjacent nodes
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                int currentIndex = (y * MAP_WIDTH) + x;
                NavNode current = nodes.get(currentIndex);
                if (current.isObstacle())
                    continue;

                try {
                    NavNode above = nodes.get((y - 1) * MAP_WIDTH + x);
                    if (!above.isObstacle())
                        navGraph.connectNodes(current, above);
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    NavNode below = nodes.get((y + 1) * MAP_WIDTH + x);
                    if (!below.isObstacle())
                        navGraph.connectNodes(current, below);
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (x == MAP_WIDTH - 1)
                        throw new IndexOutOfBoundsException();

                    NavNode toTheRight = nodes.get(y * MAP_WIDTH + (x + 1));
                    if (!toTheRight.isObstacle())
                        navGraph.connectNodes(current, toTheRight);
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (x == 0)
                        throw new IndexOutOfBoundsException();
                    NavNode toTheLeft = nodes.get(y * MAP_WIDTH + (x - 1));
                    if (!toTheLeft.isObstacle())
                        navGraph.connectNodes(current, toTheLeft);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
        path = null;
        //path = navGraph.findPath(nodes.get(0), nodes.get(156));
        rayCaster = new RayCaster(navGraph, MAP_WIDTH, MAP_HEIGHT);
        pathSmoother = new PathSmoother(rayCaster);

        new BreadthFirstSearcher(navGraph).identifySubGraphs();
    }

    public TiledIsoTransformation getTransformation() {
        return transformation;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (showFPS) {
            Gdx.app.log("FPS",
                    "" + Gdx.graphics.getFramesPerSecond());
        }
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();
        mapRenderer.render(game.spriteBatch);


        //render entities
        entityManager.update(delta);
        entityManager.draw();
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

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            Vector3 mousePositionUnprojected = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            Vector2 mousePosition = transformation.untransform(mousePositionUnprojected.x, mousePositionUnprojected.y);
            Vector2 goal = new Vector2(mousePosition.y, mousePosition.x);
            Vector2 intersectionPosition = rayCaster.findIntersection(ant.getPosition(), goal, true);
            IsometricLine ray = new IsometricLine(ant.getPosition(), goal);
            game.spriteBatch.begin();
            if (intersectionPosition == null) {
                ray.draw(shapeDrawerWhite, Color.GREEN);
            }
            if (intersectionPosition != null) {
                ray.draw(shapeDrawerWhite, Color.RED);
                IsometricCircle intersectionCircle = new IsometricCircle(20f);
                intersectionCircle.setPosition(intersectionPosition);
                intersectionCircle.draw(shapeDrawerRed);
            }
            game.spriteBatch.end();
        }

        if (Gdx.input.justTouched()) {
            //ant.updateFacingDirection(Utils.clickPositionToIso(Gdx.input.getX(), Gdx.input.getY(), camera));
            //get ant tile
            int antTileX = (int) Math.floor(ant.getPosition().x);
            int antTileY = (int) Math.floor(ant.getPosition().y);
            NavNode start = new NavNode(ant.getPosition().x, ant.getPosition().y);
            //get NavNode that corresponds with the tile the ant is standing on
            NavNode closestToStart = navGraph.getNodes().get(antTileY * MAP_WIDTH + antTileX);
            //add start to navGraph
            navGraph.addNode(start);
            //assign it's connections to start
            for (Connection<NavNode> connection :
                    navGraph.getConnections(closestToStart)) {
                navGraph.connectNodes(start, connection.getToNode());
            }
            //get click position
            Vector3 clickUnprojected = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            Vector2 clickPosition = transformation.untransform(clickUnprojected.x, clickUnprojected.y);
            //clamp click position to map borders
            //TODO implement a clamp utility
            clickPosition.set(Math.max(0f, Math.min(MAP_HEIGHT - ANTI_WRAP_MARGIN, clickPosition.x)), Math.max(0f, Math.min(MAP_WIDTH - ANTI_WRAP_MARGIN, clickPosition.y)));
            int clickTileX = (int) Math.floor(clickPosition.y); //for some reason, untransforming these coordinates
            int clickTileY = (int) Math.floor(clickPosition.x); //ends up flipping them

            NavNode closestToClick = navGraph.getNodes().get(clickTileY * MAP_WIDTH + clickTileX);


            NavNode goal;
            if (closestToClick.isObstacle()) {
                Vector2 intersectionPosition = rayCaster.findIntersection(new Vector2(clickPosition.y, clickPosition.x), ant.getPosition(), false);
                Utils.collide(ant.getCircle(), (int) intersectionPosition.x, (int) intersectionPosition.y, intersectionPosition);
                goal = new NavNode(intersectionPosition.x, intersectionPosition.y);
            } else {
                goal = new NavNode(clickPosition.y, clickPosition.x);
            }
            //add goal to navGraph
            navGraph.addNode(goal);
            //get NavNode that corresponds with the goal tile
            NavNode closestToGoal = navGraph.getNodes().get((int) (Math.floor(goal.getPosition().y) * MAP_WIDTH + Math.floor(goal.getPosition().x)));

            if (closestToStart.getSubgraphId() != closestToGoal.getSubgraphId()) {
                Gdx.app.log("Path", "goal is inaccessible");
            } else {

                //assign it's connections to goal
                for (Connection<NavNode> connection :
                        navGraph.getConnections(closestToGoal)) {
                    navGraph.connectNodes(connection.getToNode(), goal);
                }
                //find path from start to goal
                path = navGraph.findPath(start, goal);

                //smooth path
                path = pathSmoother.smooth(path);

                //render path

                //assign this new path to the ant's path property
                ant.setPath(path);
                //set ant state to WALKING
                ant.setState(UnitState.WALKING);
            }
            /*
            TODO delete start, goal and connections to them after the unit has reached it's goal
             */
        }

        //render nav graph
        game.spriteBatch.begin();
        //for (NavConnection connection : navGraph.getConnections()) {
        //    connection.draw(shapeDrawerWhite, false);
        //}
        for (NavNode navNode : navGraph.getNodes()) {
            //navNode.draw(shapeDrawerWhite, game.spriteBatch, game.font, false);
        }
        Array<NavNode> nodes = navGraph.getNodes();
        for (int i = 0; i < MAP_WIDTH * MAP_HEIGHT; i++) {
            nodes.get(i).drawSubgraphIndex(game.spriteBatch, game.font);
        }
        game.spriteBatch.end();
        game.spriteBatch.begin();
        if (path != null) {

            for (int i = 0; i < path.getCount() - 1; i++) {
                NavNode currentNode = path.get(i);
                NavNode nextNode = path.get(i + 1);
                new IsometricLine(currentNode.getPosition(), nextNode.getPosition()).draw(shapeDrawerWhite, Color.GREEN);
                currentNode.draw(shapeDrawerWhite, game.spriteBatch, game.font, true, "" + i);
            }
            path.get(path.getCount() - 1).draw(shapeDrawerWhite, game.spriteBatch, game.font, true, "" + (path.getCount() - 1));
        }
        game.spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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

    public int getMapWidth() {
        return MAP_WIDTH;
    }

    public int getMapHeight() {
        return MAP_HEIGHT;
    }

}
