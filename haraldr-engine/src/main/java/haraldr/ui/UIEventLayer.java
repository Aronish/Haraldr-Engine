package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.ui.components.UIComponent;
import haraldr.ui.components.UILayerable;

import java.util.ArrayList;
import java.util.List;

/**
 * Should handle event passthrough and handling and ui drawing/rendering. Better to do this here as it contains the UI as a flat list.
 * UIComponentGroup hierarchy will take care of positioning.
 */
public class UIEventLayer
{
    private List<UILayerable> components = new ArrayList<>();
    private Batch2D batch = new Batch2D();
    private TextBatch textBatch = new TextBatch(Font.DEFAULT_FONT);

    public void addComponent(UILayerable component)
    {
        components.add(component);
    }

    public void addComponent(int insertIndex, UILayerable component)
    {
        components.add(insertIndex, component);
    }

    public UILayerable.UIEventResult onEvent(Event event, Window window)
    {
        UILayerable.UIEventResult componentResult;
        boolean requiresRedraw = false, consumed = false;
        for (UILayerable component : components)
        {
            if (!component.isEnabled()) continue;
            componentResult = component.onEvent(event, window);
            if (componentResult.requiresRedraw())
            {
                requiresRedraw = true;
            }
            if (componentResult.consumed())
            {
                consumed = true;
                break;
            }
        }
        return new UILayerable.UIEventResult(requiresRedraw, consumed);
    }

    public void clear()
    {
        components.clear();
        batch.clear();
        textBatch.clear();
        textBatch.refreshTextMeshData();
    }

    public void draw()
    {
        batch.begin();
        for (UILayerable component : components)
        {
            if (component.isEnabled()) component.draw(batch);
        }
        batch.end();
    }

    public void render()
    {
        batch.render();
        textBatch.render();
        for (UILayerable component : components)
        {
            component.render();
        }
    }

    public Batch2D getBatch()
    {
        return batch;
    }

    public TextBatch getTextBatch()
    {
        return textBatch;
    }
}
