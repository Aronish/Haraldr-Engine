package editor;

import haraldr.debug.Logger;
import haraldr.dockspace.DockablePanel;
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
    private List<UIComponentList> uiComponentLists = new ArrayList<>();
    private float scrollOffset, listHeight;

    public PropertiesPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    public void addComponentList(UIComponentList uiComponentList)
    {
        uiComponentLists.add(uiComponentList);
        orderComponentLists(position);
        renderToBatch();
    }

    private void orderComponentLists(Vector2f position)
    {
        float currentHeight = scrollOffset;
        for (UIComponentList uiComponentList : uiComponentLists)
        {
            uiComponentList.setPosition(Vector2f.add(position, new Vector2f(0f, currentHeight + headerSize.getY())));
            uiComponentList.setSize(size);
            currentHeight += uiComponentList.getSize().getY() + 5f;
        }
        listHeight = currentHeight;
    }

    @Override
    public void onEvent(Event event, Window window)
    {
        boolean requireRedraw = false;
        super.onEvent(event, window);
        for (UIComponentList uiComponentList : uiComponentLists)
        {
            if (uiComponentList.onEvent(event))
            {
                orderComponentLists(position);
                requireRedraw = true;
            }
        }

        if (hovered && event.eventType == EventType.MOUSE_SCROLLED)
        {
            var mouseScrolledEvent = (MouseScrolledEvent) event;
            scrollOffset += mouseScrolledEvent.yOffset * SCROLL_SENSITIVITY;
            boolean canScroll = true;
            Logger.info(scrollOffset + " " + listHeight);

            if (scrollOffset > 0f)
            {
                scrollOffset = 0f;
                canScroll = false;
            }
            if (listHeight < size.getY())
            {
                canScroll = false;
            } //TODO: FIX

            if (canScroll)
            {
                for (UIComponentList uiComponentList : uiComponentLists)
                {
                    uiComponentList.addPosition(new Vector2f(0f, mouseScrolledEvent.yOffset * SCROLL_SENSITIVITY));
                }
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
        for (UIComponentList uiComponentList : uiComponentLists)
        {
            uiComponentList.setSize(size);
        }
        super.setSize(size);
    }

    @Override
    protected void renderToBatch() // TODO: Render only visible
    {
        if (uiComponentLists == null) return;
        super.renderToBatch();

        listBatch.begin();
        for (UIComponentList uiComponentList : uiComponentLists)
        {
            uiComponentList.render(listBatch);
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