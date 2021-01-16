package haraldr.main;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventDispatcher;
import haraldr.event.EventType;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.ResourceManager;
import haraldr.graphics.Texture;
import org.jetbrains.annotations.Contract;
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
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilOp;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public abstract class Application
{
    public static double time;

    private Window.WindowProperties initialWindowProperties;
    private Window window;
    private boolean initialized;

    @Contract(pure = true)
    public Application(Window.WindowProperties initialWindowProperties)
    {
        this.initialWindowProperties = initialWindowProperties;
    }

    public final void start()
    {
        init();
        loop();
    }

    protected final void stop()
    {
        glfwSetWindowShouldClose(window.getWindowHandle(), true);
    }

    protected abstract void clientInit(Window window);

    private void init()
    {
        /////WINDOW//////////////////////////////////
        window = new Window(initialWindowProperties);
        /////OPENGL CODE WON'T WORK BEFORE THIS//////////
        EventDispatcher.addCallback(new EventCallback());
        Renderer2D.init(window.getWidth(), window.getHeight());
        Renderer3D.init(initialWindowProperties);
        Texture.init();

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (EntryPoint.DEBUG)
        {
            glEnable(GL_DEBUG_OUTPUT);
            glDebugMessageCallback((source, type, id, severity, length, message, userparam) ->
            {
                if (id == 131218) return;
                if (severity != GL_DEBUG_SEVERITY_NOTIFICATION)
                {
                    System.out.println();
                    Logger.info(String.format("[OPENGL] (Source: %s, Type: %s, ID: %s, Severity: %s):", Integer.toHexString(source), Integer.toHexString(type), Integer.toHexString(id), Integer.toHexString(severity)));
                    Logger.info(memUTF8(message) + "\n");
                }
                //if (type == GL_DEBUG_TYPE_ERROR || type == GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR) stop(new WindowClosedEvent());
            }, 0);
        }
        clientInit(window);

        glViewport(0, 0, window.getWidth(), window.getHeight());
        glfwShowWindow(window.getWindowHandle());
        initialized = true;
    }

    protected abstract void clientEvent(Event event, Window window);

    private class EventCallback implements haraldr.event.EventCallback
    {
        @Override
        public void onEvent(@NotNull Event event, Window window)
        {
            clientEvent(event, window);
            if (event.eventType == EventType.WINDOW_CLOSED) stop();
            if (event.eventType == EventType.WINDOW_RESIZED)
            {
                var windowResizedEvent = (WindowResizedEvent) event;
                Renderer.setViewPort(0, 0, windowResizedEvent.width, windowResizedEvent.height);
                Renderer2D.onWindowResized(windowResizedEvent.width, windowResizedEvent.height);
            }
        }
    }

    protected abstract void clientUpdate(float deltaTime, Window window);

    private void update(float deltaTime)
    {
        time = glfwGetTime();
        clientUpdate(deltaTime, window);
    }

    protected abstract void clientRender(Window window);

    private void render()
    {
        clientRender(window);
        glfwSwapBuffers(window.getWindowHandle());
    }

    private void loop()
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
            if (!window.isMinimized())
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
            glfwPollEvents();
        }
    }

    public abstract void clientDispose();

    public final void dispose()
    {
        clientDispose();
        window.delete();
        Renderer3D.dispose();
        ResourceManager.dispose();
        glfwTerminate();
    }
}