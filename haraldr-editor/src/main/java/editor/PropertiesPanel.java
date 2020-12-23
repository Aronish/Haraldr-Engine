package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.dockspace.uicomponents.ComponentPropertyList;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseScrolledEvent;
import haraldr.graphics.Batch2D;
import haraldr.graphics.Renderer;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class PropertiesPanel extends DockablePanel
{
    private static final float SCROLL_SENSITIVITY = 10f;

    private Batch2D listBatch = new Batch2D();
    private List<ComponentPropertyList> componentPropertyLists = new ArrayList<>();
    private float scrollOffset, listHeight;

    public PropertiesPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    public void addComponentList(ComponentPropertyList componentPropertyList)
    {
        componentPropertyLists.add(componentPropertyList);
        orderComponentLists(position);
        renderToBatch();
    }

    public void clear()
    {
        componentPropertyLists.clear();
        listBatch.clear();
        textBatch.clear();
        textBatch.addTextLabel(name);
    }

    private void orderComponentLists(Vector2f position)
    {
        float currentHeight = scrollOffset;
        for (ComponentPropertyList componentPropertyList : componentPropertyLists)
        {
            componentPropertyList.setPosition(Vector2f.add(position, new Vector2f(0f, currentHeight + headerSize.getY())));
            componentPropertyList.setSize(size);
            currentHeight += componentPropertyList.getSize().getY() + 5f;
        }
        listHeight = currentHeight - scrollOffset;
    }

    @Override
    public void onEvent(Event event, Window window)
    {
        boolean requireRedraw = false;
        super.onEvent(event, window);
        for (ComponentPropertyList componentPropertyList : componentPropertyLists)
        {
            if (componentPropertyList.onEvent(event))
            {
                orderComponentLists(position);
                requireRedraw = true;
            }
        }

        if (hovered && event.eventType == EventType.MOUSE_SCROLLED)
        {
            var mouseScrolledEvent = (MouseScrolledEvent) event;

            boolean canScroll = false;
            if (listHeight > size.getY()) canScroll = true;

            if (canScroll)
            {
                scrollOffset += mouseScrolledEvent.yOffset * SCROLL_SENSITIVITY;
                if (scrollOffset > 0f) scrollOffset = 0f;

                orderComponentLists(position);
                requireRedraw = true;
            }
        }
        if (requireRedraw) renderToBatch();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        orderComponentLists(position);
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        for (ComponentPropertyList componentPropertyList : componentPropertyLists)
        {
            componentPropertyList.setSize(size);
        }
        super.setSize(size);
        if (listHeight < size.getY()) scrollOffset = 0f;
    }

    @Override
    protected void renderToBatch() // TODO: Render only visible
    {
        if (componentPropertyLists == null) return;
        super.renderToBatch();

        listBatch.begin();
        for (ComponentPropertyList componentPropertyList : componentPropertyLists)
        {
            componentPropertyList.render(listBatch);
        }
        listBatch.end();
    }

    @Override
    public void render()
    {
        Renderer.enableStencilTest();
        // Render panel and update stencil buffer to 0xFF at panel area.
        Renderer.stencilMask(0xFF);
        Renderer.stencilFunc(Renderer.StencilFunc.ALWAYS, 1, 0xFF);
        Renderer.stencilOp(Renderer.StencilOpAction.KEEP, Renderer.StencilOpAction.KEEP, Renderer.StencilOpAction.REPLACE);
        super.render();
        Renderer.stencilMask(0x00);

        // Render UIComponentLists where stencil buffer is 0xFF.
        Renderer.stencilFunc(Renderer.StencilFunc.EQUAL, 1, 0xFF);
        listBatch.render();
        textBatch.render();
        Renderer.stencilMask(0xFF);
        Renderer.stencilFunc(Renderer.StencilFunc.ALWAYS, 1, 0xFF);
        Renderer.disableStencilTest();
    }

    @Override
    public void renderText()
    {
    }
}