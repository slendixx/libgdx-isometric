package com.mygdx.isometricgame1;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ant extends Entity {

    private static final int FACING_DIRECTION_AMOUNT = 16;
    private static final int SPRITE_DIRECTION_AMOUNT = 9;
    private static final float WALKING_FRAME_DURATION = 1 / 26f;
    private static final float IDLE_FRAME_DURATION = 1 / 10f;
    private static final float SPEED = 1.4f;
    private static final float ARRIVED_TO_TARGET_POSITION_DISTANCE = 0.05f;
    private static TextureAtlas walkingAtlas = new TextureAtlas(
            Gdx.files.internal("sprites/ant/ant-walking.atlas"));
    private static TextureAtlas idleAtlas = new TextureAtlas(
            Gdx.files.internal("sprites/ant/ant-idle-0.atlas"));
    private static HashMap<Integer, Animation<TextureRegion>> walkingAnimations;
    private static HashMap<Integer, Animation<TextureRegion>> idleAnimations;
    private static float elapsedTime = 0;
    private final float ANIMATION_OFFSET_Y = 0.3f;
    private final float ANIMATION_OFFSET_X = 0.5f;
    private final float RADIUS = 25;
    private final float UPDATE_FACING_DIRECTION_DELAY = 0.25f;
    private Vector2 positionScreen;
    // TODO remove duplication of this property
    private TiledIsoTransformation transformation;
    private Vector2 directionVector;
    private int facingDirection; // 0-15
    private float updateFacingDirectionTimer;
    private UnitState state = UnitState.IDLE;
    private Vector2 targetPosition;
    /*
     * for collision detection. Visually, the center of the circle represents the
     * ant's position in the world visually.
     */
    private IsometricCircle circle;
    private GraphPath<NavNode> path;
    /**
     * Used for iterating the list of NavNodes from the path
     */
    private int navNodeIndex;

    public Ant(GameScreen screen, TiledIsoTransformation transformation) {
        super(screen);

        this.transformation = transformation;

        // init animations
        walkingAnimations = initWalkingAnimations();
        idleAnimations = initIdleAnimations();

        // init private properties
        position = new Vector2(0.5f, 0.5f);
        directionVector = new Vector2();
        targetPosition = new Vector2();
        facingDirection = 0;
        updateFacingDirectionTimer = UPDATE_FACING_DIRECTION_DELAY;

        circle = new IsometricCircle(RADIUS);

        path = null;
        navNodeIndex = 0;
    }

    public void setTargetPosition(Vector2 targetPosition) {
        this.targetPosition = targetPosition;
    }

    public void setState(UnitState state) {
        this.state = state;
    }

    public void setPath(GraphPath<NavNode> path) {
        this.path = path;
        //set target position to path's next node
        navNodeIndex = 0;
        setTargetPosition(path.get(navNodeIndex).getPosition());
    }

    private HashMap<Integer, Animation<TextureRegion>> initWalkingAnimations() {
        walkingAnimations = new HashMap<Integer, Animation<TextureRegion>>();
        int animationIndex = 9;
        // init animations for directions 0-8
        for (int i = 0; i < SPRITE_DIRECTION_AMOUNT; i++) {
            walkingAnimations.put(i, new Animation<TextureRegion>(WALKING_FRAME_DURATION,
                    walkingAtlas.findRegions("walking-" + i), PlayMode.LOOP));
        } // init animations with flipped sprites for directions 9-15
        // we need to store again references to sprites for directions 1-7
        for (int i = 7; i > 0; i--) {
            walkingAnimations.put(animationIndex++, walkingAnimations.get(i));
        }

        return walkingAnimations;
    }

    private HashMap<Integer, Animation<TextureRegion>> initIdleAnimations() {
        idleAnimations = new HashMap<Integer, Animation<TextureRegion>>();
        int animationIndex = 9;
        // init animations for directions 0-8
        for (int i = 0; i < SPRITE_DIRECTION_AMOUNT; i++) {
            idleAnimations.put(i, new Animation<TextureRegion>(IDLE_FRAME_DURATION,
                    idleAtlas.findRegions("idle-" + i), PlayMode.LOOP));
        } // init directions with flipped sprites for directions 9-15
        // we need to store again references to sprites for directions 1-7
        for (int i = 7; i > 0; i--) {
            idleAnimations.put(animationIndex++, idleAnimations.get(i));
        }
        return idleAnimations;
    }

    public void update(float delta, ArrayList<Entity> entities) {
        //repeat until ant reaches goal:
        //  every frame, move ant towards next NavNode on the path.
        //  if ant has reached current waypoint, pop it
        //end


        super.update(delta);
        elapsedTime += delta;
        if (state == UnitState.WALKING) {
            //path following:
            //updateDirection
            updateFacingDirection(targetPosition);
            //if arrived to target position
            //  set target position to next in path
            position.x += SPEED * directionVector.x * delta;
            position.y += SPEED * directionVector.y * delta;

            for (Entity entity : entities) {
                /*
                 * when checking for collision with ants, remember to check
                 * if entity is a reference to the object itself
                 */
                if (entity instanceof Rock)
                    collide(((Rock) entity).getSquare());
            }

            // Gdx.app.log("position", "x:" + antPosition.x + " y:" + antPosition.y);
            float distanceToTarget = position.dst(targetPosition);

            if (distanceToTarget <= ARRIVED_TO_TARGET_POSITION_DISTANCE) {
                /*
                 * TODO this check will probably have to be adjusted to take the unit size into
                 * consideration when I start checkin for collisions
                 */
                if (navNodeIndex < path.getCount() - 1) {
                    setTargetPosition(path.get(++navNodeIndex).getPosition());
                } else {
                    state = UnitState.IDLE;
                }

                // position.set(targetPosition);
            }
        }
        circle.setPosition(position);
    }

    public void updateFacingDirection(Vector2 targetPosition) {
        this.targetPosition = targetPosition;
        // Gdx.app.log("target position", "x:" + antTargetPosition.x + " y:" +
        // antTargetPosition.y);
        //state = UnitState.WALKING;
        directionVector.set(targetPosition);
        directionVector.sub(position).nor();
        float angle = MathUtils.radiansToDegrees * MathUtils.atan2(directionVector.y, directionVector.x);

        if (angle >= 146.25 && angle < 168.75)
            facingDirection = 15;
        else if (angle >= 168.75 && angle < 180.0 || angle >= -180 && angle < -168.75)
            facingDirection = 14;
        else if (angle >= -168.75 && angle < -146.25)
            facingDirection = 13;
        else if (angle >= -146.25 && angle < -123.75)
            facingDirection = 12;
        else if (angle >= -123.75 && angle < -101.25)
            facingDirection = 11;
        else if (angle >= -101.25 && angle < -78.75)
            facingDirection = 10;
        else if (angle >= -78.75 && angle < -56.25)
            facingDirection = 9;
        else if (angle >= -56.25 && angle < -33.75)
            facingDirection = 8;
        else if (angle >= -33.75 && angle < -11.25)
            facingDirection = 7;
        else if (angle >= -11.25 && angle < 11.25)
            facingDirection = 6;
        else if (angle >= -11.25 && angle < 33.75)
            facingDirection = 5;
        else if (angle >= 33.75 && angle < 56.25)
            facingDirection = 4;
        else if (angle >= 56.25 && angle < 78.75)
            facingDirection = 3;
        else if (angle >= 78.75 && angle < 101.25)
            facingDirection = 2;
        else if (angle >= 101.25 && angle < 123.75)
            facingDirection = 1;
        else if (angle >= 123.75 && angle < 146.25)
            facingDirection = 0;
    }

    public IsometricCircle getCircle() {
        return circle;
    }

    public void draw(SpriteBatch batch) {

        boolean flipX = false;
        if (facingDirection >= 9 && facingDirection < FACING_DIRECTION_AMOUNT) {
            flipX = true;
        }
        // TODO overload transform method to receive a Vector2
        // TODO remove positionScreen from this class. Perhaps receive positionScreen as
        // argument
        positionScreen = transformation.transform(position.x, position.y);

        TextureRegion currentFrame = null;

        switch (state) {
            case IDLE:
                currentFrame = idleAnimations.get(facingDirection).getKeyFrame(elapsedTime);
                break;
            case WALKING:
                currentFrame = walkingAnimations.get(facingDirection).getKeyFrame(elapsedTime);
                break;
        }

        batch.draw(currentFrame.getTexture(), positionScreen.x - currentFrame.getRegionWidth() * ANIMATION_OFFSET_X,
                positionScreen.y - currentFrame.getRegionHeight() * ANIMATION_OFFSET_Y,
                0, 0,
                currentFrame.getRegionWidth(),
                currentFrame.getRegionHeight(), 1, 1, 0,
                currentFrame.getRegionX(), currentFrame.getRegionY(), currentFrame.getRegionWidth(),
                currentFrame.getRegionHeight(), flipX, false);
    }

    public void dispose() {
        // TODO dispose of EVERYTHING
    }

    public boolean intersects(IsometricSquare square) {
        /*
         * source
         * https://stackoverflow.com/questions/401847/circle-rectangle-collision-
         * detection-intersection
         */
        Vector2 circlePosition = circle.getPosition();
        Vector2 squarePosition = square.getPosition();

        // TODO refactor global constant 128 "TILE WIDTH"
        float distanceToSquareX = 128 * Math.abs(circlePosition.x - squarePosition.x);
        float distanceToSquareY = 128 * Math.abs(circlePosition.y - squarePosition.y);

        float semiSquareLength = 128 / 2;

        if (distanceToSquareX > (semiSquareLength + RADIUS))
            return false;
        if (distanceToSquareY > (semiSquareLength + RADIUS))
            return false;

        if (distanceToSquareX <= (semiSquareLength))
            return true;
        if (distanceToSquareY <= (semiSquareLength))
            return true;

        double distanceToSquareCorner = Math.pow((distanceToSquareX - semiSquareLength), 2)
                + Math.pow((distanceToSquareY - semiSquareLength), 2);

        return distanceToSquareCorner <= Math.pow(RADIUS, 2);
    }

    public void collide(IsometricSquare square) {

        Vector2 squarePosition = square.getPosition();
        Vector2 circlePosition = this.circle.getPosition();

        Vector2 nearestPoint = new Vector2(
                (float) Math.max(squarePosition.x - 0.5, Math.min(squarePosition.x + 0.5, position.x)),
                (float) Math.max(squarePosition.y - 0.5, Math.min(squarePosition.y + 0.5, position.y)));

        float nearestY = 128 * (nearestPoint.y - circlePosition.y);
        float nearestX = 128 * (nearestPoint.x - circlePosition.x);
        Vector2 toNearest = new Vector2(nearestX, nearestY);
        double distanceToNearestPoint = Math.sqrt(nearestX * nearestX +
                nearestY * nearestY);

        if (distanceToNearestPoint <= RADIUS + 1) {

            position.sub(toNearest.nor().scl((float) (RADIUS -
                    distanceToNearestPoint) / 128));

        }
    }
}
