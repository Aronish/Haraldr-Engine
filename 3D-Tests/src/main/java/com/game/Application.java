package com.game;

import com.game.debug.Logger;
import com.game.event.DebugScreenUpdatedEvent;
import com.game.event.Event;
import com.game.event.EventType;
import com.game.event.IEventCallback;
import com.game.graphics.Models;
import com.game.graphics.Renderer;
import com.game.layer.GUILayer;
import com.game.layer.Layer;
import com.game.layer.LayerStack;
import com.game.layer.WorldLayer;
import com.game.math.Matrix4f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Application
{
    public static final Logger MAIN_LOGGER = new Logger("Main");

    private LayerStack layerStack;
    private Window window;

    void start()
    {
        init();
        loop();
    }

    private void stop(Event event)
    {
        glfwSetWindowShouldClose(window.getWindowHandle(), true);
        event.setHandled(true);
    }

    private void init()
    {
        layerStack = new LayerStack();
        window = new Window(1280, 720, false, false);
        window.setEventCallback(new EventCallback());
        glfwShowWindow(window.getWindowHandle());
        Renderer.setClearColor(0.2f, 0.6f, 0.65f, 1.0f);
        /*---OpenGL code won't work before this---*/
        Layer worldLayer = new WorldLayer("World");
        Layer guiLayer = new GUILayer("GUI");
        layerStack.pushLayer(worldLayer);
        layerStack.pushLayer(guiLayer);
    }

    public class EventCallback implements IEventCallback
    {
        @Override
        public void onEvent(Event event)
        {
            if (event.eventType == EventType.WINDOW_CLOSED) stop(event);
            if (event.eventType == EventType.WINDOW_RESIZED) Matrix4f.recalculateOrthographic(window.getAspectRatio());
            for (Layer layer : layerStack)
            {
                if (event.isHandled()) break;
                layer.onEvent(window, event);
            }
        }
    }

    private void update(float deltaTime)
    {
        for (Layer layer : layerStack)
        {
            layer.onUpdate(window, deltaTime);
        }
        glfwPollEvents();
    }

    private void render()
    {
        Renderer.clear();
        layerStack.reverseIterator().forEachRemaining(Layer::onRender);
        glfwSwapBuffers(window.getWindowHandle());
    }

    private void loop()
    {
        double frameRate = 60.0d;
        double updatePeriod = 1.0d / frameRate;
        double currentTime = glfwGetTime();
        double timer = 0.0d;
        int frames = 0;
        int updates = 0;

        while (!glfwWindowShouldClose(window.getWindowHandle()))
        {
            double newTime = glfwGetTime();
            double frameTime = newTime - currentTime;
            currentTime = newTime;
            timer += frameTime;
            if (timer >= 1.0d)
            {
                int fps = (int) (frames / timer), ups = (int) (updates / timer);
                window.setTitle("FPS: " + fps + " UPS: " + ups);
                window.dispatchNewEvent(new DebugScreenUpdatedEvent(fps, ups));
                timer = 0.0d;
                frames = 0;
                updates = 0;
            }
            while (frameTime > 0.0)
            {
                double deltaTime = Math.min(frameTime, updatePeriod);
                update((float) deltaTime);
                ++updates;
                frameTime -= deltaTime;
            }
            render();
            ++frames;
        }
    }

    void cleanUp()
    {
        Models.cleanUp();
        Renderer.deleteShaders();
        glfwTerminate();
    }
}
