package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseScrolledEvent;
import haraldr.graphics.Batch2D;
import haraldr.graphics.Renderer;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.UIComponentList;

import java.util.ArrayList;
import java.util.List;

public class PropertiesPanel extends DockablePanel
{
    private static final float SCROLL_SENSITIVITY = 10f;

    private Batch2D listBatch = new Batch2D(), overlayBatch = new Batch2D();
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

    public void clear()
    {
        uiComponentLists.forEach(UIComponentList::onDispose);
        uiComponentLists.clear();
        listBatch.clear();
        textBatch.clear();
        textBatch.addTextLabel(name);
    }

    private void orderComponentLists(Vector2f position)
    {
        float currentHeight = scrollOffset;
        for (UIComponentList uiComponentList : uiComponentLists)
        {
            uiComponentList.setPosition(Vector2f.add(position, new Vector2f(0f, currentHeight + headerSize.getY())));
            uiComponentList.setWidth(size.getX());
            currentHeight += uiComponentList.getVerticalSize() + 5f;
        }
        listHeight = currentHeight - scrollOffset;
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false, consumeEvent = super.onEvent(event, window);
        for (UIComponentList uiComponentList : uiComponentLists)
        {
            if (uiComponentList.onEvent(event, window))
            {
                requiresRedraw = true;
                orderComponentLists(position); //Minor TODO: Does not need to happen every event, only if the collapsed state of a list has changed
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
                requiresRedraw = true;
            }
        }
        if (requiresRedraw) renderToBatch();
        return consumeEvent;
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
            uiComponentList.setWidth(size.getX());
        }
        super.setSize(size);
        if (listHeight < size.getY()) scrollOffset = 0f;
    }

    @Override
    protected void renderToBatch() // TODO: Render only visible
    {
        if (uiComponentLists == null) return;
        super.renderToBatch();

        listBatch.begin();
        overlayBatch.begin();
        for (UIComponentList uiComponentList : uiComponentLists)
        {
            uiComponentList.draw(listBatch);
            uiComponentList.drawOverlay(overlayBatch);
        }
        overlayBatch.end();
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
        overlayBatch.render();
        Renderer.stencilMask(0xFF);
        Renderer.stencilFunc(Renderer.StencilFunc.ALWAYS, 1, 0xFF);
        Renderer.disableStencilTest();
    }

    @Override
    public Batch2D getMainBatch()
    {
        return listBatch;
    }

    @Override
    public Batch2D getOverlayBatch()
    {
        return overlayBatch;
    }
}