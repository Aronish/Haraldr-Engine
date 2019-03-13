package main.java;

import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

class Window {

    private long window;
    private boolean isFullscreen;

    Window(int width, int height, boolean fullscreen){
        int windowWidth = fullscreen ? 1920 : width;
        int windowHeight = fullscreen ? 1080 : height;
        this.isFullscreen = fullscreen;

        if (!glfwInit()){
            throw new IllegalStateException("Couldn't init GLFW!");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        this.window = glfwCreateWindow(windowWidth, windowHeight, "FUCKYEAH", (fullscreen ? glfwGetPrimaryMonitor() : NULL), NULL);
        if (this.window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Window failed to be created");
        }

        if(fullscreen){
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }

        glfwSetKeyCallback(this.window, new Input());
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(this.window, (vidmode.width() - windowWidth) / 2, (vidmode.height() - windowHeight) / 2);
        glfwMakeContextCurrent(this.window);
    }

    long getWindow(){
        return this.window;
    }
}
