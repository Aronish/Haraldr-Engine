package editor;

import haraldr.debug.Logger;
import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.TagComponent;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.ListItem;
import haraldr.ui.components.UIButton;
import haraldr.ui.components.UIVerticalList;
import haraldr.ui.groups.UIVerticalListGroup;
import haraldr.ui.groups.VerticalListInsertData;
import org.jetbrains.annotations.Contract;

public class EntityHierarchyPanel extends DockablePanel<UIVerticalListGroup> // Only ever constraint group??
{
    private UIVerticalList entityList;
    private UIButton addEntity;
    private EntitySelectedAction entitySelectedAction;

    public EntityHierarchyPanel(Vector2f position, Vector2f size, Vector4f color, String name, EntitySelectedAction entitySelectedAction)
    {
        super(position, size, color, name);
        this.entitySelectedAction = entitySelectedAction;
    }

    @Override
    protected void initializeUI()
    {
        uiRoot = new UIVerticalListGroup();
        entityList = new UIVerticalList(uiLayerStack, 1);
        addEntity = new UIButton(uiLayerStack, 1, () -> Logger.info("ADD ENTITY"));
        addEntity.setSize(new Vector2f(200f, 20f));
        uiRoot.addComponent(new VerticalListInsertData(addEntity));
        uiRoot.addComponent(new VerticalListInsertData(entityList));
    }

    private void addEntity(String name, Entity entity)
    {
        entityList.addItem(name, size.getX(), new EntityListItem(entity));
        draw();
    }

    public void refreshEntityList(EntityRegistry entityRegistry)
    {
        clear();
        entityList.clear();
        uiLayerStack.getLayer(0).addComponent(addEntity);
        uiLayerStack.getLayer(0).addComponent(entityList);
        entityRegistry.view(TagComponent.class).forEach(((transformComponent, tagComponent) -> addEntity(tagComponent.tag, entityRegistry.getEntityOf(tagComponent))));
        draw();
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