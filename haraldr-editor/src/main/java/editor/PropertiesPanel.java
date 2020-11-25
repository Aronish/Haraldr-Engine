package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.event.Event;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class PropertiesPanel extends DockablePanel
{
    private UIComponentList uiComponentList;

    public PropertiesPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    public void setUiComponentList(UIComponentList uiComponentList)
    {
        this.uiComponentList = uiComponentList;
    }

    @Override
    public void onEvent(Event event, Window window)
    {
        super.onEvent(event, window);
        if (uiComponentList.onEvent(event)) renderToBatch();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        uiComponentList.setPosition(Vector2f.add(position, new Vector2f(0f, headerSize.getY())));
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        uiComponentList.setSize(size);
        super.setSize(size);
    }

    @Override
    protected void renderToBatch()
    {
        if (uiComponentList == null) return;
        renderBatch.begin();
        renderBatch.drawQuad(position, size, color);
        renderBatch.drawQuad(position, headerSize, HEADER_COLOR);
        uiComponentList.render(renderBatch);
        renderBatch.end();
    }
}