package com.mygdx.isometricgame1;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class EntityManager {

    public ArrayList<Entity> entities;
    private SpriteBatch batch;
    private ShapeDrawer shapeDrawer;

    public EntityManager(SpriteBatch batch, ShapeDrawer shapeDrawer) {
        entities = new ArrayList<>();
        this.batch = batch;
        this.shapeDrawer = shapeDrawer;
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
                ant.getCircle().draw(shapeDrawer);
                // TODO Get reference property to SpriteBatch out of Ant class
                ant.draw(batch);
                continue;
            }
            if (entity instanceof Rock) {
                Rock rock = (Rock) entity;
                rock.getSquare().draw(shapeDrawer);
                // TODO Get reference property to SpriteBatch out of Rock class
                rock.draw(batch);
            }
        }
    }
}
