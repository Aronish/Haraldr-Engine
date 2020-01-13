package engine.main;

import engine.debug.DebugEventHandler;
import engine.debug.Logger;
import engine.event.DebugScreenUpdatedEvent;
import engine.event.Event;
import engine.event.EventDispatcher;
import engine.event.EventType;
import engine.event.WindowResizedEvent;
import engine.graphics.Renderer2D;
import engine.input.Input;
import engine.input.Key;
import engine.layer.Layer;
import engine.layer.LayerStack;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public abstract class Application
{
    public static final Logger MAIN_LOGGER = new Logger("Main");

    private boolean initialized = false;
    protected LayerStack layerStack = new LayerStack();
    private Window window;

    public abstract void start();

    private void stop(@NotNull Event event)
    {
        glfwSetWindowShouldClose(window.getWindowHandle(), true);
        event.setHandled(true);
    }

    protected void init(int windowWidth, int windowHeight, boolean fullscreen, boolean vSync)
    {
        /////WINDOW///////////////////////////////////////////////////////
        window = new Window(windowWidth, windowHeight, fullscreen, vSync);
        /////OPENGL CODE WON'T WORK BEFORE THIS///////////////////////////
        EventDispatcher.addCallback(new EventCallback());
        EventDispatcher.addCallback(new DebugEventHandler());
        Matrix4f.init(window.getWidth(), window.getHeight());

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (EntryPoint.DEBUG)
        {
            glEnable(GL_DEBUG_OUTPUT);
            glDebugMessageCallback((source, type, id, severity, length, message, userparam) -> {
                System.out.println("Source: " + Integer.toHexString(source) + "\nType: " + Integer.toHexString(type) + "\nSeverity: " + Integer.toHexString(severity) + "\nLength: " + length);
                System.out.println(memUTF8(message) + "\n");
            }, 0);
        }

        glfwShowWindow(window.getWindowHandle());
        initialized = true;
    }

    //TODO: Fix event system.
    public class EventCallback implements engine.event.EventCallback
    {
        @Override
        public void onEvent(@NotNull Event event, Window window)
        {
            if (event.eventType == EventType.WINDOW_CLOSED) stop(event);
            if (event.eventType == EventType.WINDOW_RESIZED) Matrix4f.onResize((WindowResizedEvent) event);
            if (event.eventType == EventType.KEY_PRESSED)
            {
                if (Input.isKeyPressed(window.getWindowHandle(), Key.KEY_ESCAPE)) stop(event);
                if (Input.isKeyPressed(window.getWindowHandle(), Key.KEY_F)) window.changeFullscreen();
            }
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
        Renderer2D.clear();
        layerStack.reverseIterator().forEachRemaining(Layer::onRender);
        glfwSwapBuffers(window.getWindowHandle());
    }

    protected void loop()
    {
        if (!initialized) throw new IllegalStateException("Application was not initialized before main loop start!");
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
                EventDispatcher.dispatch(new DebugScreenUpdatedEvent(fps, ups), window);
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

    public void dispose()
    {
        glfwTerminate();
    }
}
