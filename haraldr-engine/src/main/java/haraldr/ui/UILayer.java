package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;

import java.util.ArrayList;
import java.util.List;

public class UILayer
{
    private List<UIComponent> components = new ArrayList<>();
    private Batch2D batch = new Batch2D();
    private TextBatch textBatch = new TextBatch(Font.DEFAULT_FONT);

    public void addComponent(UIComponent component)
    {
        components.add(component);
    }

    public UIComponentBehavior.UIEventResult onEvent(Event event, Window window)
    {
        UIComponentBehavior.UIEventResult componentResult;
        boolean requiresRedraw = false, consumed = false;
        for (UIComponent component : components)
        {
            componentResult = component.onEvent(event, window);
            if (componentResult.requiresRedraw())
            {
                requiresRedraw = true;
                break;
            }
            if (componentResult.consumed())
            {
                consumed = true;
                break;
            }
        }
        return new UIComponentBehavior.UIEventResult(requiresRedraw, consumed);
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
        for (UIComponent component : components)
        {
            component.draw(batch);
        }
        batch.end();
    }

    public void render()
    {
        batch.render();
        textBatch.render();
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
