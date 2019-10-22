package com.game.layer;

import com.game.Window;
import com.game.event.Event;
import com.game.event.EventDispatcher;
import com.game.event.EventType;
import com.game.event.GUIToggledEvent;
import com.game.event.KeyEvent;
import com.game.event.MouseMovedEvent;
import com.game.event.MousePressedEvent;
import com.game.event.MouseReleasedEvent;
import com.game.event.WindowResizedEvent;
import com.game.gui.GUIPanel;
import com.game.gui.constraint.AlignedConstraint;
import com.game.gui.constraint.AlignmentSide;
import com.game.math.Vector3f;
import com.game.math.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;

public class GUILayer extends Layer
{
    private boolean guiVisible = false;

    private GUIPanel panel1;

    public GUILayer(String name, Window window)
    {
        super(name);
        panel1 = new GUIPanel(
                new Vector3f(0.0f, 0.0f), 100, 300, new Vector4f(0.8f, 0.2f, 0.3f, 1.0f),
                new AlignedConstraint(AlignmentSide.LEFT, 10), window
        );
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
            panel1.draw();
        }
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
        if (guiVisible && event.eventType != EventType.GUI_TOGGLED)
        {
            if (event.eventType == EventType.WINDOW_RESIZED)
            {
                panel1.onResize((WindowResizedEvent) event);
            }
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                panel1.onMouseMoved((MouseMovedEvent) event, window);
            }
            if (event.eventType == EventType.MOUSE_PRESSED)
            {
                panel1.onMousePressed((MousePressedEvent) event);
            }
            if (event.eventType == EventType.MOUSE_RELEASED)
            {
                panel1.onMouseReleased((MouseReleasedEvent) event);
            }
            event.setHandled(true);
        }
    }
}