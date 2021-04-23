package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Component;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.graphics.Batch2D;
import haraldr.graphics.Renderer;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.UIEventLayer;
import haraldr.ui.components.UIVerticalListGroup;

public class PropertiesPanel extends DockablePanel
{
    private UIEventLayer stencilLayer = new UIEventLayer();
    private UIVerticalListGroup mainContentGroup;

    public PropertiesPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    @Override
    protected void initializeUI()
    {
        mainContentGroup = new UIVerticalListGroup(uiLayerStack, 0);
    }

    @Override
    protected void setUIPosition(Vector2f position)
    {
        mainContentGroup.setPosition(position);
    }

    @Override
    protected void setUISize(Vector2f size)
    {
        mainContentGroup.setSize(size);
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
    public void clear()
    {
        super.clear();
        mainContentGroup.clear();
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