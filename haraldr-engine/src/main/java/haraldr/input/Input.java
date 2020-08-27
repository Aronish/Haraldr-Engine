package haraldr.input;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.KeyEvent;
import haraldr.event.KeyPressedEvent;
import haraldr.event.KeyReleasedEvent;
import haraldr.event.MouseButtonEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.MouseReleasedEvent;
import haraldr.main.Window;
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
    public static boolean wasKeyPressed(@NotNull Event event, Key key)
    {
        if (event.eventType != EventType.KEY_PRESSED) return false;
        return ((KeyPressedEvent) event).keyCode == key.keyCode;
    }

    @Contract(pure = true)
    public static boolean wasKeyReleased(@NotNull Event event, Key key)
    {
        if (event.eventType != EventType.KEY_RELEASED) return false;
        return ((KeyReleasedEvent) event).keyCode == key.keyCode;
    }

    @Contract(pure = true)
    public static boolean wasMousePressed(@NotNull Event event, Button button)
    {
        if (event.eventType != EventType.MOUSE_PRESSED) return false;
        return ((MousePressedEvent) event).button == button.buttonCode;
    }

    @Contract(pure = true)
    public static boolean wasMouseReleased(@NotNull Event event, Button button)
    {
        if (event.eventType != EventType.MOUSE_RELEASED) return false;
        return ((MouseReleasedEvent) event).button == button.buttonCode;
    }
}
