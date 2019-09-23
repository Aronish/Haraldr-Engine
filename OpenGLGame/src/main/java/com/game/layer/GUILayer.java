package com.game.layer;

import com.game.Window;
import com.game.event.DebugScreenUpdatedEvent;
import com.game.event.Event;
import com.game.event.EventCategory;
import com.game.event.EventType;
import com.game.event.GUIToggledEvent;
import com.game.event.KeyEvent;
import com.game.graphics.font.PackedFont;
import com.game.graphics.font.TextRenderer;
import com.game.gui.GUILabel;
import com.game.gui.IGUITextComponent;
import com.game.math.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;

public class GUILayer extends Layer
{
    private PackedFont packedFont;
    private boolean guiVisible = false;

    private GUILabel debugHeader;
    private Map<PackedFont, List<IGUITextComponent>> guiComponents = new HashMap<>();

    public GUILayer(String name, Window window)
    {
        super(name);
        packedFont = new PackedFont("fonts/Roboto-Regular.ttf", window);
        debugHeader = new GUILabel(new Vector3f(0.0f, 0.0f), packedFont, "W", 50, window.getWidth(), window.getHeight());

        ArrayList<IGUITextComponent> temp = new ArrayList<>();
        temp.add(debugHeader);
        guiComponents.put(packedFont, temp);
    }

    @Override
    public void onUpdate(Window window, float deltaTime) {}

    @Override
    public void onRender()
    {
        if (guiVisible) TextRenderer.renderGuiComponents(guiComponents);
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
                window.dispatchNewEvent(new GUIToggledEvent(guiVisible));
                event.setHandled(true);
            }
        }
        if (guiVisible)
        {
            if (event.eventType == EventType.DEBUG_SCREEN_UPDATED)
            {
                DebugScreenUpdatedEvent debugScreenUpdatedEvent = (DebugScreenUpdatedEvent) event;
                //debugHeader.setText(String.format("Debug Info:\nFPá: %d\nUPS: %d", debugScreenUpdatedEvent.fps, debugScreenUpdatedEvent.ups));
                MAIN_LOGGER.info(packedFont.getStringWidth(debugHeader.text, 0, debugHeader.text.length())/*TODO Multiply with something to do with orthographic proj.*/);
                event.setHandled(true);
            }
            if (event.eventType == EventType.WINDOW_RESIZED)
            {
                debugHeader.calculateMatrix(window.getWidth(), window.getHeight());
            }
        }
    }
}