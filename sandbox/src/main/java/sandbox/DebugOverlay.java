package sandbox;

import haraldr.event.DebugScreenUpdatedEvent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.graphics.ui.TextLabel;
import haraldr.scenegraph.Scene2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class DebugOverlay extends Scene2D
{
    private List<TextLabel> textLabels = new ArrayList<>();

    private TextLabel debugModeEnabled = new TextLabel(new Vector2f(10f, 30f), new Vector4f(1f), "Debug Mode Enabled");
    private TextLabel frameTimeData = new TextLabel(new Vector2f(10f, 60f), new Vector4f(1f), "<frame data>");

    public DebugOverlay()
    {
        textLabels.add(debugModeEnabled);
        textLabels.add(frameTimeData);
    }

    @Override
    public void onClientActivate()
    {
    }

    @Override
    public void onClientEvent(Window window, Event event)
    {
        if (event.eventType == EventType.DEBUG_SCREEN_UPDATED)
        {
            frameTimeData.setText(String.format("FPS: %d, UPS: %d", ((DebugScreenUpdatedEvent) event).fps, ((DebugScreenUpdatedEvent) event).ups));
        }
    }

    @Override
    public void onClientUpdate(Window window, float deltaTime)
    {
    }

    @Override
    public void onClientRender()
    {
    }

    @Override
    public void onClientDispose()
    {
    }
}
