package sandbox;

import haraldr.event.DebugScreenUpdatedEvent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.graphics.ui.TextLabel;
import haraldr.main.Scene;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class DebugOverlay implements Scene
{
    private List<TextLabel> textLabels = new ArrayList<>();

    private TextLabel debugModeEnabled = new TextLabel(new Vector2f(10f, 30f), new Vector4f(1f), "Debug Mode Enabled");
    private TextLabel frameTimeData = new TextLabel(new Vector2f(10f, 60f), new Vector4f(1f), "<frame data>");
    private TextLabel babbel = new TextLabel(new Vector2f(10f, 90f), new Vector4f(0.8f),
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas quis dui a quam egestas vulputate. Nulla eget enim at nunc volutpat efficitur sit amet eu enim.\n" +
                    "Suspendisse mauris velit, pellentesque eu lorem ac, mattis pulvinar eros. Nunc sit amet dapibus justo. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae;\n" +
                    "Duis quis dui vel eros bibendum euismod finibus at arcu. Phasellus tincidunt ante nec nisi finibus venenatis. Phasellus tincidunt ut mi sed interdum. Nullam id eros cursus, fringilla dui a, venenatis felis.\n" +
                    "Nam nulla orci, varius eu rhoncus vitae, tristique nec lacus. Quisque gravida nisl leo, eu vehicula tortor condimentum quis. Suspendisse potenti. Ut rhoncus mauris non tempus vulputate.\n" +
                    "Integer consectetur at mi eu lobortis. Nulla facilisi. Aliquam eget enim suscipit, vulputate eros in, faucibus mi. Sed vestibulum porttitor convallis.\n" +
                    "Mauris at libero maximus, sodales nisl pulvinar, molestie quam. Nunc id metus sed elit sagittis hendrerit.\n" +
                    "In non lectus nulla. Donec vestibulum, leo in facilisis tristique, nisi arcu tincidunt augue, sed varius enim nisl cursus nulla. Vivamus id mi non libero facilisis condimentum a in nisi.\n" +
                    "Proin sagittis libero vitae nisi viverra, eu imperdiet neque sodales. Integer velit nibh, suscipit vel rutrum in, auctor ac arcu.\n" +
                    "Vestibulum mollis, odio eu consectetur maximus, lectus arcu rutrum magna, vitae pulvinar odio erat nec dolor. Aliquam et massa risus. Mauris convallis odio eu orci ornare vulputate.\n" +
            "\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas quis dui a quam egestas vulputate. Nulla eget enim at nunc volutpat efficitur sit amet eu enim.\n" +
                    "Suspendisse mauris velit, pellentesque eu lorem ac, mattis pulvinar eros. Nunc sit amet dapibus justo. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae;\n" +
                    "Duis quis dui vel eros bibendum euismod finibus at arcu. Phasellus tincidunt ante nec nisi finibus venenatis. Phasellus tincidunt ut mi sed interdum. Nullam id eros cursus, fringilla dui a, venenatis felis.\n" +
                    "Nam nulla orci, varius eu rhoncus vitae, tristique nec lacus. Quisque gravida nisl leo, eu vehicula tortor condimentum quis. Suspendisse potenti. Ut rhoncus mauris non tempus vulputate.\n" +
                    "Integer consectetur at mi eu lobortis. Nulla facilisi. Aliquam eget enim suscipit, vulputate eros in, faucibus mi. Sed vestibulum porttitor convallis.\n" +
                    "Mauris at libero maximus, sodales nisl pulvinar, molestie quam. Nunc id metus sed elit sagittis hendrerit.\n" +
                    "In non lectus nulla. Donec vestibulum, leo in facilisis tristique, nisi arcu tincidunt augue, sed varius enim nisl cursus nulla. Vivamus id mi non libero facilisis condimentum a in nisi.\n" +
                    "Proin sagittis libero vitae nisi viverra, eu imperdiet neque sodales. Integer velit nibh, suscipit vel rutrum in, auctor ac arcu.\n" +
                    "Vestibulum mollis, odio eu consectetur maximus, lectus arcu rutrum magna, vitae pulvinar odio erat nec dolor. Aliquam et massa risus. Mauris convallis odio eu orci ornare vulputate."
    );

    public DebugOverlay()
    {
        textLabels.add(debugModeEnabled);
        textLabels.add(frameTimeData);
    }

    @Override
    public void onActivate()
    {

    }

    @Override
    public void onEvent(Event event)
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
    }

    @Override
    public void onDispose()
    {
    }
}
