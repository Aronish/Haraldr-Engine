package engine.main;

import engine.debug.Logger;
import engine.event.Event;
import engine.event.EventDispatcher;
import engine.event.EventType;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.event.WindowResizedEvent;
import engine.graphics.Renderer3D;
import engine.graphics.Shader;
import engine.input.Input;
import engine.input.Key;
import engine.layer.Layer;
import engine.layer.LayerStack;
import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public abstract class Application
{
    public static final Logger MAIN_LOGGER = new Logger("Main");

    private boolean initialized = false;
    protected LayerStack layerStack = new LayerStack();
    protected Window window;

    public static double time;
    public static int initWidth, initHeight;

    public abstract void start();

    private void stop(@NotNull Event event)
    {
        glfwSetWindowShouldClose(window.getWindowHandle(), true);
        event.setHandled(true);
    }

    protected void init(Window.WindowProperties windowProperties)
    {
        /////WINDOW///////////////////////////////////////////////////////
        window = new Window(windowProperties);
        initWidth = window.getInitWidth();
        initHeight = window.getInitHeight();
        /////OPENGL CODE WON'T WORK BEFORE THIS///////////////////////////
        EventDispatcher.addCallback(new EventCallback());
        Matrix4f.init(window.getWidth(), window.getHeight());
        Renderer3D.init(window);

        //glEnable(GL_FRAMEBUFFER_SRGB);
        if (ProgramArguments.isArgumentSet("MSAA")) //TODO: Doesn't work with custom framebuffers
        {
            glEnable(GL_MULTISAMPLE); // Definitely has performance impact even if GLFW_SAMPLES is 0.
        }
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (EntryPoint.DEBUG)
        {
            glEnable(GL_DEBUG_OUTPUT);
            glDebugMessageCallback((source, type, id, severity, length, message, userparam) ->
            {
                if (severity != GL_DEBUG_SEVERITY_NOTIFICATION)
                {
                    System.out.println();
                    MAIN_LOGGER.info(String.format("[OPENGL] (Source: %s, Type: %s, Severity: %s):", Integer.toHexString(source), Integer.toHexString(type), Integer.toHexString(severity)));
                    MAIN_LOGGER.info(memUTF8(message) + "\n");
                }
                //if (type == GL_DEBUG_TYPE_ERROR || type == GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR) stop(new WindowClosedEvent());
            }, 0);
        }

        glfwShowWindow(window.getWindowHandle());
        initialized = true;
    }

    public class EventCallback implements engine.event.EventCallback
    {
        @Override
        public void onEvent(@NotNull Event event, Window window)
        {
            if (event.eventType == EventType.WINDOW_CLOSED) stop(event);
            if (event.eventType == EventType.WINDOW_RESIZED) Matrix4f.onResize((WindowResizedEvent) event);
            if (event.eventType == EventType.KEY_PRESSED)
            {
                if (Input.isKeyPressed(window, Key.KEY_ESCAPE)) stop(event);
                if (Input.isKeyPressed(window, Key.KEY_F)) window.changeFullscreen();
            }
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                if (window.isFocused())
                {
                    Renderer3D.getPerspectiveCamera().getController().handleRotation((MouseMovedEvent) event);
                }
            }
            if (event.eventType == EventType.MOUSE_SCROLLED)
            {
                Renderer3D.getPerspectiveCamera().getController().handleScroll((MouseScrolledEvent) event);
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
        time = glfwGetTime();
        if (window.isFocused())
        {
            Renderer3D.getPerspectiveCamera().getController().handleMovement(window, deltaTime);
        }
        for (Layer layer : layerStack)
        {
            layer.onUpdate(window, deltaTime);
        }
        glfwPollEvents();
    }

    private void render()
    {
        Renderer3D.begin();
        layerStack.reverseIterator().forEachRemaining(Layer::onRender);
        Renderer3D.end();
        glfwSwapBuffers(window.getWindowHandle());
    }

    protected void loop()
    {
        if (!initialized) throw new IllegalStateException("Application was not initialized correctly before main loop start!");
        double frameRate = 60d;
        double updatePeriod = 1d / frameRate;
        double currentTime = glfwGetTime();
        double timer = 0d;
        int frames = 0;
        int updates = 0;

        while (!glfwWindowShouldClose(window.getWindowHandle()))
        {
            double newTime = glfwGetTime();
            double frameTime = newTime - currentTime;
            currentTime = newTime;
            timer += frameTime;
            if (timer >= 1d)
            {
                int fps = (int) (frames / timer), ups = (int) (updates / timer);
                window.setTitle(String.format("FPS: %d UPS: %d Frametime: %f ms", fps, ups, frameTime * 1000d));
                timer = 0.0d;
                frames = 0;
                updates = 0;
            }
            while (frameTime > 0d)
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
        layerStack.forEach(Layer::onDispose);
        Renderer3D.dispose();
        Shader.DEFAULT2D.delete();
        Shader.DIFFUSE.delete();
        Shader.NORMAL.delete();
        Shader.LIGHT_SHADER.delete();
        Shader.VISIBLE_NORMALS.delete();
        Shader.REFLECTIVE.delete();
        Shader.REFRACTIVE.delete();
        glfwTerminate();
    }
}
