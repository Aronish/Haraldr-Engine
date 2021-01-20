package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;

import java.util.ArrayList;
import java.util.List;

public class UILayer implements UIContainer
{
    private List<UIComponent> components = new ArrayList<>();
    private Batch2D batch = new Batch2D();
    private TextBatch textBatch = new TextBatch(Font.DEFAULT_FONT);

    public void addComponent(UIComponent component)
    {
        components.add(component);
    }

    public boolean onEvent(Event event, Window window)
    {
        UIComponentBehavior.UIEventResult eventResult;
        for (UIComponent component : components)
        {
            eventResult = component.onEvent(event, window);
            if (eventResult.consumed()) return true;
        }
        return false;
    }

    public void render()
    {
        batch.render();
        textBatch.render();
    }

    @Override
    public Batch2D getBatch()
    {
        return batch;
    }

    @Override
    public TextBatch getTextBatch()
    {
        return textBatch;
    }
}
