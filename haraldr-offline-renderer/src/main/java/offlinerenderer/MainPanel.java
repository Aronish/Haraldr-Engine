package offlinerenderer;

import haraldr.dockspace.DockablePanel;
import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.UILayerStack;
import haraldr.ui.components.UIComponent;
import haraldr.ui.components.UILabeledList;

import java.util.ArrayList;
import java.util.List;

public class MainPanel extends DockablePanel
{
    private UILabeledList uiComponentList = new UILabeledList(uiLayerStack, 0, Vector2f.addY(position, headerSize.getY()), new Vector2f());

    public MainPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    public void addComponent(String label, UIComponent uiComponent)
    {
        uiComponentList.addComponent(label, uiComponent);
    }

    @Override
    public void setPosition(Vector2f position)
    {
        uiComponentList.setPosition(Vector2f.addY(position, headerSize.getY()));
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        uiComponentList.setSize(size);
        super.setSize(size);
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        boolean consumePress = super.onEvent(event, window), requireRedraw = uiComponentList.onEvent(event, window).requiresRedraw();
        if (requireRedraw) draw();
        return consumePress;
    }

    @Override
    protected void draw()
    {
        if (uiComponentList == null) return;
        Batch2D mainBatch = mainLayer.getBatch();
        mainBatch.begin();
        mainBatch.drawQuad(position, size, color);
        mainBatch.drawQuad(position, headerSize, HEADER_COLOR);

        uiComponentList.draw(mainBatch);
        mainBatch.end();
    }
}
