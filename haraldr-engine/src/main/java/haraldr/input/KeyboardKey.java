package haraldr.input;

import org.jetbrains.annotations.Contract;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")
public enum KeyboardKey
{
    /////PRINTABLE KEYS///////////////////////
    KEY_SPACE			(GLFW.GLFW_KEY_SPACE),
    KEY_APOSTROPHE	    (GLFW.GLFW_KEY_APOSTROPHE),
    KEY_COMMA			(GLFW.GLFW_KEY_COMMA),
    KEY_MINUS			(GLFW.GLFW_KEY_MINUS),
    KEY_PERIOD			(GLFW.GLFW_KEY_PERIOD),
    KEY_SLASH			(GLFW.GLFW_KEY_SLASH),
    KEY_0			    (GLFW.GLFW_KEY_0),
    KEY_1			    (GLFW.GLFW_KEY_1),
    KEY_2			    (GLFW.GLFW_KEY_2),
    KEY_3			    (GLFW.GLFW_KEY_3),
    KEY_4			    (GLFW.GLFW_KEY_4),
    KEY_5			    (GLFW.GLFW_KEY_5),
    KEY_6			    (GLFW.GLFW_KEY_6),
    KEY_7			    (GLFW.GLFW_KEY_7),
    KEY_8			    (GLFW.GLFW_KEY_8),
    KEY_9			    (GLFW.GLFW_KEY_9),
    KEY_SEMICOLON		(GLFW.GLFW_KEY_SEMICOLON),
    KEY_EQUAL			(GLFW.GLFW_KEY_EQUAL),
    KEY_A			    (GLFW.GLFW_KEY_A),
    KEY_B			    (GLFW.GLFW_KEY_B),
    KEY_C			    (GLFW.GLFW_KEY_C),
    KEY_D			    (GLFW.GLFW_KEY_D),
    KEY_E			    (GLFW.GLFW_KEY_E),
    KEY_F			    (GLFW.GLFW_KEY_F),
    KEY_G			    (GLFW.GLFW_KEY_G),
    KEY_H			    (GLFW.GLFW_KEY_H),
    KEY_I			    (GLFW.GLFW_KEY_I),
    KEY_J			    (GLFW.GLFW_KEY_J),
    KEY_K			    (GLFW.GLFW_KEY_K),
    KEY_L			    (GLFW.GLFW_KEY_L),
    KEY_M			    (GLFW.GLFW_KEY_M),
    KEY_N			    (GLFW.GLFW_KEY_N),
    KEY_O			    (GLFW.GLFW_KEY_O),
    KEY_P			    (GLFW.GLFW_KEY_P),
    KEY_Q			    (GLFW.GLFW_KEY_Q),
    KEY_R			    (GLFW.GLFW_KEY_R),
    KEY_S			    (GLFW.GLFW_KEY_S),
    KEY_T			    (GLFW.GLFW_KEY_T),
    KEY_U			    (GLFW.GLFW_KEY_U),
    KEY_V			    (GLFW.GLFW_KEY_V),
    KEY_W			    (GLFW.GLFW_KEY_W),
    KEY_X			    (GLFW.GLFW_KEY_X),
    KEY_Y			    (GLFW.GLFW_KEY_Y),
    KEY_Z			    (GLFW.GLFW_KEY_Z),
    KEY_LEFT_BRACKET	(GLFW.GLFW_KEY_LEFT_BRACKET),
    KEY_BACKSLASH		(GLFW.GLFW_KEY_BACKSLASH),
    KEY_RIGHT_BRACKET	(GLFW.GLFW_KEY_RIGHT_BRACKET),
    KEY_GRAVE_ACCENT	(GLFW.GLFW_KEY_GRAVE_ACCENT),
    KEY_WORLD_1			(GLFW.GLFW_KEY_WORLD_1),
    KEY_WORLD_2			(GLFW.GLFW_KEY_WORLD_2),
    /////FUNCTION KEYS/////////////////////////
    KEY_ESCAPE			(GLFW.GLFW_KEY_ESCAPE),
    KEY_ENTER			(GLFW.GLFW_KEY_ENTER),
    KEY_TAB			    (GLFW.GLFW_KEY_TAB),
    KEY_BACKSPACE		(GLFW.GLFW_KEY_BACKSPACE),
    KEY_INSERT			(GLFW.GLFW_KEY_INSERT),
    KEY_DELETE			(GLFW.GLFW_KEY_DELETE),
    KEY_RIGHT			(GLFW.GLFW_KEY_RIGHT),
    KEY_LEFT			(GLFW.GLFW_KEY_LEFT),
    KEY_DOWN			(GLFW.GLFW_KEY_DOWN),
    KEY_UP			    (GLFW.GLFW_KEY_UP),
    KEY_PAGE_UP			(GLFW.GLFW_KEY_PAGE_UP),
    KEY_PAGE_DOWN		(GLFW.GLFW_KEY_PAGE_DOWN),
    KEY_HOME			(GLFW.GLFW_KEY_HOME),
    KEY_END			    (GLFW.GLFW_KEY_END),
    KEY_CAPS_LOCK		(GLFW.GLFW_KEY_CAPS_LOCK),
    KEY_SCROLL_LOCK		(GLFW.GLFW_KEY_SCROLL_LOCK),
    KEY_NUM_LOCK		(GLFW.GLFW_KEY_NUM_LOCK),
    KEY_PRINT_SCREEN	(GLFW.GLFW_KEY_PRINT_SCREEN),
    KEY_PAUSE			(GLFW.GLFW_KEY_PAUSE),
    KEY_F1			    (GLFW.GLFW_KEY_F1),
    KEY_F2			    (GLFW.GLFW_KEY_F2),
    KEY_F3			    (GLFW.GLFW_KEY_F3),
    KEY_F4			    (GLFW.GLFW_KEY_F4),
    KEY_F5			    (GLFW.GLFW_KEY_F5),
    KEY_F6			    (GLFW.GLFW_KEY_F6),
    KEY_F7			    (GLFW.GLFW_KEY_F7),
    KEY_F8			    (GLFW.GLFW_KEY_F8),
    KEY_F9			    (GLFW.GLFW_KEY_F9),
    KEY_F10			    (GLFW.GLFW_KEY_F10),
    KEY_F11			    (GLFW.GLFW_KEY_F11),
    KEY_F12			    (GLFW.GLFW_KEY_F12),
    KEY_F13			    (GLFW.GLFW_KEY_F13),
    KEY_F14			    (GLFW.GLFW_KEY_F14),
    KEY_F15			    (GLFW.GLFW_KEY_F15),
    KEY_F16			    (GLFW.GLFW_KEY_F16),
    KEY_F17			    (GLFW.GLFW_KEY_F17),
    KEY_F18			    (GLFW.GLFW_KEY_F18),
    KEY_F19			    (GLFW.GLFW_KEY_F19),
    KEY_F20			    (GLFW.GLFW_KEY_F20),
    KEY_F21			    (GLFW.GLFW_KEY_F21),
    KEY_F22			    (GLFW.GLFW_KEY_F22),
    KEY_F23			    (GLFW.GLFW_KEY_F23),
    KEY_F24			    (GLFW.GLFW_KEY_F24),
    KEY_F25			    (GLFW.GLFW_KEY_F25),
    KEY_KP_0			(GLFW.GLFW_KEY_KP_0),
    KEY_KP_1			(GLFW.GLFW_KEY_KP_1),
    KEY_KP_2			(GLFW.GLFW_KEY_KP_2),
    KEY_KP_3			(GLFW.GLFW_KEY_KP_3),
    KEY_KP_4			(GLFW.GLFW_KEY_KP_4),
    KEY_KP_5			(GLFW.GLFW_KEY_KP_5),
    KEY_KP_6			(GLFW.GLFW_KEY_KP_6),
    KEY_KP_7			(GLFW.GLFW_KEY_KP_7),
    KEY_KP_8			(GLFW.GLFW_KEY_KP_8),
    KEY_KP_9			(GLFW.GLFW_KEY_KP_9),
    KEY_KP_DECIMAL		(GLFW.GLFW_KEY_KP_DECIMAL),
    KEY_KP_DIVIDE		(GLFW.GLFW_KEY_KP_DIVIDE),
    KEY_KP_MULTIPLY		(GLFW.GLFW_KEY_KP_MULTIPLY),
    KEY_KP_SUBTRACT		(GLFW.GLFW_KEY_KP_SUBTRACT),
    KEY_KP_ADD			(GLFW.GLFW_KEY_KP_ADD),
    KEY_KP_ENTER		(GLFW.GLFW_KEY_KP_ENTER),
    KEY_KP_EQUAL		(GLFW.GLFW_KEY_KP_EQUAL),
    KEY_LEFT_SHIFT		(GLFW.GLFW_KEY_LEFT_SHIFT),
    KEY_LEFT_CONTROL	(GLFW.GLFW_KEY_LEFT_CONTROL),
    KEY_LEFT_ALT		(GLFW.GLFW_KEY_LEFT_ALT),
    KEY_LEFT_SUPER		(GLFW.GLFW_KEY_LEFT_SUPER),
    KEY_RIGHT_SHIFT		(GLFW.GLFW_KEY_RIGHT_SHIFT),
    KEY_RIGHT_CONTROL	(GLFW.GLFW_KEY_RIGHT_CONTROL),
    KEY_RIGHT_ALT		(GLFW.GLFW_KEY_RIGHT_ALT),
    KEY_RIGHT_SUPER		(GLFW.GLFW_KEY_RIGHT_SUPER),
    KEY_MENU			(GLFW.GLFW_KEY_MENU),
    KEY_LAST			(GLFW.GLFW_KEY_MENU);

    public final int keyCode;

    @Contract(pure = true)
    KeyboardKey(int keyCode)
    {
        this.keyCode = keyCode;
    }
}
