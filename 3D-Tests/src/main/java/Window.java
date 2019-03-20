package main.java;

import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A class for handling window creation.
 */
class Window {

    private long window;
    private boolean isFullscreen;
    private int windowWidth, windowHeight;
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
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

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
            glfwSetWindowMonitor(window, 0, this.vidmode.width() / 2 - this.windowWidth / 2, this.vidmode.height() / 2 - this.windowHeight / 2, this.windowWidth, this.windowHeight, 60);
            glViewport(0, 0, this.windowWidth, this.windowHeight);
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
