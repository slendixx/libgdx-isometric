package com.mygdx.isometricgame1;

public interface ObstacleManager {
    /**
     * Determines if a tile is occupied by an obstacle or not.
     * <p>
     * Implementers could use a hash function to index it's stored obstacle entities
     * in order to quickly access a NavNode map and determine if the target node is an obstacle.
     *
     * @param x target tile's x coordinate
     * @param y target tile's y coordinate
     * @return
     */
    public boolean tileIsObstacle(int x, int y, int gameMapWidth);
}
