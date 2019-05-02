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
import static org.lwjgl.opengl.GL46.glClearColor;

public class Main implements Runnable {

    private double frameRate;
    private Player player;
    private World world;

    protected Thread main;

    static Window window;

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
        //glEnable(GL_DEPTH_TEST);
        glfwSwapInterval(1);// Enable v-sync
        glfwShowWindow(window.getWindow());
        glClearColor(0.0f, 0.6f, 0.75f, 1.0f);

        frameRate = 60;
        new Models();
        new Camera(); //Just here to initialize
        player = new Player();
        world = new World(new Vector3f(0.0f, -1.0f));
    }

    /**
     * Updates object attributes, checks for user input, calculates game logic and checks for collisions.
     * @param deltaTime the delta time gotten from the timing circuit of the main loop. Used for physics.
     */
    private void update(double deltaTime) {
        Input.moveCameraAndPlayer(deltaTime, player, world);
        {//Collision Detection
            if (player.isMoving()){
                if (checkCollision(world)){
                    doCollision(getCollisionDirection(world), world);
                    player.updateMatrix();
                    world.updateMatrix();
                }
            }
        }
        if (player.isMoving()){
            world.updateMatrix();
        }
        player.setIsMoving(false);
        glfwPollEvents();
    }

    /**
     * Renders all objects and scenes.
     */
    private void render() {
        Renderer.clear();
        { // Only Render Objects Here - Objects further back are rendered first
            Renderer.render(world);
            Renderer.render(player);
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

    private boolean checkCollision(Entity entity){
        boolean collisionX = player.position.x + player.getWidth() > entity.position.x && entity.position.x + entity.getAABBs().getWidth() > player.position.x;
        boolean collisionY = player.position.y - player.getHeight() < entity.position.y && entity.position.y - entity.getAABBs().getHeight() < player.position.y;
        return collisionX && collisionY;
    }

    private EnumDirection getCollisionDirection(Entity entity){
        float topCollision = (entity.position.y - (player.position.y - player.getHeight()));
        float bottomCollision = player.position.y - (entity.position.y - entity.aabb.getHeight());
        float leftCollision = player.position.x + player.getWidth() - entity.position.x;
        float rightCollision = entity.position.x + entity.aabb.getWidth() - player.position.x;
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

    private void doCollision(EnumDirection direction, Entity entity) {
        float inside;
        switch (direction) {
            case NORTH:
                inside = entity.position.y - (player.position.y - player.getHeight());
                player.addPosition(new Vector3f(0.0f, inside));
                Camera.addPosition(new Vector3f(0.0f, inside * Camera.scale));
                break;
            case EAST:
                inside = entity.position.x + entity.aabb.getWidth() - player.position.x;
                player.addPosition(new Vector3f(inside, 0.0f));
                Camera.addPosition(new Vector3f(inside * Camera.scale, 0.0f));
                break;
            case WEST:
                inside = player.position.x + player.getWidth() - entity.position.x;
                player.addPosition(new Vector3f(-inside, 0.0f));
                Camera.addPosition(new Vector3f(-inside * Camera.scale, 0.0f));
                break;
            case SOUTH:
                inside = player.position.y - (entity.position.y - entity.aabb.getHeight());
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