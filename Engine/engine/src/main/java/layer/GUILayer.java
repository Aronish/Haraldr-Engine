package layer;

import event.Event;
import event.EventDispatcher;
import event.EventType;
import event.GUIToggledEvent;
import event.KeyEvent;
import main.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;

public class GUILayer extends Layer
{
    private boolean guiVisible = false;

    public GUILayer(String name, Window window)
    {
        super(name);
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
    }

    @Override
    public void onRender()
    {
    }

    @Override
    public void onEvent(Window window, Event event)
    {
        if (false) LOGGER.info(event.toString());

        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (((KeyEvent) event).keyCode == GLFW_KEY_E)
            {
                guiVisible = !guiVisible;
                window.setCursorVisible(guiVisible);
                EventDispatcher.dispatch(new GUIToggledEvent(guiVisible));
            }
        }
    }
}