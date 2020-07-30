package sandbox;

import haraldr.event.DebugScreenUpdatedEvent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.graphics.ui.Font;
import haraldr.graphics.ui.TextLabel;
import haraldr.layer.Layer;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DebugLayer extends Layer
{
    private List<TextLabel> textLabels = new ArrayList<>();

    private Font font = new Font("default_fonts/Roboto-Regular.ttf", 30);
    private TextLabel debugModeEnabled = new TextLabel(new Vector2f(10f, 30f), font, new Vector4f(1f), "Debug Mode Enabled");
    private TextLabel frameTimeData = new TextLabel(new Vector2f(10f, 60f), font, new Vector4f(1f), "<frame data>");

    public DebugLayer()
    {
        textLabels.add(debugModeEnabled);
        textLabels.add(frameTimeData);
    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.DEBUG_SCREEN_UPDATED)
        {
            frameTimeData.setText(String.format("FPS: %d, UPS: %d", ((DebugScreenUpdatedEvent) event).fps, ((DebugScreenUpdatedEvent) event).ups));
        }
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {

    }

    @Override
    public void onRender()
    {
        for (TextLabel textLabel : textLabels) textLabel.render();
    }

    @Override
    public void onDispose()
    {
        textLabels.forEach(TextLabel::delete);
        font.delete();
    }
}
