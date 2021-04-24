package editor;

import haraldr.debug.Logger;
import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Component;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.graphics.Batch2D;
import haraldr.graphics.Renderer;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.UIEventLayer;
import haraldr.ui.components.UIButton;
import haraldr.ui.groups.ConstraintInsertData;
import haraldr.ui.groups.UIConstraintGroup;
import haraldr.ui.groups.UIOffset;
import haraldr.ui.groups.UIRelativePosition;
import haraldr.ui.groups.UIRelativeWidth;
import haraldr.ui.groups.UISide;
import haraldr.ui.groups.UIVerticalListGroup;
import haraldr.ui.groups.VerticalListInsertData;

public class PropertiesPanel extends DockablePanel<UIConstraintGroup>
{
    private UIEventLayer stencilLayer = new UIEventLayer();
    private UIVerticalListGroup propertiesList;
    private UIButton addComponent;

    public PropertiesPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    @Override
    protected void initializeUI()
    {
        uiRoot = new UIConstraintGroup();
        propertiesList = new UIVerticalListGroup();
        addComponent = new UIButton(uiLayerStack, 0, () -> Logger.info("ADD COMPONENT"));
        addComponent.setSize(new Vector2f(0f, 20f));
        addComponent.setEnabled(false);

        uiRoot.addComponent(new ConstraintInsertData(
                addComponent,
                new UIOffset(addComponent, uiRoot, new Vector2f(0f, 5f)),
                new UIRelativeWidth(addComponent, uiRoot, 1f)
        ));
        uiRoot.addComponent(new ConstraintInsertData(
                propertiesList,
                new UIRelativePosition(propertiesList, addComponent, UISide.BOTTOM, 5f),
                new UIRelativeWidth(propertiesList, uiRoot, 1f)
        ));
    }

    public void populateWithEntity(Entity selected, EntityRegistry registry)
    {
        uiLayerStack.getLayer(0).addComponent(addComponent);
        addComponent.setEnabled(true);
        ComponentUIVisitor componentUIVisitor = new ComponentUIVisitor();
        for (Class<? extends Component> componentType : registry.getRegisteredComponentTypes())
        {
            if (registry.hasComponent(componentType, selected))
            {
                Component component = registry.getComponent(componentType, selected);
                ECSComponentGroup ecsComponentGroup = new ECSComponentGroup(uiLayerStack, 0, componentType.getSimpleName().replace("Component", ""), position, size);
                componentUIVisitor.setComponentPropertyList(ecsComponentGroup);
                component.acceptVisitor(componentUIVisitor);

                propertiesList.addComponent(new VerticalListInsertData(ecsComponentGroup));
            }
        }
        uiLayerStack.getLayer(0).getTextBatch().refreshTextMeshData();
        draw();
    }

    @Override
    public void clear()
    {
        super.clear();
        propertiesList.clear();
        addComponent.setEnabled(false);
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