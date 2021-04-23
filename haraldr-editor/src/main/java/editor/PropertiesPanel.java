package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.dockspace.Dockspace;
import haraldr.ecs.Component;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.graphics.Renderer;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.UIEventLayer;
import haraldr.ui.components.UIContainer;
import haraldr.ui.components.UILayerable;
import haraldr.ui.components.UIVerticalListGroup;

public class PropertiesPanel extends DockablePanel
{
    private UIEventLayer stencilLayer = new UIEventLayer();
    private UIVerticalListGroup mainContentGroup;

    public PropertiesPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    public void populateWithEntity(Entity selected, EntityRegistry registry)
    {
        ComponentUIVisitor componentUIVisitor = new ComponentUIVisitor();
        for (Class<? extends Component> componentType : registry.getRegisteredComponentTypes())
        {
            if (registry.hasComponent(componentType, selected))
            {
                Component component = registry.getComponent(componentType, selected);
                ECSComponentGroup ecsComponentGroup = new ECSComponentGroup(uiLayerStack, 0, componentType.getSimpleName().replace("Component", ""), position, size);
                componentUIVisitor.setComponentPropertyList(ecsComponentGroup);
                component.acceptVisitor(componentUIVisitor);

                mainContentGroup.addComponent(ecsComponentGroup);
            }
        }
        uiLayerStack.getLayer(0).getTextBatch().refreshTextMeshData();
        draw();
    }

    @Override
    protected void initializeUIPositioning()
    {
        mainContentGroup = new UIVerticalListGroup(uiLayerStack, 0);
        mainContentGroup.setPosition(Vector2f.addY(position, headerSize.getY()));
        mainContentGroup.setSize(Vector2f.addY(size, headerSize.getY()));
    }

    @Override
    public void clear()
    {
        super.clear();
        mainContentGroup.clear();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        mainContentGroup.setPosition(Vector2f.addY(position, headerSize.getY()));
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        mainContentGroup.setSize(Vector2f.addY(size, headerSize.getY()));
        super.setSize(size);
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
        super.draw();
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