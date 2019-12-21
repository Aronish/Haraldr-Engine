package engine.main;

import engine.debug.Logger;
import engine.event.DebugScreenUpdatedEvent;
import engine.event.Event;
import engine.event.EventDispatcher;
import engine.event.EventType;
import engine.event.IEventCallback;
import engine.event.KeyPressedEvent;
import engine.event.WindowResizedEvent;
import engine.graphics.Renderer2D;
import engine.layer.Layer;
import engine.layer.LayerStack;
import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
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
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public abstract class Application
{
    public static final Logger MAIN_LOGGER = new Logger("Main");

    protected LayerStack layerStack;
    private Window window;

    public void start()
    {
        init();
        loop();
    }

    private void stop(@NotNull Event event)
    {
        glfwSetWindowShouldClose(window.getWindowHandle(), true);
        event.setHandled(true);
    }

    protected void init()
    {
        /////WINDOW//////////////////////////////////////////////////////////////
        window = new Window(1280, 720, false, false);
        /////OPENGL CODE WON'T WORK BEFORE THIS//////////////////////////////////
        window.setEventCallback(new EventCallback());
        EventDispatcher.addCallback(new EventCallback());
        /////INIT//////////
        Matrix4f.init(window.getWidth(), window.getHeight());
        Renderer2D.setClearColor(0.2f, 0.6f, 0.65f, 1.0f);

        layerStack = new LayerStack();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glEnable(GL_DEBUG_OUTPUT);
        glDebugMessageCallback((source, type, id, severity, length, message, userparam) -> {
            System.out.println("Source: " + Integer.toHexString(source) + "\nType: " + Integer.toHexString(type) + "\nSeverity: " + Integer.toHexString(severity) + "\nLength: " + length);
            System.out.println(memUTF8(message) + "\n");
        }, 0);

        glfwShowWindow(window.getWindowHandle());
    }

    public class EventCallback implements IEventCallback
    {
        @Override
        public void onEvent(@NotNull Event event)
        {
            if (event.eventType == EventType.WINDOW_CLOSED) stop(event);
            if (event.eventType == EventType.WINDOW_RESIZED) Matrix4f.onResize((WindowResizedEvent) event);
            if (event.eventType == EventType.KEY_PRESSED)
            {
                if (((KeyPressedEvent) event).keyCode == GLFW_KEY_ESCAPE) stop(event);
                if (((KeyPressedEvent) event).keyCode == GLFW_KEY_F) window.changeFullscreen();
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
                EventDispatcher.dispatch(new DebugScreenUpdatedEvent(fps, ups));
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
