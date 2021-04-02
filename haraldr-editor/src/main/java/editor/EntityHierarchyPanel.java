package editor;

import haraldr.debug.Logger;
import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.TagComponent;
import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.ListItem;
import haraldr.ui.components.UIDropDownSelector;
import haraldr.ui.components.UIVerticalList;
import org.jetbrains.annotations.Contract;

public class EntityHierarchyPanel extends DockablePanel
{
    private UIVerticalList entityList = new UIVerticalList(uiLayerStack, 0);
    private EntitySelectedAction entitySelectedAction;

    private UIDropDownSelector test = new UIDropDownSelector(uiLayerStack, 1);

    public EntityHierarchyPanel(Vector2f position, Vector2f size, Vector4f color, String name, EntitySelectedAction entitySelectedAction)
    {
        super(position, size, color, name);
        this.entitySelectedAction = entitySelectedAction;
        setPosition(position);
        setSize(size);
        test.addMenuItem("Test 1", () -> Logger.info("Test 1"));
        test.addMenuItem("Test 2", () -> Logger.info("Test 2"));
        test.addMenuItem("Test 3", () -> Logger.info("Test 3"));
        draw();
    }

    private void addEntity(String name, Entity entity)
    {
        entityList.addItem(name, size.getX(), new EntityListItem(entity));
        draw();
    }

    public boolean onEvent(Event event, Window window)
    {
        boolean consumeEvent = super.onEvent(event, window);
        if (entityList.onEvent(event, window).requiresRedraw()) draw();
        return consumeEvent;
    }

    public void refreshEntityList(EntityRegistry entityRegistry)
    {
        entityList.clear();
        uiLayerStack.getLayer(0).getTextBatch().clear();
        uiLayerStack.getLayer(0).getTextBatch().addTextLabel(name);
        entityRegistry.view(TagComponent.class).forEach(((transformComponent, tagComponent) -> addEntity(tagComponent.tag, entityRegistry.getEntityOf(tagComponent))));
    }

    @Override
    protected void draw()
    {
        if (entityList == null) return; //TODO: Streamline
        Batch2D mainBatch = uiLayerStack.getLayer(0).getBatch();
        mainBatch.begin();
        mainBatch.drawQuad(position, size, color);
        mainBatch.drawQuad(position, headerSize, HEADER_COLOR);
        entityList.draw(mainBatch);
        mainBatch.end();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        entityList.setPosition(Vector2f.addY(position, headerSize.getY()));
        test.setPosition(Vector2f.addY(position, entityList.getSize().getY() + 100f));
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        entityList.setSize(size);
        test.setSize(new Vector2f(size.getX(), 20f));
        super.setSize(size);
    }

    public interface EntitySelectedAction
    {
        void run(Entity selectedEntity);
    }

    private class EntityListItem implements ListItem.ListItemCallback
    {
        private Entity entity;

        @Contract(pure = true)
        private EntityListItem(Entity entity)
        {
            this.entity = entity;
        }

        @Override
        public void onPress()
        {
            EntityHierarchyPanel.this.entitySelectedAction.run(entity);
        }
    }
}