package com.mygdx.isometricgame1;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;

public class MapRenderer {

    private final int MAP_WIDTH; // in tiles
    private final int MAP_HEIGHT; // in tiles
    private final int TILE_WIDTH; // in pixels
    private final int TILE_HEIGHT; // in pixels
    // unit vectors i & j describe the projection
    private Vector2 transformedPos;
    private ArrayList<TiledMapTileLayer> tileLayers;
    private TextureRegion textureBuffer;
    private TiledIsoTransformation transformation;

    public MapRenderer(TiledMap map) {
        tileLayers = new ArrayList<TiledMapTileLayer>();
        tileLayers.add((TiledMapTileLayer) map.getLayers().get(0));

        MapProperties props = map.getProperties();
        MAP_WIDTH = props.get("width", Integer.class);
        MAP_HEIGHT = props.get("height", Integer.class);
        TILE_WIDTH = props.get("tilewidth", Integer.class);
        TILE_HEIGHT = props.get("tileheight", Integer.class);
        transformation = new TiledIsoTransformation(TILE_WIDTH, TILE_HEIGHT);
    }

    public void render(SpriteBatch batch) {
        for (TiledMapTileLayer layer : tileLayers) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    Cell cell = layer.getCell(x, y);
                    // Perhaps could cache all textures
                    textureBuffer = cell.getTile().getTextureRegion();
                    transformedPos = transformation.transform(x, y);
                    // subtract half tile height to draw from leftmost corner of the sprite
                    batch.draw(textureBuffer, transformedPos.x, transformedPos.y - TILE_HEIGHT / 2);
                }
            }
        }
    }

}
