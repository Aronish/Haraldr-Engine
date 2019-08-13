package com.game;

import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL46.GL_TRUE;
import static org.lwjgl.opengl.GL46.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A class for handling GLFW window creation.
 */
class Window {

    private long window;
    private boolean isFullscreen;
    private boolean VSyncOn;
    private int windowWidth, windowHeight;
    private GLFWVidMode vidmode;

    /**
     * Constructor with parameters for window width and height, whether is will be fullscreen and have VSync turned on upon creation.
     * Creates a GLFW window and sets appropriate settings and attributes.
     * @param width the window width, in pixels.
     * @param height the window height, in pixels.
     * @param fullscreen whether the window will be fullscreen upon creation.
     * @param VSync whether the window will have VSync turned on upon creation.
     */
    Window(int width, int height, boolean fullscreen, boolean VSync){
        isFullscreen = fullscreen;
        VSyncOn = VSync;
        if (!glfwInit()){
            throw new IllegalStateException("Couldn't init GLFW!");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GL_TRUE);
        vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode == null){
            throw new IllegalStateException("Vidmode was not found!");
        }
        windowWidth = fullscreen ? vidmode.width() : width;
        windowHeight = fullscreen ? vidmode.height() : height;
        window = glfwCreateWindow(windowWidth, windowHeight, "OpenGL Game", (fullscreen ? glfwGetPrimaryMonitor() : NULL), NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Window failed to be created");
        }
        if (isFullscreen){
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }
        changeVSync();
        glfwSetKeyCallback(window, new Input());
        glfwSetWindowPos(window, (vidmode.width() - windowWidth) / 2, (vidmode.height() - windowHeight) / 2);
        glfwMakeContextCurrent(window);
    }

    /**
     * Changes between fullscreen and windowed mode.
     */
    void changeFullscreen(){
        if (isFullscreen){
            isFullscreen = false;
            glfwSetWindowMonitor(window, 0, vidmode.width() / 2 - windowWidth / 2, vidmode.height() / 2 - windowHeight / 2, windowWidth, windowHeight, 60);
            glViewport(0, 0, windowWidth, windowHeight);
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }else{
            isFullscreen = true;
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, vidmode.width(), vidmode.height(), 60);
            glViewport(0, 0, vidmode.width(), vidmode.height());
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }
    }

    /**
     * Toggles VSync.
     */
    void changeVSync(){
        if (VSyncOn){
            VSyncOn = false;
            glfwSwapInterval(0);
        }else{
            VSyncOn = true;
            glfwSwapInterval(1);
        }
    }

    /**
     * Sets the title to the provided one.
     * @param title the new title.
     */
    void setTitle(String title){
        glfwSetWindowTitle(window, title);
    }

    /**
     * @return whether VSync is turned on.
     */
    boolean VSyncOn(){
        return VSyncOn;
    }

    /**
     * Gets the window object ID.
     * @return the window object ID.
     */
    long getWindow(){
        return window;
    }
}
