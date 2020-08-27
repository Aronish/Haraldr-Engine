package haraldr.main;

import haraldr.debug.Logger;
import haraldr.event.DebugScreenUpdatedEvent;
import haraldr.event.Event;
import haraldr.event.EventDispatcher;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.event.WindowFocusEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.ResourceManager;
import haraldr.graphics.Texture;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.math.Matrix4f;
import haraldr.math.Vector2f;
import haraldr.scenegraph.Scene2D;
import haraldr.scenegraph.Scene3D;
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
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilOp;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public abstract class Application
{
    private boolean initialized = false;
    protected Window window;
    private Scene3D activeScene;
    private Scene2D activeOverlay;

    public static double time;
    public static int initWidth, initHeight;

    public static Vector2f gameViewPosition, gameViewSize;

    public abstract void start();

    private void stop(@NotNull Event event)
    {
        glfwSetWindowShouldClose(window.getWindowHandle(), true);
        event.setHandled(true);
    }

    protected void init(Window.WindowProperties windowProperties)
    {
        /////WINDOW///////////////////////////
        window = new Window(windowProperties);
        initWidth = window.getInitWidth();
        initHeight = window.getInitHeight();
        /////OPENGL CODE WON'T WORK BEFORE THIS//////////
        EventDispatcher.addCallback(new EventCallback());
        Matrix4f.init(window.getWidth(), window.getHeight());
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
            //glEnable(GL_DEBUG_OUTPUT);
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

        glfwShowWindow(window.getWindowHandle());
        initialized = true;
    }

    public void setActiveScene(Scene3D scene)
    {
        activeScene = scene;
        activeScene.onActivate();
    }

    public void setActiveOverlay(Scene2D overlay)
    {
        activeOverlay = overlay;
        activeOverlay.onActivate(window);
    }

    public class EventCallback implements haraldr.event.EventCallback
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
                if (Input.isKeyPressed(window, Key.KEY_E))
                {
                    //window.setFocus(!window.isFocused());
                    //Renderer3D.getCamera().onFocus(new WindowFocusEvent(window.isFocused()));
                }
            }
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                //Renderer3D.getCamera().handleRotation((MouseMovedEvent) event);
            }
            if (event.eventType == EventType.MOUSE_SCROLLED)
            {
                //Renderer3D.getCamera().handleScroll((MouseScrolledEvent) event);
            }
            if (event.eventType == EventType.WINDOW_FOCUS)
            {
                //Renderer3D.getCamera().onFocus((WindowFocusEvent) event);
            }
            if (activeOverlay != null) activeOverlay.onEvent(event, window);
            if (!event.isHandled()) activeScene.onEvent(event, window);
        }
    }

    private void update(float deltaTime)
    {
        time = glfwGetTime();
        activeOverlay.onUpdate(deltaTime, window);
        //if (window.isFocused())
        //{
        //    Renderer3D.getCamera().handleMovement(window, deltaTime);
        //}
        activeScene.onUpdate(deltaTime, window);
        activeOverlay.onUpdate(deltaTime, window);
    }

    protected void render()
    {
        Renderer.clear(Renderer.ClearMask.COLOR);
        glEnable(GL_DEPTH_TEST);
        //Renderer3D.begin(window);
        activeScene.onRender();
        Renderer3D.end(window);
        glDisable(GL_DEPTH_TEST);
        activeOverlay.onRender();
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
                    EventDispatcher.dispatch(new DebugScreenUpdatedEvent(fps, ups), window);
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

    public void dispose()
    {
        window.delete();
        activeScene.onDispose();
        activeOverlay.onDispose();
        Renderer2D.dispose();
        Renderer3D.dispose();
        ResourceManager.dispose();
        glfwTerminate();
    }
}