package com.game;

import com.game.graphics.InstancedRenderer;
import com.game.graphics.Models;
import com.game.graphics.Renderer;
import com.game.level.Level;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

class Application {

    private static double frameRate;
    private double updatePeriod = 1.0d / frameRate;
    private double currentTime = glfwGetTime();
    private double timer = 0.0d;
    private int frames = 0;
    private int updates = 0;

    private Level level;
    private Window window;

    void run(){
        init();
        loop();
    }

    /**
     * Initialize the game and all it's objects and scenes.
     */
    private void init(){
        window = new Window(1280, 720, false, false);
        GL.createCapabilities();
        /*---OpenGL code won't work before this---*/
        glfwShowWindow(window.getWindow());
        Renderer.setClearColor(0.2f, 0.6f, 0.65f, 1.0f);

        frameRate = 60.0d;
        new Camera(0.5f);
        level = new Level();
    }

    /**
     * Updates object attributes, checks for user input, calculates game logic and checks for collisions.
     * @param deltaTime the delta time gotten from the timing circuit of the main loop. Used for com.game.physics.
     */
    private void update(float deltaTime){
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

    /**
     * The main game loop. Uses variable update time step and fixed rendering time step (I think). Cleans up afterwards.
     */
    private void loop(){
        update(0.0f);
        while (!glfwWindowShouldClose(window.getWindow())) {
            double newTime = glfwGetTime();
            double frameTime = newTime - currentTime;
            currentTime = newTime;
            timer += frameTime;
            if (timer >= 1.0d){
                window.setTitle("FPS: " + (int) (frames / timer) + " UPS: " + (int) (updates / timer));
                timer = 0.0d;
                frames = 0;
                updates = 0;
            }
            while (frameTime > 0.0) {
                double deltaTime = Math.min(frameTime, updatePeriod);
                if (currentTime > 2.0d){
                    update((float) deltaTime);
                    ++updates;
                }
                frameTime -= deltaTime;
            }
            render();
            ++frames;
        }
    }

    void cleanUp(){
        Models.cleanUp();
        Renderer.deleteShaders();
        InstancedRenderer.deleteShaders();
        glfwTerminate();
    }

    Window getWindow(){
        return window;
    }
}
