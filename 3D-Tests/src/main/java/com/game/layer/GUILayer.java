package com.game.layer;

import com.game.Window;
import com.game.event.DebugScreenUpdatedEvent;
import com.game.event.Event;
import com.game.event.EventType;
import com.game.event.GUIToggledEvent;
import com.game.event.KeyEvent;
import com.game.graphics.font.Fonts;
import com.game.graphics.font.TextRenderer;
import com.game.gui.GUILabel;
import com.game.math.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;

public class GUILayer extends Layer
{
    private TextRenderer textRenderer = new TextRenderer();
    private boolean guiVisible = false;

    private GUILabel fpsCounter = new GUILabel(new Vector3f(-16.0f, 7.8f), Fonts.VCR_OSD_MONO_1, "FPS: ");

    public GUILayer(String name)
    {
        super(name);
    }

    @Override
    public void onUpdate(Window window, float deltaTime) {}

    @Override
    public void onRender() {
        if (guiVisible) textRenderer.render(fpsCounter);
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
                fpsCounter.setText("FPS: " + ((DebugScreenUpdatedEvent) event).fps);
                textRenderer.setupRenderData(fpsCounter.getTextRenderData());
                event.setHandled(true);
            }
        }
    }
}
