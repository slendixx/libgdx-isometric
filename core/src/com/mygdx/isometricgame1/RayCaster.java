package com.mygdx.isometricgame1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class RayCaster {

    private final float MAX_DISTANCE = 100.0f;
    private final int MAP_WIDTH;
    private final int MAP_HEIGHT;
    private ObstacleManager obstacleManager;

    public RayCaster(ObstacleManager obstacleManager, int gameMapWidth, int gameMapHeight) {
        this.obstacleManager = obstacleManager;
        MAP_WIDTH = gameMapWidth;
        MAP_HEIGHT = gameMapHeight;
    }

    public Vector2 findIntersection(Vector2 start, Vector2 goal) {
        Vector2 direction = new Vector2(goal.x - start.x, goal.y - start.y).nor();
        Vector2 rayUnitStepSize = new Vector2((float) Math.sqrt(1 + (direction.y / direction.x) * (direction.y / direction.x)), (float) Math.sqrt(1 + (direction.x / direction.y) * (direction.x / direction.y)));
        int checkX = (int) Math.floor(start.x);
        int checkY = (int) Math.floor(start.y);
        Vector2 rayLength = new Vector2();
        Vector2 step = new Vector2(1, 1);
        if (direction.x < 0) {
            step.x = -1;
            rayLength.x = (start.x - (float) checkX) * rayUnitStepSize.x;
        } else {
            step.x = 1;
            rayLength.x = ((float) (checkX + 1) - start.x) * rayUnitStepSize.x;
        }
        if (direction.y < 0) {
            step.y = -1;
            rayLength.y = (start.y - (float) checkY) * rayUnitStepSize.y;
        } else {
            step.y = 1;
            rayLength.y = ((float) (checkY + 1) - start.y) * rayUnitStepSize.y;
        }

        boolean tileFound = false;
        boolean loop = true;
        float distance = 0f;
        while (!tileFound && distance < MAX_DISTANCE && loop) {
            //walk a tile of distance on one axis
            if (rayLength.x < rayLength.y) {
                checkX += step.x;
                distance = rayLength.x;
                rayLength.x += rayUnitStepSize.x;
            } else {
                checkY += step.y;
                distance = rayLength.y;
                rayLength.y += rayUnitStepSize.y;
            }

            //make sure the ray doesn't go out of bounds
            if (checkX >= 0 && checkX < MAP_WIDTH && checkY >= 0 && checkY < MAP_HEIGHT) {
                if (checkX == Math.floor(goal.x) && checkY == Math.floor(goal.y)) {
                    loop = false;
                }
                if (obstacleManager.tileIsObstacle(checkX, checkY, MAP_WIDTH)) {
                    tileFound = true;
                }
            }
            //get the point of intersection with the obstacle
            if (tileFound) {
                Vector2 intersectionPoint = new Vector2();
                intersectionPoint.set(start).add(direction.scl(distance));
                return intersectionPoint;
            }
        }
        return null;
    }

    public boolean nodesHaveVisibility(NavNode start, NavNode goal) {
        //javidx9's super fast raycasting algorithm modified to check wether or not we find an obstacle between start and goal
        Vector2 direction = new Vector2(goal.getPosition().x - start.getPosition().x, goal.getPosition().y - start.getPosition().y).nor();
        Vector2 rayUnitStepSize = new Vector2((float) Math.sqrt(1 + (direction.y / direction.x) * (direction.y / direction.x)), (float) Math.sqrt(1 + (direction.x / direction.y) * (direction.x / direction.y)));
        int checkX = (int) Math.floor(start.getPosition().x);
        int checkY = (int) Math.floor(start.getPosition().y);
        Vector2 rayLength = new Vector2();
        Vector2 step = new Vector2(1, 1);
        if (direction.x < 0) {
            step.x = -1;
            rayLength.x = (start.getPosition().x - (float) checkX) * rayUnitStepSize.x;
        } else {
            step.x = 1;
            rayLength.x = ((float) (checkX + 1) - start.getPosition().x) * rayUnitStepSize.x;
        }
        if (direction.y < 0) {
            step.y = -1;
            rayLength.y = (start.getPosition().y - (float) checkY) * rayUnitStepSize.y;
        } else {
            step.y = 1;
            rayLength.y = ((float) (checkY + 1) - start.getPosition().y) * rayUnitStepSize.y;
        }

        boolean tileFound = false;
        float distance = 0f;
        int goalX = (int) Math.floor(goal.getPosition().x);
        int goalY = (int) Math.floor(goal.getPosition().y);
        while (!tileFound && distance < MAX_DISTANCE) {
            //make sure the ray doesn't go out of bounds
            if (checkX >= 0 && checkX < MAP_WIDTH && checkY >= 0 && checkY < MAP_HEIGHT) {
                if (checkX == goalX && checkY == goalY) {
                    return true;
                }
                if (obstacleManager.tileIsObstacle(checkX, checkY, MAP_WIDTH))
                    return false;
            }
            //walk a tile of distance on one axis
            if (rayLength.x < rayLength.y) {
                checkX += step.x;
                distance = rayLength.x;
                rayLength.x += rayUnitStepSize.x;
            } else {
                checkY += step.y;
                distance = rayLength.y;
                rayLength.y += rayUnitStepSize.y;
            }


            /*
            //get the point of intersection with the obstacle
            //not needed in this game
            Vector2 intersectionPoint;
            if(tileFound){
                intersectionPoint = new Vector2();
                intersectionPoint.set(start.getPosition()).add(direction.scl(distance));
            }
             */
        }
        //if tile was found, nodes don't have visibility
        return true;

    }

    @Deprecated
    public boolean nodesHaveVisibilityVariation(NavNode start, NavNode goal) {
        //implement javidx9's super fast raycasting algorithm to obtain x & y coordinates for the obstacleProvider
        Vector2 direction = new Vector2(goal.getPosition().x - start.getPosition().x, goal.getPosition().y - start.getPosition().y).nor();
        Vector2 rayUnitStepSize = new Vector2((float) Math.sqrt(1 + (direction.y / direction.x) * (direction.y / direction.x)), (float) Math.sqrt(1 + (direction.x / direction.y) * (direction.x / direction.y)));
        int checkX = (int) Math.floor(start.getPosition().x);
        int checkY = (int) Math.floor(start.getPosition().y);
        Vector2 rayLength = new Vector2();
        Vector2 step = new Vector2(1, 1);
        if (direction.x < 0) {
            step.x = -1;
            rayLength.x = (start.getPosition().x - (float) checkX) * rayUnitStepSize.x;
        } else {
            step.x = 1;
            rayLength.x = ((float) (checkX + 1) - start.getPosition().x) * rayUnitStepSize.x;
        }
        if (direction.y < 0) {
            step.y = -1;
            rayLength.y = (start.getPosition().y - (float) checkY) * rayUnitStepSize.y;
        } else {
            step.y = 1;
            rayLength.y = ((float) (checkY + 1) - start.getPosition().y) * rayUnitStepSize.y;
        }

        boolean tileFound = false;
        float distance = 0f;
        boolean loop = true;

        while (!tileFound && distance < MAX_DISTANCE && loop) {

            //walk a tile of distance on one axis
            if (rayLength.x < rayLength.y) {
                checkX += step.x;
                distance = rayLength.x;
                rayLength.x += rayUnitStepSize.x;
            } else {
                checkY += step.y;
                distance = rayLength.y;
                rayLength.y += rayUnitStepSize.y;
            }

            //make sure the ray doesn't go out of bounds
            if (checkX >= 0 && checkX < MAP_WIDTH && checkY >= 0 && checkY < MAP_HEIGHT) {
                if (checkX == Math.floor(goal.getPosition().x) && checkY == Math.floor(goal.getPosition().y)) {
                    loop = false;
                }
                if (obstacleManager.tileIsObstacle(checkX, checkY, MAP_WIDTH))
                    tileFound = true;
            }
            /*
            //get the point of intersection with the obstacle
            //not needed in this game
            Vector2 intersectionPoint;
            if(tileFound){
                intersectionPoint = new Vector2();
                intersectionPoint.set(start.getPosition()).add(direction.scl(distance));
            }
             */
        }
        //if tile was found, nodes don't have visibility
        if (!tileFound)
            return true;
        if (!loop)
            return true;

        return false;

    }
}
