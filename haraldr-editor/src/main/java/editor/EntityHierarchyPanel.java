package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.TagComponent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;
import haraldr.ui.TextLabel;

import java.util.ArrayList;
import java.util.List;

public class EntityHierarchyPanel extends DockablePanel
{
    private List<EntityListItem> entityListItems = new ArrayList<>();
    private float currentListHeight;

    private EntitySelectedAction entitySelectedAction = (selectedEntity) -> {};

    public EntityHierarchyPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    public void setEntitySelectedAction(EntitySelectedAction entitySelectedAction)
    {
        this.entitySelectedAction = entitySelectedAction;
    }

    private void addEntity(String name, Entity entity)
    {
        EntityListItem entityListItem = new EntityListItem(name, Vector2f.add(position, new Vector2f(0f, currentListHeight + headerSize.getY())), entity);
        currentListHeight += entityListItem.tag.getFont().getSize();
        entityListItems.add(entityListItem);
    }

    public boolean onEvent(Event event, Window window)
    {
        boolean consumeEvent = super.onEvent(event, window);
        EntityListItem pressedListItem = null; // Defer to avoid concurrent modification
        for (EntityListItem entityListItem : entityListItems)
        {
            if (entityListItem.onEvent(event))
            {
                renderToBatch();
                if (entityListItem.pressed)
                {
                    pressedListItem = entityListItem;
                    break;
                }
            }
        }
        if (pressedListItem != null) entitySelectedAction.run(pressedListItem.entity);
        return consumeEvent;
    }

    public void refreshEntityList(EntityRegistry entityRegistry)
    {
        entityListItems.clear();
        currentListHeight = 0f;
        textBatch.clear();
        textBatch.addTextLabel(name);
        entityRegistry.view(TagComponent.class).forEach(((transformComponent, tagComponent) -> addEntity(tagComponent.tag, entityRegistry.getEntityOf(tagComponent))));
    }

    @Override
    protected void renderToBatch()
    {
        if (entityListItems == null) return;
        mainBatch.begin();
        mainBatch.drawQuad(position, size, color);
        mainBatch.drawQuad(position, headerSize, HEADER_COLOR);
        for (EntityListItem entityListItem : entityListItems)
        {
            mainBatch.drawQuad(entityListItem.position, entityListItem.size, entityListItem.hovered ? new Vector4f(0.3f, 0.3f, 0.3f, 1f) : new Vector4f(0f));
        }
        mainBatch.end();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        for (int i = 0; i < entityListItems.size(); ++i)
        {
            entityListItems.get(i).setPosition(Vector2f.add(position, new Vector2f(0f, i * textBatch.getFont().getSize() + headerSize.getY())));
        }
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        for (EntityListItem entityListItem : entityListItems)
        {
            entityListItem.size.setX(size.getX());
        }
        super.setSize(size);
    }

    public interface EntitySelectedAction
    {
        void run(Entity selectedEntity);
    }

    //TODO: Use UIVerticalList or some tree-like structure.
    private class EntityListItem
    {
        private TextLabel tag;
        private Vector2f position, size;
        private boolean hovered, pressed;
        private Entity entity;

        private EntityListItem(String name, Vector2f position, Entity entity)
        {
            this.tag = EntityHierarchyPanel.this.textBatch.createTextLabel(name, position, new Vector4f(1f));
            this.position = position;
            size = new Vector2f(EntityHierarchyPanel.this.size.getX(), tag.getFont().getSize());
            this.entity = entity;
        }

        private boolean onEvent(Event event)
        {
            boolean requiresRedraw = false;
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                boolean previousHoveredState = hovered;
                hovered = Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), position, size);
                requiresRedraw = previousHoveredState != hovered;
            }
            if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                boolean lastPressed = pressed;
                pressed = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size);
                requiresRedraw = lastPressed != pressed;
            }
            if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1))
            {
                requiresRedraw = pressed;
                pressed = false;
            }
            return requiresRedraw;
        }

        private void setPosition(Vector2f position)
        {
            this.position.set(position);
            tag.setPosition(position);
        }
    }
}