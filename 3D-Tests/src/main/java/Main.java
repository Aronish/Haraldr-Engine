//java -cp "C:\Users\Aron\Documents\Java Projects\3D-Tests\lwjgl\*";"C:\Users\Aron\Documents\Java Projects\3D-Tests\src" main/Main

package main.java;

import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Main implements Runnable {

    protected Thread main;

    static Window window;

    private double frameRate;
    private Camera camera;
    private Player player;
    private TexturedModel world;
    private TexturedModel obstacle;

    /**
     * Start a new thread with the game on it.
     */
    private void start() {
        main = new Thread(this, "main");
        main.start();
    }

    /**
     * The run method of the thread. Initializes and starts the main loop.
     */
    public void run() {
        init();
        loop();
    }

    /**
     * Initialize the game and all it's objects and scenes.
     */
    private void init() {
        window = new Window(1280, 720, false);
        GL.createCapabilities();
        /*---OpenGL code won't work before this---*/
        //glEnable(GL_DEPTH_TEST); //zhcr
        glfwSwapInterval(1);// Enable v-sync
        glfwShowWindow(window.getWindow());

        frameRate = 60;
        camera = new Camera(); //Just here to initialize
        player = new Player();
        world = new World();
        obstacle = new Obstacle();
    }

    /**
     * Updates object attributes, checks for user input, calculates game logic and checks for collisions.
     * @param deltaTime the delta time gotten from the timing circuit of the main loop. Used for physics.
     */
    private void update(double deltaTime) {
        // Update transformations, states and do collision detection here and so on. Basically everything except rendering.
        Input.moveCameraAndPlayer(deltaTime, player);
        {
            boolean isCollision = checkCollision(player, obstacle);
            if (isCollision){
                EnumDirection direction = getCollisionDirection(player, obstacle);
                if (direction != null) {
                    doCollision(direction);
                }
            }
        }
        player.setScale(2.0f);
        obstacle.setAttributes(new Vector3f(3.0f, 2.0f, 0.0f), 0.0f, 2.0f);
        world.setAttributes(new Vector3f(), 0.0f, 2.0f);
        glfwPollEvents();
    }

    /**
     * Renders all objects and scenes.
     */
    private void render() {
        glClearColor(0.0f, 0.4f, 0.85f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        { // Only Render Objects Here - Objects further back are rendered first
            world.render();
            player.render();
            obstacle.render();
        }
        glfwSwapBuffers(window.getWindow());
    }

    private double dt = 1.0d / frameRate;
    private double currentTime = glfwGetTime();

    /**
     * The main loop which handles timing, updating and rendering.
     */
    private void loop() {
        while (!glfwWindowShouldClose(window.getWindow())) {
            double newTime = glfwGetTime();
            double frameTime = newTime - currentTime;
            currentTime = newTime;
            while (frameTime > 0.0) {
                double deltaTime = Math.min(frameTime, dt);
                update(deltaTime);
                frameTime -= deltaTime;
            }
            render();
        }
        glfwTerminate();
    }

    /**
     * Checks collision between two objects.
     * @param obj1 a TexturedModel object, obj1 should be the moving one if possible.
     * @param obj2 a preferrably static TexturedModel.
     * @return true if collision, false if not.
     */
    private boolean checkCollision(TexturedModel obj1, TexturedModel obj2){
        boolean collisionX = obj1.getPosition().x + obj1.getWidth() > obj2.getPosition().x && obj2.getPosition().x + obj2.getWidth() > obj1.getPosition().x;
        boolean collisionY = obj1.getPosition().y + obj1.getHeight() > obj2.getPosition().y && obj2.getPosition().y + obj2.getHeight() > obj1.getPosition().y;
        return collisionX && collisionY;
    }

    /**
     * Gets the direction in which the collision must have happened.
     * @param obj1 a TexturedModel object, obj1 should be the moving one if possible.
     * @param obj2 a preferrably static TexturedModel.
     * @return the EnumDirection in which the collision most likely happened, returns null if something went wrong.
     *         make sure you check for null.
     */
    private EnumDirection getCollisionDirection(TexturedModel obj1, TexturedModel obj2){
        float topCollision = (obj2.getPosition().y + obj2.getHeight() - obj1.getPosition().y);
        float bottomCollision = (obj1.getPosition().y + obj1.getHeight() - obj2.getPosition().y);
        float leftCollision = (obj1.getPosition().x + obj1.getWidth()) - obj2.getPosition().x;
        float rightCollision = (obj2.getPosition().x + obj2.getWidth()) - obj1.getPosition().x;
        if (topCollision < bottomCollision && topCollision < leftCollision && topCollision < rightCollision ) {
            return EnumDirection.NORTH;
        }
        if (bottomCollision < topCollision && bottomCollision < leftCollision && bottomCollision < rightCollision) {
            return EnumDirection.SOUTH;
        }
        if (leftCollision < rightCollision && leftCollision < topCollision && leftCollision < bottomCollision) {
            return EnumDirection.WEST;
        }
        if (rightCollision < leftCollision && rightCollision < topCollision && rightCollision < bottomCollision ) {
            return EnumDirection.EAST;
        }
        return null;
    }

    /**
     * Responds to a collision in the given direction.
     * @param direction the EnumDirection in which the collision happened.
     */
    private void doCollision(EnumDirection direction){
        float inside;
        switch (direction){
            case NORTH:
                inside = (obstacle.getPosition().y + obstacle.getHeight()) - player.getPosition().y;
                player.addPosition(new Vector3f(0.0f, inside, 0.0f));
                Camera.addPosition(new Vector3f(0.0f , inside, 0.0f));
                break;
            case EAST:
                inside = (obstacle.getPosition().x + obstacle.getWidth()) - player.getPosition().x;
                player.addPosition(new Vector3f(inside, 0.0f, 0.0f));
                Camera.addPosition(new Vector3f(inside, 0.0f, 0.0f));
                break;
            case WEST:
                inside = (player.getPosition().x + player.getWidth()) - obstacle.getPosition().x;
                player.addPosition(new Vector3f(-inside, 0.0f, 0.0f));
                Camera.addPosition(new Vector3f(-inside, 0.0f, 0.0f));
                break;
            case SOUTH:
                inside = (player.getPosition().y + player.getHeight()) - obstacle.getPosition().y;
                player.addPosition(new Vector3f(0.0f, -inside, 0.0f));
                Camera.addPosition(new Vector3f(0.0f, -inside, 0.0f));
                break;
        }
    }

    public static void main(String[] args) {
        new Main().start();
    }

    public enum EnumDirection {
        NORTH,
        EAST,
        WEST,
        SOUTH
    }
}