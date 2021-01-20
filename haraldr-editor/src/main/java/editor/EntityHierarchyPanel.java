package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.TagComponent;
import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.ListItem;
import haraldr.ui.UIVerticalList;
import org.jetbrains.annotations.Contract;

public class EntityHierarchyPanel extends DockablePanel
{
    private UIVerticalList entityList = new UIVerticalList(this);
    private EntitySelectedAction entitySelectedAction;

    public EntityHierarchyPanel(Vector2f position, Vector2f size, Vector4f color, String name, EntitySelectedAction entitySelectedAction)
    {
        super(position, size, color, name);
        this.entitySelectedAction = entitySelectedAction;
        setPosition(position);
        setSize(size);
        renderToBatch();
    }

    private void addEntity(String name, Entity entity)
    {
        entityList.addItem(name, size.getX(), new EntityListItem(entity));
        renderToBatch();
    }

    public boolean onEvent(Event event, Window window)
    {
        boolean consumeEvent = super.onEvent(event, window);
        if (entityList.onEvent(event, window).requiresRedraw()) renderToBatch();
        return consumeEvent;
    }

    public void refreshEntityList(EntityRegistry entityRegistry)
    {
        entityList.clear();
        //textBatch.clear();
        //textBatch.addTextLabel(name);
        entityRegistry.view(TagComponent.class).forEach(((transformComponent, tagComponent) -> addEntity(tagComponent.tag, entityRegistry.getEntityOf(tagComponent))));
    }

    @Override
    protected void renderToBatch()
    {
        if (entityList == null) return;
        Batch2D mainBatch = batches.get(0);
        mainBatch.begin();
        mainBatch.drawQuad(position, size, color);
        mainBatch.drawQuad(position, headerSize, HEADER_COLOR);
        entityList.draw(mainBatch);
        mainBatch.end();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        entityList.setPosition(Vector2f.add(position, new Vector2f(0f, headerSize.getY())));
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        entityList.setWidth(size.getX());
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