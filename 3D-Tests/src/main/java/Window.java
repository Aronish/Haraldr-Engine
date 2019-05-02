package main.java;

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
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL46.GL_TRUE;
import static org.lwjgl.opengl.GL46.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A class for handling window creation.
 */
class Window {

    private long window;
    private boolean isFullscreen;
    private static int windowWidth, windowHeight;
    private GLFWVidMode vidmode;

    /**
     * Constructor with parameters for window width and height and whether is will be fullscreen upon creation.
     * Creates a GLFW window and sets appropriate settings and attributes.
     * @param width the window width, in pixels.
     * @param height the window height, in pixels.
     * @param fullscreen whether the window will be fullscreen upon creation.
     */
    Window(int width, int height, boolean fullscreen){
        isFullscreen = fullscreen;

        if (!glfwInit()){
            throw new IllegalStateException("Couldn't init GLFW!");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GL_TRUE);

        this.vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        windowWidth = fullscreen ? this.vidmode.width() : width;
        windowHeight = fullscreen ? this.vidmode.height() : height;

        window = glfwCreateWindow(windowWidth, windowHeight, "OpenGL Game", (fullscreen ? glfwGetPrimaryMonitor() : NULL), NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Window failed to be created");
        }

        if(fullscreen){
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }

        glfwSetKeyCallback(window, new Input());
        glfwSetWindowPos(window, (this.vidmode.width() - windowWidth) / 2, (this.vidmode.height() - windowHeight) / 2);
        glfwMakeContextCurrent(window);
    }

    /**
     * Changes between fullscreen and windowed mode.
     */
    void changeFullscreen(){
        if (isFullscreen){
            isFullscreen = false;
            glfwSetWindowMonitor(window, 0, this.vidmode.width() / 2 - windowWidth / 2, this.vidmode.height() / 2 - windowHeight / 2, windowWidth, windowHeight, 60);
            glViewport(0, 0, windowWidth, windowHeight);
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }else{
            isFullscreen = true;
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, this.vidmode.width(), this.vidmode.height(), 60);
            glViewport(0, 0, this.vidmode.width(), this.vidmode.height());
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }
    }

    /**
     * Gets the window object ID.
     * @return the window object ID.
     */
    long getWindow(){
        return window;
    }
}
