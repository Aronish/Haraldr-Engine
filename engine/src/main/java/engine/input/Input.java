package engine.input;

import engine.event.Event;
import engine.event.KeyEvent;
import engine.event.MouseButtonEvent;
import engine.event.MousePressedEvent;
import engine.main.Window;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class Input
{
    public static boolean isKeyPressed(Window window, @NotNull Key key)
    {
        return GLFW.glfwGetKey(window.getWindowHandle(), key.keyCode) != 0;
    }

    public static boolean isMouseButtonPressed(long window, @NotNull Button button)
    {
        return GLFW.glfwGetMouseButton(window, button.buttonCode) != 0;
    }

    @Contract(pure = true)
    public static boolean wasKey(Event event, @NotNull Key key)
    {
        return ((KeyEvent) event).keyCode == key.keyCode;
    }

    @Contract(pure = true)
    public static boolean wasMouseButton(@NotNull Event event, @NotNull Button button)
    {
        return ((MouseButtonEvent) event).button == button.buttonCode;
    }
}
