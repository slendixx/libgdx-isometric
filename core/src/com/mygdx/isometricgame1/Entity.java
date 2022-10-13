package com.mygdx.isometricgame1;

import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position;
    protected int zIndex;
    protected GameScreen screen;
    protected boolean colliding;

    public Entity(GameScreen screen) {
        position = new Vector2(0, 0);
        zIndex = 0;
        updateZIndex(screen.getMapWidth(), screen.getMapHeight());
        this.screen = screen;
        colliding = false;
    }

    public boolean isColliding() {
        return colliding;
    }

    public void setColliding(boolean colliding) {
        this.colliding = colliding;
    }

    public void update(float delta) {
        updateZIndex(screen.getMapWidth(), screen.getMapHeight());
    }

    public void updateZIndex(int mapWidth, int mapHeight) {
        int rowsFromTopmostRow = (mapHeight - 1) - (int) Math.floor(position.y);
        zIndex = (rowsFromTopmostRow * mapWidth) + (int) Math.floor(position.x);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        updateZIndex(screen.getMapWidth(), screen.getMapHeight());
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
        updateZIndex(screen.getMapWidth(), screen.getMapHeight());
    }

    public int getZIndex() {
        return zIndex;
    }

}
