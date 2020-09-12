package oldgame.gameobject;

import haraldr.math.MathUtils;
import haraldr.scene.OrthographicCamera;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;
import oldgame.physics.PlayerMovementType;

import static oldgame.world.Grid.GRID_SIZE;

public class Player extends MovableEntity
{
    private static final float WALK_SPEED = 4.0f;
    private static final float JUMP_STRENGTH = 13.0f;
    private static final float RUN_MULTIPLIER = 3.0f;
    private static final float BOOST_MULTIPLIER = 10.0f;

    private Vector2f gridPosition = new Vector2f();
    private PlayerMovementType movementType = PlayerMovementType.STAND;
    private boolean isJumping = false;
    private boolean isRunning = false;
    private boolean isFalling = false;
    private boolean isBoosting = false;

    public Player()
    {
        this(new Vector3f(), 0.0f, 1.0f);
    }

    public Player(Vector3f position)
    {
        this(position, 0.0f, 1.0f);
    }

    public Player(Vector3f position, float scale)
    {
        this(position, 0.0f, scale);
    }

    private Player(Vector3f position, float rotation, float scale)
    {
        super(position, rotation, scale, true, GameObject.PLAYER);
    }

    public void setMovementType(PlayerMovementType movementType)
    {
        this.movementType = movementType;
    }

    public void setJumping(boolean isJumping)
    {
        this.isJumping = isJumping;
    }

    public void setRunning(boolean isRunning)
    {
        this.isRunning = isRunning;
    }

    public void setFalling(boolean isFalling)
    {
        this.isFalling = isFalling;
    }

    public void setBoosting(boolean isBoosting)
    {
        this.isBoosting = isBoosting;
    }

    public boolean isJumping()
    {
        return isJumping;
    }

    public boolean isFalling()
    {
        return isFalling;
    }

    public Vector2f getGridPosition()
    {
        return gridPosition;
    }

    public void resetPosition()
    {
        position.set(0.0f, 250.0f, 0f);
    }

    @Override
    public void update(OrthographicCamera camera, float deltaTime)
    {
        super.update(camera, deltaTime);
        gridPosition.set((float) MathUtils.fastFloor(getPosition().getX() / GRID_SIZE), (float) MathUtils.fastFloor(getPosition().getY() / GRID_SIZE));
    }

    @Override
    public void calculateMotion(OrthographicCamera camera, float deltaTime)
    {
        ///// WALKING ///////////////////////////
        if (movementType != PlayerMovementType.STAND)
        {
            scale.set(movementType.directionFactor, 1.0f);
        }
        if (isBoosting)
        {
            velocity.addX(WALK_SPEED * BOOST_MULTIPLIER * movementType.directionFactor);
        }
        else if (isRunning)
        {
            velocity.addX(WALK_SPEED * RUN_MULTIPLIER * movementType.directionFactor);
        }
        else
        {
            velocity.addX(WALK_SPEED * movementType.directionFactor);
        }
        ///// JUMPING ///////////////////////////
        if (isJumping)
        {
            if (Math.abs(gravityAcceleration) < JUMP_STRENGTH)
            {
                velocity.addY(JUMP_STRENGTH);
                calculateGravity(0.0f, deltaTime);
            }
            else
            {
                isJumping = false;
            }
        }
        else if (isFalling)
        {
            calculateGravity(JUMP_STRENGTH, deltaTime);
        }
        else
        {
            calculateGravity(0.0f, deltaTime);
        }
        addPosition(new Vector3f(velocity.getX() * deltaTime, velocity.getY() * deltaTime, 0f));
        camera.setPosition(Vector3f.add(position, gameObjectType.getModel().getAABB().getMiddle()));
    }
}