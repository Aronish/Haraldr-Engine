//java -cp "C:\Users\Aron\Documents\Java Projects\3D-Tests\lwjgl\*";"C:\Users\Aron\Documents\Java Projects\3D-Tests\src" main/Main
package main.java;
//TODO Change physics style to vector forces.
import main.java.debug.Logger;
import main.java.graphics.Models;
import main.java.graphics.Renderer;
import main.java.level.Level;
import main.java.physics.CollisionDetector;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL46.glClearColor;

class Main implements Runnable{

    private static double frameRate;
    private Level level;
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
    public void run(){
        init();
        loop();
    }

    /**
     * Initialize the game and all it's objects and scenes.
     */
    private void init(){
        window = new Window(1280, 720, false);
        GL.createCapabilities();
        /*---OpenGL code won't work before this---*/
        glfwSwapInterval(1);
        glfwShowWindow(window.getWindow());
        glClearColor(0.2f, 0.6f, 0.65f, 1.0f);

        frameRate = 60;
        new CollisionDetector();
        new Models();
        new Camera();
        new Logger();
        level = new Level();
    }

    /**
     * Updates object attributes, checks for user input, calculates game logic and checks for collisions.
     * @param deltaTime the delta time gotten from the timing circuit of the main loop. Used for physics.
     */
    private void update(double deltaTime){
        level.updateLevel(deltaTime);
        glfwPollEvents();
    }

    /**
     * Render all objects and scenes.
     */
    private void render(){
        Renderer.clear();
        level.renderLevel();
        glfwSwapBuffers(window.getWindow());
    }

    private double dt = 1.0d / frameRate;
    private double currentTime = glfwGetTime();

    /**
     * The main game loop. Uses variable update time step and fixed rendering time step (I think).
     */
    private void loop(){
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

    public static void main(String[] args){
        new Main().start();
    }
}
