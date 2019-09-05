package com.game.layer;

import com.game.Window;
import com.game.event.DebugScreenUpdatedEvent;
import com.game.event.Event;
import com.game.event.EventType;
import com.game.event.GUIToggledEvent;
import com.game.event.KeyEvent;
import com.game.graphics.font.Font;
import com.game.graphics.font.Fonts;
import com.game.graphics.font.TextRenderer;
import com.game.gui.GUILabel;
import com.game.math.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;

public class GUILayer extends Layer
{
    private TextRenderer textRenderer = new TextRenderer();
    private boolean guiVisible = false;

    private GUILabel debugHeader = new GUILabel(new Vector3f(-16.0f, 9.0f), Fonts.ROBOTO_REGULAR);

    private Map<Font, List<GUILabel>> guiComponents = new HashMap<>();

    public GUILayer(String name)
    {
        super(name);
        ArrayList<GUILabel> temp = new ArrayList<>();
        temp.add(debugHeader);
        guiComponents.put(Fonts.ROBOTO_REGULAR, temp);
    }

    @Override
    public void onUpdate(Window window, float deltaTime) {}

    @Override
    public void onRender()
    {
        if (guiVisible) textRenderer.renderGuiComponents(guiComponents);
    }

    @Override
    public void onEvent(Window window, Event event)
    {
        //LOGGER.info(event.toString());
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (((KeyEvent) event).keyCode == GLFW_KEY_U)
            {
                guiVisible = !guiVisible;
                window.dispatchNewEvent(new GUIToggledEvent(guiVisible));
                event.setHandled(true);
            }
        }
        if (guiVisible)
        {
            if (event.eventType == EventType.DEBUG_SCREEN_UPDATED)
            {
                DebugScreenUpdatedEvent debugScreenUpdatedEvent = (DebugScreenUpdatedEvent) event;
                debugHeader.setText(String.format("Debug Info:\nFPS: %d\nUPS: %d", debugScreenUpdatedEvent.fps, debugScreenUpdatedEvent.ups));
                event.setHandled(true);
            }
        }
    }
}