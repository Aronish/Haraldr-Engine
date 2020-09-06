package haraldr.input;

import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")
public enum MouseButton
{
    MOUSE_BUTTON_1			(GLFW.GLFW_MOUSE_BUTTON_1),
    MOUSE_BUTTON_2			(GLFW.GLFW_MOUSE_BUTTON_2),
    MOUSE_BUTTON_3			(GLFW.GLFW_MOUSE_BUTTON_3),
    MOUSE_BUTTON_4			(GLFW.GLFW_MOUSE_BUTTON_4),
    MOUSE_BUTTON_5			(GLFW.GLFW_MOUSE_BUTTON_5),
    MOUSE_BUTTON_6			(GLFW.GLFW_MOUSE_BUTTON_6),
    MOUSE_BUTTON_7			(GLFW.GLFW_MOUSE_BUTTON_7),
    MOUSE_BUTTON_8			(GLFW.GLFW_MOUSE_BUTTON_8),
    MOUSE_BUTTON_LAST       (GLFW.GLFW_MOUSE_BUTTON_8),
    MOUSE_BUTTON_LEFT		(GLFW.GLFW_MOUSE_BUTTON_1),
    MOUSE_BUTTON_RIGHT		(GLFW.GLFW_MOUSE_BUTTON_2),
    MOUSE_BUTTON_MIDDLE		(GLFW.GLFW_MOUSE_BUTTON_3);

    public final int buttonCode;

    MouseButton(int buttonCode)
    {
        this.buttonCode = buttonCode;
    }
}
