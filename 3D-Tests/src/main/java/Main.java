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
    private TexturedModel world;
    private Player player;

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
        glEnable(GL_DEPTH_TEST);
        glfwSwapInterval(1);// Enable v-sync
        glfwShowWindow(window.getWindow());

        frameRate = 60;
        new Camera(); //Just here to initialize
        world = new World();
        player = new Player();
    }

    private void update(double deltaTime) {
        // Update transformations, states and other stuff here.
        {
            Input.moveCamera(deltaTime);
            world.updateMatrix(new Vector3f(), 0.0f, 1.0f);
        }
        glfwPollEvents();
    }

    private void render() {
        glClearColor(0.0f, 0.4f, 0.85f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        { // Only Render Objects Here - Objects further back are rendered first
            world.render();
            player.render();
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

    public static void main(String[] args) {
        new Main().start();
    }
}