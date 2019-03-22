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
        world = new World();
        obstacle = new Obstacle();
    }

    private void update(double deltaTime) {
        // Update transformations, states and do collision detection here and so on. Basically everything except rendering.
        {
            Input.moveCamera(deltaTime);
            obstacle.setAttributes(new Vector3f(3.0f, 2.0f, 0.0f), 0.0f, 2.0f);
            world.setAttributes(new Vector3f(), 0.0f, 2.0f);
        }
        if (checkCollision(obstacle)){
            Camera.setPosition(new Vector3f());
        }
        glfwPollEvents();
    }

    private void render() {
        glClearColor(0.0f, 0.4f, 0.85f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        { // Only Render Objects Here - Objects further back are rendered first
            world.render();
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

    private boolean checkCollision(TexturedModel object){
        boolean collisionX = Camera.getPosition().x + 2.0f > object.getPosition().x && object.getPosition().x + object.getVertexArray().getWidth() > Camera.getPosition().x - 0.5f;
        boolean collisionY = Camera.getPosition().y + 2.0f > object.getPosition().y && object.getPosition().y + object.getVertexArray().getHeight() > Camera.getPosition().y - 0.5f;
        return collisionX && collisionY;
    }

    public static void main(String[] args) {
        new Main().start();
    }
}