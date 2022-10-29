package com.mygdx.isometricgame1;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EntityManager {

    public ArrayList<Entity> entities;
    Ant ant;
    GameScreen screen;
    private SpriteBatch batch;
    private ShapeDrawer shapeDrawer;

    public EntityManager(GameScreen screen, SpriteBatch batch, ShapeDrawer shapeDrawer) {
        this.screen = screen;
        entities = new ArrayList<>();
        this.batch = batch;
        this.shapeDrawer = shapeDrawer;
    }

    public Ant getAnt() {
        return ant;
    }

    public void add(Entity entity) {
        entities.add(entity);
    }

    public void orderByZIndex() {
        // TODO implement more efficient sorting algorithm
        Entity temp;
        for (int i = 0; i < entities.size() - 1; i++) {
            Entity current = entities.get(i);
            Entity next = entities.get(i + 1);

            if (current.getZIndex() > next.getZIndex()) {
                temp = current;
                entities.set(i, next);
                entities.set(i + 1, temp);
            }
        }
    }

    public void update(float delta) {
        for (Entity entity : entities) {
            entity.setColliding(false);
            if (entity instanceof Ant) {
                Ant ant = (Ant) entity;
                ant.update(delta, entities);
                continue;
            }
        }
        orderByZIndex();
    }

    public void draw() {
        for (Entity entity : entities) {
            if (entity instanceof Ant) {
                Ant ant = (Ant) entity;
                ant.getCircle().draw(shapeDrawer, ant.isColliding() ? Color.RED : Color.WHITE);
                // TODO Get reference property to SpriteBatch out of Ant class
                ant.draw(batch);
                continue;
            }
            if (entity instanceof Rock) {
                Rock rock = (Rock) entity;
                rock.getSquare().draw(shapeDrawer, rock.isColliding() ? Color.RED : Color.WHITE);
                // TODO Get reference property to SpriteBatch out of Rock class
                rock.draw(batch);
            }
        }
    }

    public Rock[] getRocks() {
        ArrayList<Rock> rocks = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Rock)
                rocks.add((Rock) entity);
        }
        return rocks.toArray(new Rock[0]);
    }

    //TODO refactor as "init entities from map" or smt like that
    public void parseMap(TiledMap map) {
        //Read https://lightrun.com/answers/libgdx-libgdx-make-use-of-tiledmapobjects-templates
        MapLayers layers = map.getLayers();
        MapObjects objects = layers.get("objects").getObjects();
        Vector2 position = new Vector2();

        for (MapObject object : objects) {
            /*Gdx.app.log("object", "" + object.getName());
            MapProperties mapProps = object.getProperties();
            Iterator<String> iter = mapProps.getKeys();
            while (iter.hasNext()) {
                String key = iter.next();
                Gdx.app.log(key, mapProps.get(key).toString());
            }*/
            if (object.getName().equals("Ant")) {

                ant = new Ant(screen, screen.getTransformation());
                position = fromMapPosition((float) object.getProperties().get("x"), (float) object.getProperties().get("y"), position);
                position.add(0.5f, 0.5f);
                ant.setPosition(position);
                entities.add(ant);
            }
            if (object.getName().equals("Rock")) {
                position = fromMapPosition((float) object.getProperties().get("x"), (float) object.getProperties().get("y"), position);
                entities.add(new Rock(screen, screen.getTransformation(), (int) position.x, (int) position.y));

            }
        }
    }

    private Vector2 fromMapPosition(float mapX, float mapY, Vector2 position) {
        return position.set((mapX / 64.0f) - 1, (mapY / 64.0f));
    }
}

