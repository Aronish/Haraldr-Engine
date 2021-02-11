package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Component;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.graphics.Renderer;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.ComponentUIVisitor;
import haraldr.ui.UIComponentList;
import haraldr.ui.UIEventLayer;
import haraldr.ui.UILayerable;
import haraldr.ui.UIVerticalListGroup;

public class PropertiesPanel extends DockablePanel
{
    private static final float SCROLL_SENSITIVITY = 10f;

    private UIEventLayer stencilLayer = new UIEventLayer();
    private UIVerticalListGroup mainContentGroup = new UIVerticalListGroup(uiLayerStack, 0);

    public PropertiesPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
        setPosition(position);
        setSize(size);
        draw();
    }

    public void populateWithEntity(Entity selected, EntityRegistry registry)
    {
        ComponentUIVisitor componentUIVisitor = new ComponentUIVisitor();
        for (Class<? extends Component> componentType : registry.getRegisteredComponentTypes())
        {
            if (registry.hasComponent(componentType, selected))
            {
                Component component = registry.getComponent(componentType, selected);
                UIComponentList uiComponentList = new UIComponentList(uiLayerStack, 0, componentType.getSimpleName().replace("Component", ""), position, size);
                componentUIVisitor.setComponentPropertyList(uiComponentList);
                component.acceptVisitor(componentUIVisitor);

                mainContentGroup.addComponent(uiComponentList);
            }
        }
        mainLayer.getTextBatch().refreshTextMeshData();
    }

    public void clear()
    {
        mainContentGroup.clear();
        uiLayerStack.clear();
        mainLayer.getTextBatch().addTextLabel(name);
    }
/*
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
*/
    @Override
    public boolean onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false, consumeEvent = super.onEvent(event, window);
        // UILayer events
        UILayerable.UIEventResult uiEventResult = uiLayerStack.onEvent(event, window);
        //if (requiresRedraw = uiEventResult.requiresRedraw()) orderComponentLists(position);
/*
        //Scroll panel
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
*/
        if (requiresRedraw) draw();
        return consumeEvent;
    }

    @Override
    protected void draw() // TODO: Render only visible
    {
        if (stencilLayer == null) return;
        //Stencil
        Batch2D stencilBatch = stencilLayer.getBatch();
        stencilBatch.begin();
        stencilBatch.drawQuad(position, size, color);
        stencilBatch.drawQuad(position, headerSize, HEADER_COLOR);
        stencilBatch.end();
        //UI
        uiLayerStack.draw();
    }

    @Override
    public void render()
    {
        Renderer.enableStencilTest();
        // Render panel and update stencil buffer to 0xFF at panel area.
        Renderer.stencilMask(0xFF);
        Renderer.stencilFunc(Renderer.StencilFunc.ALWAYS, 1, 0xFF);
        Renderer.stencilOp(Renderer.StencilOpAction.KEEP, Renderer.StencilOpAction.KEEP, Renderer.StencilOpAction.REPLACE);
        stencilLayer.render();
        Renderer.stencilMask(0x00);

        // Render UIComponentLists where stencil buffer is 0xFF.
        Renderer.stencilFunc(Renderer.StencilFunc.EQUAL, 1, 0xFF);
        uiLayerStack.render();
        Renderer.stencilMask(0xFF);
        Renderer.stencilFunc(Renderer.StencilFunc.ALWAYS, 1, 0xFF);
        Renderer.disableStencilTest();
    }
}