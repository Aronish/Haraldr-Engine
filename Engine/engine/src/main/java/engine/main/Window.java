package engine.main;

import engine.event.EventDispatcher;
import engine.event.EventObserver;
import engine.event.KeyPressedEvent;
import engine.event.KeyReleasedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MousePressedEvent;
import engine.event.MouseReleasedEvent;
import engine.event.MouseScrolledEvent;
import engine.event.WindowClosedEvent;
import engine.event.WindowFocusEvent;
import engine.event.WindowResizedEvent;
import engine.graphics.Framebuffer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL46.GL_TRUE;
import static org.lwjgl.opengl.GL46.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Represents a GLFW window.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Window
{
    private long windowHandle;
    private GLFWVidMode vidmode;
    private boolean vSyncOn;
    private boolean isFullscreen;
    private boolean focused = true;
    private int windowWidth, windowHeight, initWidth, initHeight;

    private List<EventObserver<WindowResizedEvent>> observers = new ArrayList<>();

    private Framebuffer framebuffer;

    Window(@NotNull WindowProperties windowProperties)
    {
        isFullscreen = windowProperties.fullscreen;
        vSyncOn = windowProperties.vsync;

        if (!glfwInit())
        {
            throw new IllegalStateException("Couldn't init GLFW!");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, windowProperties.maximized ? GLFW_TRUE : GLFW_FALSE);

        glfwWindowHint(GLFW_SAMPLES, windowProperties.samples);

        vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode == null)
        {
            throw new IllegalStateException("Vidmode was not found!");
        }

        initWidth       = windowProperties.fullscreen ? vidmode.width()  : (windowProperties.maximized ? vidmode.width() : windowProperties.width);
        initHeight      = windowProperties.fullscreen ? vidmode.height() : (windowProperties.maximized ? vidmode.height() - 63 : windowProperties.height); // -63 for window borders
        windowWidth     = windowProperties.fullscreen ? vidmode.width()  : (windowProperties.maximized ? vidmode.width() : windowProperties.width);
        windowHeight    = windowProperties.fullscreen ? vidmode.height() : (windowProperties.maximized ? vidmode.height() - 63 : windowProperties.height);

        windowHandle = glfwCreateWindow(windowWidth, windowHeight, "OpenGL Game", (windowProperties.fullscreen ? glfwGetPrimaryMonitor() : NULL), NULL);
        if (windowHandle == NULL)
        {
            glfwTerminate();
            throw new RuntimeException("Window failed to be created");
        }

        glfwSetWindowPos(windowHandle, (vidmode.width() - windowWidth) / 2, (vidmode.height() - windowHeight) / 2);
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        setCursorVisible(true);
        setFocus(true);
        setVSync(windowProperties.vsync);
        ///// FRAMEBUFFER ///////////////
        framebuffer = new Framebuffer();
        framebuffer.setColorAttachment(new Framebuffer.ColorAttachment(initWidth, initHeight, GL_RGB16F));
        framebuffer.setDepthBuffer(new Framebuffer.RenderBuffer(initWidth, initHeight, GL_DEPTH_COMPONENT24));

        ///// CALLBACKS ///////////////////////////////////////////////////////////
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS){
                EventDispatcher.dispatch(new KeyPressedEvent(key), this);
            }else if (action == GLFW_RELEASE){
                EventDispatcher.dispatch(new KeyReleasedEvent(key), this);
            }
        });

        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            if (action == GLFW_PRESS){
                EventDispatcher.dispatch(new MousePressedEvent(button), this);
            }else if (action == GLFW_RELEASE){
                EventDispatcher.dispatch(new MouseReleasedEvent(button), this);
            }
        });

        glfwSetScrollCallback(windowHandle, (window, xOffset, yOffset) -> EventDispatcher.dispatch(new MouseScrolledEvent(xOffset, yOffset), this));

        glfwSetCursorPosCallback(windowHandle, (window, xPos, yPos) -> EventDispatcher.dispatch(new MouseMovedEvent(xPos, yPos), this));

        glfwSetWindowCloseCallback(windowHandle, (window) -> EventDispatcher.dispatch(new WindowClosedEvent(), this));

        glfwSetWindowSizeCallback(windowHandle, (window, newWidth, newHeight) -> {
            windowWidth = newWidth;
            windowHeight = newHeight;
            for (EventObserver<WindowResizedEvent> observer : observers)
            {
                observer.onEvent(new WindowResizedEvent(newWidth, newHeight));
            }
            setViewPortSize(newWidth, newHeight);
            framebuffer.getColorAttachment().resize(newWidth, newHeight);
            framebuffer.getDepthAttachment().resize(newWidth, newHeight);
            EventDispatcher.dispatch(new WindowResizedEvent(newWidth, newHeight), this);
        });

        glfwSetWindowFocusCallback(windowHandle, (window, focused) -> {
            setFocus(focused);
            EventDispatcher.dispatch(new WindowFocusEvent(focused), this);
        });

        glfwSetErrorCallback((error, description) -> MAIN_LOGGER.info(error, description));
    }

    public void addObserver(EventObserver<WindowResizedEvent> observer)
    {
        observers.add(observer);
    }

    public void removeObserver(EventObserver<WindowResizedEvent> observer)
    {
        observers.remove(observer);
    }

    public void changeFullscreen()
    {
        if (isFullscreen)
        {
            isFullscreen = false;
            glfwSetWindowMonitor(windowHandle, 0, vidmode.width() / 2 - initWidth / 2, vidmode.height() / 2 - initHeight / 2, initWidth, initHeight, 60);
            setViewPortSize(initWidth, initHeight);
        }else{
            isFullscreen = true;
            glfwSetWindowMonitor(windowHandle, glfwGetPrimaryMonitor(), 0, 0, vidmode.width(), vidmode.height(), 60);
            setViewPortSize(vidmode.width(), vidmode.height());
        }
    }

    public void setFocus(boolean focused)
    {
        this.focused = focused;
        glfwSetInputMode(windowHandle, GLFW_CURSOR, focused ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    private void setViewPortSize(int width, int height)
    {
        glViewport(0, 0, width, height);
    }

    public void setCursorVisible(boolean visible)
    {
        glfwSetInputMode(windowHandle, GLFW_CURSOR, visible ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_HIDDEN);
    }

    public void setVSync(boolean enabled)
    {
        vSyncOn = enabled;
        glfwSwapInterval(enabled ? 1 : 0);
    }

    public void setTitle(String title)
    {
        glfwSetWindowTitle(windowHandle, title);
    }

    public long getWindowHandle()
    {
        return windowHandle;
    }

    public Framebuffer getFramebuffer()
    {
        return framebuffer;
    }

    public int getWidth()
    {
        return windowWidth;
    }

    public int getHeight()
    {
        return windowHeight;
    }

    public int getInitWidth()
    {
        return initWidth;
    }

    public int getInitHeight()
    {
        return initHeight;
    }

    public boolean isFocused()
    {
        return focused;
    }

    public boolean vSyncOn()
    {
        return vSyncOn;
    }

    public void delete()
    {
        framebuffer.delete();
    }

    public static class WindowProperties
    {

        public final int width, height, samples;
        public final boolean maximized, fullscreen, vsync;

        public WindowProperties(int width, int height, int samples, boolean maximized, boolean fullscreen, boolean vsync)
        {
            this.width = width;
            this.height = height;
            this.samples = samples;
            this.maximized = maximized;
            this.fullscreen = fullscreen;
            this.vsync = vsync;
        }
    }
}
