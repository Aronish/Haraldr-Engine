package main;

import event.IEventCallback;
import event.KeyPressedEvent;
import event.KeyReleasedEvent;
import event.MouseMovedEvent;
import event.MousePressedEvent;
import event.MouseReleasedEvent;
import event.MouseScrolledEvent;
import event.WindowClosedEvent;
import event.WindowResizedEvent;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static main.Application.MAIN_LOGGER;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorContentScale;
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
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL46.GL_TRUE;
import static org.lwjgl.opengl.GL46.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Represents a GLFW window.
 */
public class Window
{
    private long windowHandle;
    private GLFWVidMode vidmode;
    private boolean VSyncOn;
    private boolean isFullscreen;
    private float aspectRatio;
    private float contentScaleX, contentScaleY;
    private int windowWidth, windowHeight, initWidth, initHeight;

    private IEventCallback eventCallback;

    /**
     * @param width the window width, in pixels.
     * @param height the window height, in pixels.
     * @param fullscreen whether the window will be fullscreen upon creation.
     * @param VSync whether the window will have VSync turned on upon creation.
     */
    Window(int width, int height, boolean fullscreen, boolean VSync)
    {
        isFullscreen = fullscreen;
        VSyncOn = VSync;

        if (!glfwInit())
        {
            throw new IllegalStateException("Couldn't init GLFW!");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode == null)
        {
            throw new IllegalStateException("Vidmode was not found!");
        }
        initWidth = width;
        initHeight = height;
        windowWidth = fullscreen ? vidmode.width() : width;
        windowHeight = fullscreen ? vidmode.height() : height;
        aspectRatio = (float) windowWidth / windowHeight;

        try (MemoryStack s = stackPush())
        {
            FloatBuffer px = s.mallocFloat(1);
            FloatBuffer py = s.mallocFloat(1);

            glfwGetMonitorContentScale(glfwGetPrimaryMonitor(), px, py);

            contentScaleX = px.get(0);
            contentScaleY = py.get(0);
        }

        windowHandle = glfwCreateWindow(windowWidth, windowHeight, "OpenGL Game", (fullscreen ? glfwGetPrimaryMonitor() : NULL), NULL);
        if (windowHandle == NULL)
        {
            glfwTerminate();
            throw new RuntimeException("Window failed to be created");
        }

        glfwSetWindowPos(windowHandle, (vidmode.width() - windowWidth) / 2, (vidmode.height() - windowHeight) / 2);
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        setCursorVisible(false);
        setVSync(VSync);
        ///// CALLBACKS ///////////////////////////////
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS){
                eventCallback.onEvent(new KeyPressedEvent(key));
            }else if (action == GLFW_RELEASE){
                eventCallback.onEvent(new KeyReleasedEvent(key));
            }
        });

        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            if (action == GLFW_PRESS){
                eventCallback.onEvent(new MousePressedEvent(button));
            }else if (action == GLFW_RELEASE){
                eventCallback.onEvent(new MouseReleasedEvent(button));
            }
        });

        glfwSetScrollCallback(windowHandle, (window, xOffset, yOffset) -> eventCallback.onEvent(new MouseScrolledEvent(xOffset, yOffset)));

        glfwSetCursorPosCallback(windowHandle, (window, xPos, yPos) -> eventCallback.onEvent(new MouseMovedEvent(xPos, yPos)));

        glfwSetWindowCloseCallback(windowHandle, (window) -> eventCallback.onEvent(new WindowClosedEvent()));

        glfwSetWindowSizeCallback(windowHandle, (window, newWidth, newHeight) -> {
            aspectRatio = (float) newWidth / newHeight;
            windowWidth = newWidth;
            windowHeight = newHeight;
            eventCallback.onEvent(new WindowResizedEvent(newWidth, newHeight));
            setViewPortSize(newWidth, newHeight);
        });

        glfwSetErrorCallback((error, description) -> MAIN_LOGGER.info(error, description));
    }

    void setEventCallback(IEventCallback eventCallback)
    {
        this.eventCallback = eventCallback;
    }

    void changeFullscreen()
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

    private void setViewPortSize(int width, int height)
    {
        glViewport(0, 0, width, height);
    }

    public void setCursorVisible(boolean visible)
    {
        glfwSetInputMode(windowHandle, GLFW_CURSOR, visible ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_HIDDEN);
    }

    void setVSync(boolean enabled)
    {
        VSyncOn = enabled;
        glfwSwapInterval(enabled ? 1 : 0);
    }

    void setTitle(String title)
    {
        glfwSetWindowTitle(windowHandle, title);
    }

    boolean VSyncOn()
    {
        return VSyncOn;
    }

    public long getWindowHandle()
    {
        return windowHandle;
    }

    public float getAspectRatio()
    {
        return aspectRatio;
    }

    public int getWidth()
    {
        return windowWidth;
    }

    public int getHeight()
    {
        return windowHeight;
    }

    public float getContentScaleX()
    {
        return contentScaleX;
    }

    public float getContentScaleY()
    {
        return contentScaleY;
    }
}
