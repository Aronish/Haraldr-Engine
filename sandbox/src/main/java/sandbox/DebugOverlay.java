package sandbox;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.graphics.ui.TextLabel;
import haraldr.scene.Scene2D;
import haraldr.main.Window;

import java.util.ArrayList;
import java.util.List;

public class DebugOverlay extends Scene2D
{
    private List<TextLabel> textLabels = new ArrayList<>();

    //private TextLabel debugModeEnabled = new TextLabel("Debug Mode Enabled", new Vector2f(10f, 30f), new Vector4f(1f));
    //private TextLabel frameTimeData = new TextLabel("<frame data>", new Vector2f(10f, 60f), new Vector4f(1f));

    public DebugOverlay()
    {
        //textLabels.add(debugModeEnabled);
        //textLabels.add(frameTimeData);
    }

    @Override
    public void onClientActivate(Window window)
    {
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
        if (event.eventType == EventType.DEBUG_SCREEN_UPDATED)
        {
            //frameTimeData.setText(String.format("FPS: %d, UPS: %d", ((DebugScreenUpdatedEvent) event).fps, ((DebugScreenUpdatedEvent) event).ups));
        }
    }

    @Override
    protected void onClientUpdate(float deltaTime, Window window)
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
