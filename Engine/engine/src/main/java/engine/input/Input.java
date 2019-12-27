package engine.input;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class Input
{
    public static boolean isKeyPressed(long window, @NotNull Key key)
    {
        return GLFW.glfwGetKey(window, key.keyCode) != 0;
    }

    public static boolean isMouseButtonPressed(long window, @NotNull Button button)
    {
        return GLFW.glfwGetMouseButton(window, button.buttonCode) != 0;
    }
}
