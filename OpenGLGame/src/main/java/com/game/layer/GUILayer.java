package com.game.layer;

import com.game.Window;
import com.game.event.Event;
import com.game.event.EventCategory;
import com.game.event.EventDispatcher;
import com.game.event.EventType;
import com.game.event.GUIToggledEvent;
import com.game.event.KeyEvent;
import com.game.event.MouseMovedEvent;
import com.game.event.WindowResizedEvent;
import com.game.gui.GUIPanel;
import com.game.math.Vector3f;
import com.game.math.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;

public class GUILayer extends Layer
{
    private boolean guiVisible = false;

    private GUIPanel panel;

    public GUILayer(String name, Window window)
    {
        super(name);
        panel = new GUIPanel(new Vector3f(0.0f, 0.0f), 100, 500, new Vector4f(0.8f, 0.2f, 0.3f, 1.0f));
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
    }

    @Override
    public void onRender()
    {
        if (guiVisible)
        {
            panel.draw();
        }
    }

    @Override
    public void onEvent(Window window, Event event)
    {
        if (event.isInCategory(EventCategory.CATEGORY_WINDOW))
        {
            //LOGGER.info(event.toString());
        }
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (((KeyEvent) event).keyCode == GLFW_KEY_U)
            {
                guiVisible = !guiVisible;
                event.setHandled(true);
                EventDispatcher.dispatch(new GUIToggledEvent(guiVisible));
            }
        }
        if (guiVisible)
        {
            if (event.eventType == EventType.WINDOW_RESIZED)
            {
                panel.onResize((WindowResizedEvent) event);
            }
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                panel.onMouseMoved((MouseMovedEvent) event);
            }
        }
    }
}