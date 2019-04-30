//java -cp "C:\Users\Aron\Documents\Java Projects\3D-Tests\lwjgl\*";"C:\Users\Aron\Documents\Java Projects\3D-Tests\src" main/Main

package main.java;

import main.java.graphics.Models;
import main.java.graphics.Renderer;
import main.java.math.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL46.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL46.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL46.glClear;
import static org.lwjgl.opengl.GL46.glClearColor;

public class Main implements Runnable {

    protected Thread main;
    static Window window;

    private double frameRate;
    private Renderer renderer;
    private Camera camera;
    private Player player;
    private float worldSize;
    private World world;

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
        window = new Window(1920, 1080, false);
        GL.createCapabilities();
        /*---OpenGL code won't work before this---*/
        //glEnable(GL_DEPTH_TEST);
        glfwSwapInterval(1);// Enable v-sync
        glfwShowWindow(window.getWindow());

        frameRate = 60;
        worldSize = 512.0f;
        renderer = new Renderer();
        new Models(worldSize);
        camera = new Camera(new Vector3f(), 0.0f, 1.0f); //Just here to initialize
        player = new Player();
        world = new World();
    }

    /**
     * Updates object attributes, checks for user input, calculates game logic and checks for collisions.
     * @param deltaTime the delta time gotten from the timing circuit of the main loop. Used for physics.
     */
    private void update(double deltaTime) {
        Input.moveCameraAndPlayer(deltaTime, player);
        {//Collision Detection
            if (player.isMoving()){
                for (Obstacle obstacle : world.getObstacles()){
                    if (checkCollision(player, obstacle)){
                        doCollision(getCollisionDirection(player, obstacle), obstacle);
                    }
                }
            }
        }
        player.setIsMoving(false);
        player.updateMatrix(); //TODO Fix constant player matrix updates.
        for (Obstacle obstacle : world.getObstacles()){
            obstacle.updateMatrix();
        }
        world.updateMatrix();
        glfwPollEvents();
    }

    /**
     * Renders all objects and scenes.
     */
    private void render() {
        glClearColor(0.0f, 0.4f, 0.85f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        { // Only Render Objects Here - Objects further back are rendered first
            renderer.render(world);
            for (Obstacle obstacle : world.getObstacles()){
                renderer.render(obstacle);
            }
            renderer.render(player);
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

    private boolean checkCollision(Entity obj1, Entity obj2){
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

    private EnumDirection getCollisionDirection(Entity obj1, Entity obj2){
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
        return EnumDirection.INVALIDDIR;
    }

    /**
     * Responds to a collision in the given direction. Collision response only applies to the player and camera.
     * @param direction the EnumDirection in which the collision happened.
     * @param object the object in which the collision occurred.
     */

    private void doCollision(EnumDirection direction, Entity object){
        float inside;
        switch (direction){
            case NORTH:
                inside = (object.getPosition().y + object.getHeight()) - player.getPosition().y;
                player.addPosition(new Vector3f(0.0f, inside));
                Camera.addPosition(new Vector3f(0.0f , inside * Camera.scale));
                break;
            case EAST:
                inside = (object.getPosition().x + object.getWidth()) - player.getPosition().x;
                player.addPosition(new Vector3f(inside, 0.0f));
                Camera.addPosition(new Vector3f(inside * Camera.scale, 0.0f));
                break;
            case WEST:
                inside = (player.getPosition().x + player.getWidth()) - object.getPosition().x;
                player.addPosition(new Vector3f(-inside, 0.0f));
                Camera.addPosition(new Vector3f(-inside * Camera.scale, 0.0f));
                break;
            case SOUTH:
                inside = (player.getPosition().y + player.getHeight()) - object.getPosition().y;
                player.addPosition(new Vector3f(0.0f, -inside));
                Camera.addPosition(new Vector3f(0.0f, -inside * Camera.scale));
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
        SOUTH,
        INVALIDDIR
    }
}