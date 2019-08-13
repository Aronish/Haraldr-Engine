package com.game;

import com.game.debug.Logger;
import com.game.event.Event;
import com.game.event.IEventCallback;
import com.game.graphics.InstancedRenderer;
import com.game.graphics.Models;
import com.game.graphics.Renderer;
import com.game.layer.Layer;
import com.game.layer.LayerStack;
import com.game.layer.WindowLayer;
import com.game.layer.WorldLayer;
import com.game.level.Level;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Application {

    private static double frameRate;
    private double updatePeriod = 1.0d / frameRate;
    private double currentTime = glfwGetTime();
    private double timer = 0.0d;
    private int frames = 0;
    private int updates = 0;

    public static final Logger MAIN_LOGGER = new Logger("Main");

    private LayerStack layerStack;
    private WorldLayer worldLayer;
    private WindowLayer windowLayer;

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
        layerStack = new LayerStack();
        worldLayer = new WorldLayer("World Layer 1");
        windowLayer = new WindowLayer("Window");
        layerStack.pushLayer(windowLayer);
        layerStack.pushLayer(worldLayer);

        window = new Window(1280, 720, false, false);
        window.setEventCallback(new EventCallback());
        GL.createCapabilities();
        glfwShowWindow(window.getWindow());
        Renderer.setClearColor(0.2f, 0.6f, 0.65f, 1.0f);
        /*---OpenGL code won't work before this---*/

        frameRate = 60.0d;
        new Camera(0.5f);
        level = new Level();
    }

    public class EventCallback implements IEventCallback {
        @Override
        public void onEvent(Event e) {
            for (Layer layer : layerStack){
                layer.onEvent(e);
                if (e.isHandled()){
                    break;
                }
            }
        }
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
                update((float) deltaTime);
                ++updates;
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
