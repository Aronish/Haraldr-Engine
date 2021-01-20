package offlinerenderer;

import haraldr.dockspace.DockablePanel;
import haraldr.event.Event;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.UIComponent;

import java.util.ArrayList;
import java.util.List;

public class MainPanel extends DockablePanel
{
    private List<UIComponent> uiComponents = new ArrayList<>();

    public MainPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    public void addUIComponent(UIComponent uiComponent)
    {
        uiComponents.add(uiComponent);
    }

    @Override
    public void setPosition(Vector2f position)
    {
        for (UIComponent uiComponent : uiComponents)
        {
            uiComponent.setPosition(Vector2f.add(position, new Vector2f(0f, headerSize.getY())));
        }
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        for (UIComponent uiComponent : uiComponents)
        {
            uiComponent.setWidth(size.getX());
        }
        super.setSize(size);
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        boolean consumePress = super.onEvent(event, window), requireRedraw = false;
        for (UIComponent uiComponent : uiComponents)
        {
            if (uiComponent.onEvent(event, window)) requireRedraw = true;
        }
        if (requireRedraw) draw();
        return consumePress;
    }

    @Override
    protected void draw()
    {
        if (uiComponents == null) return;
        mainBatch.begin();
        mainBatch.drawQuad(position, size, color);
        mainBatch.drawQuad(position, headerSize, HEADER_COLOR);

        for (UIComponent uiComponent : uiComponents)
        {
            uiComponent.draw(mainBatch);
        }
        mainBatch.end();
    }
}
