package haraldr.input;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.KeyPressedEvent;
import haraldr.event.KeyReleasedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.MouseReleasedEvent;
import haraldr.main.Window;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class Input
{
    public static boolean isKeyPressed(Window window, @NotNull KeyboardKey keyboardKey)
    {
        return GLFW.glfwGetKey(window.getWindowHandle(), keyboardKey.keyCode) != 0;
    }

    public static boolean isMouseButtonPressed(long window, @NotNull MouseButton mouseButton)
    {
        return GLFW.glfwGetMouseButton(window, mouseButton.buttonCode) != 0;
    }

    @Contract(pure = true)
    public static boolean wasKeyPressed(@NotNull Event event, KeyboardKey keyboardKey)
    {
        if (event.eventType != EventType.KEY_PRESSED) return false;
        return ((KeyPressedEvent) event).keyCode == keyboardKey.keyCode;
    }

    @Contract(pure = true)
    public static boolean wasKeyReleased(@NotNull Event event, KeyboardKey keyboardKey)
    {
        if (event.eventType != EventType.KEY_RELEASED) return false;
        return ((KeyReleasedEvent) event).keyCode == keyboardKey.keyCode;
    }

    @Contract(pure = true)
    public static boolean wasMousePressed(@NotNull Event event, MouseButton mouseButton)
    {
        if (event.eventType != EventType.MOUSE_PRESSED) return false;
        return ((MousePressedEvent) event).button == mouseButton.buttonCode;
    }

    @Contract(pure = true)
    public static boolean wasMouseReleased(@NotNull Event event, MouseButton mouseButton)
    {
        if (event.eventType != EventType.MOUSE_RELEASED) return false;
        return ((MouseReleasedEvent) event).button == mouseButton.buttonCode;
    }
}
