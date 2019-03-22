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

    private void start() {
        main = new Thread(this, "main");
        main.start();
    }

    public void run() {
        init();
        loop();
    }

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

    private void update(double deltaTime) {
        // Update transformations, states and do collision detection here and so on. Basically everything except rendering.
        Input.moveCameraAndPlayer(deltaTime, player);
        if (checkCollision(player, obstacle) == Directions.WEST){
            System.out.println("WEST");
            float inside = (player.getPosition().x + player.getWidth()) - obstacle.getPosition().x;
            player.addPosition(new Vector3f(-inside, 0.0f, 0.0f));
            Camera.addPosition(new Vector3f(-inside, 0.0f, 0.0f));
        }else if (checkCollision(player, obstacle) == Directions.EAST){
            System.out.println("EAST");
            /*
            float inside = (obstacle.getPosition().x + obstacle.getWidth()) - player.getPosition().x;
            player.addPosition(new Vector3f(-inside, 0.0f, 0.0f));
            Camera.addPosition(new Vector3f(-inside, 0.0f, 0.0f));
            */
        }
        player.setScale(2.0f);
        obstacle.setAttributes(new Vector3f(3.0f, 2.0f, 0.0f), 0.0f, 2.0f);
        world.setAttributes(new Vector3f(), 0.0f, 2.0f);
        glfwPollEvents();
    }

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
     * Checks collision between two objects. Obj1 should ususally be the moving player.
     * @param obj1 //TODO
     * @param obj2 //TODO
     * @return //TODO
     */
    private Directions checkCollision(TexturedModel obj1, TexturedModel obj2){
        boolean collisionX = obj1.getPosition().x + obj1.getWidth() > obj2.getPosition().x && obj2.getPosition().x + obj2.getWidth() > obj1.getPosition().x;
        boolean collisionY = obj1.getPosition().y + obj1.getHeight() > obj2.getPosition().y && obj2.getPosition().y + obj2.getHeight() > obj1.getPosition().y;
        if (collisionX && collisionY && obj1.getPosition().x < obj2.getPosition().x + obj2.getWidth() / 2) {
            return Directions.WEST;
        } else {
            return Directions.EAST;
        }
    }

    public static void main(String[] args) {
        new Main().start();
    }

    public enum Directions{
        NORTH,
        EAST,
        WEST,
        SOUTH
    }
}