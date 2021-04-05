package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.TagComponent;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.ListItem;
import haraldr.ui.components.UIVerticalList;
import org.jetbrains.annotations.Contract;

public class EntityHierarchyPanel extends DockablePanel
{
    private UIVerticalList entityList = new UIVerticalList(uiLayerStack, 0);
    private EntitySelectedAction entitySelectedAction;

    public EntityHierarchyPanel(Vector2f position, Vector2f size, Vector4f color, String name, EntitySelectedAction entitySelectedAction)
    {
        super(position, size, color, name);
        this.entitySelectedAction = entitySelectedAction;
        setPosition(position);
        setSize(size);
        uiLayerStack.getLayer(0).addComponent(0, new PanelModel());
        draw();
    }

    private void addEntity(String name, Entity entity)
    {
        entityList.addItem(name, size.getX(), new EntityListItem(entity));
        draw();
    }

    public void refreshEntityList(EntityRegistry entityRegistry)
    {
        uiLayerStack.clear();
        uiLayerStack.getLayer(0).addComponent(0, new PanelModel());
        uiLayerStack.getLayer(0).getTextBatch().addTextLabel(name);

        entityList.clear();
        uiLayerStack.getLayer(0).addComponent(entityList);
        entityRegistry.view(TagComponent.class).forEach(((transformComponent, tagComponent) -> addEntity(tagComponent.tag, entityRegistry.getEntityOf(tagComponent))));
        draw();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        entityList.setPosition(Vector2f.addY(position, headerSize.getY()));
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        entityList.setSize(size);
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