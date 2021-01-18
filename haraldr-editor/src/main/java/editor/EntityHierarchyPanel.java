package editor;

import haraldr.debug.Logger;
import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.TagComponent;
import haraldr.event.Event;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.ListItem;
import haraldr.ui.TextBatch;
import haraldr.ui.UIVerticalList;

public class EntityHierarchyPanel extends DockablePanel
{
    private UIVerticalList entityList;
    private float currentListHeight;
    private EntitySelectedAction entitySelectedAction;

    public EntityHierarchyPanel(Vector2f position, Vector2f size, Vector4f color, String name, EntitySelectedAction entitySelectedAction)
    {
        super(position, size, color, name);
        entityList = new UIVerticalList(this);
        this.entitySelectedAction = entitySelectedAction;
    }

    private void addEntity(String name, Entity entity)
    {
        EntityListItem entityListItem = new EntityListItem(name, Vector2f.add(position, new Vector2f(0f, currentListHeight + headerSize.getY())), textBatch, entity);
        currentListHeight += entityListItem.getTag().getFont().getSize();
        entityList.addItem(entityListItem);
    }

    public boolean onEvent(Event event, Window window)
    {
        boolean consumeEvent = super.onEvent(event, window);
        if (entityList.onEvent(event, window)) renderToBatch();
        return consumeEvent;
    }

    private void onEntitySelected(Entity entity)
    {
        entitySelectedAction.run(entity);
    }

    public void refreshEntityList(EntityRegistry entityRegistry)
    {
        entityList.clear();
        currentListHeight = 0f;
        textBatch.clear();
        textBatch.addTextLabel(name);
        entityRegistry.view(TagComponent.class).forEach(((transformComponent, tagComponent) -> addEntity(tagComponent.tag, entityRegistry.getEntityOf(tagComponent))));
    }

    @Override
    protected void renderToBatch()
    {
        if (entityList == null) return;
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

    private class EntityListItem extends ListItem
    {
        private Entity entity;

        private EntityListItem(String name, Vector2f position, TextBatch textBatch, Entity entity)
        {
            super(name, position, textBatch, true, Logger::info);
            this.entity = entity;
        }

        @Override
        public void onItemPressed()
        {
            EntityHierarchyPanel.this.onEntitySelected(entity);
        }
    }
}