package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.dockspace.uicomponents.TextLabel;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.ArrayList;
import java.util.List;

public class EntityHierarchyPanel extends DockablePanel
{
    private List<EntityListItem> entityListItems = new ArrayList<>();
    private float currentListHeight;

    public EntityHierarchyPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
        addEntity("TEST");
        addEntity("HELLO");
    }

    private void addEntity(String name)
    {
        EntityListItem entityListItem = new EntityListItem(name, Vector2f.add(position, new Vector2f(0f, currentListHeight + headerSize.getY())));
        currentListHeight += entityListItem.tag.getFont().getSize();
        entityListItems.add(entityListItem);
    }

    public void onEvent(Event event, Window window)
    {
        super.onEvent(event, window);
        for (EntityListItem entityListItem : entityListItems)
        {
            if (entityListItem.onEvent(event)) renderToBatch();
        }
    }

    @Override
    protected void renderToBatch()
    {
        if (entityListItems == null) return;
        renderBatch.begin();
        renderBatch.drawQuad(position, size, color);
        renderBatch.drawQuad(position, headerSize, HEADER_COLOR);
        for (EntityListItem entityListItem : entityListItems)
        {
            renderBatch.drawQuad(entityListItem.tag.getPosition(), new Vector2f(size.getX(), entityListItem.tag.getFont().getSize()), entityListItem.hovered ? new Vector4f(0.3f, 0.3f, 0.3f, 1f) : new Vector4f(0f));
        }
        renderBatch.end();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        for (int i = 0; i < entityListItems.size(); ++i)
        {
            entityListItems.get(i).tag.setPosition(Vector2f.add(position, new Vector2f(0f, i * textBatch.getFont().getSize() + headerSize.getY())));
        }
        super.setPosition(position);
    }

    private class EntityListItem
    {
        private TextLabel tag;
        private Vector2f position, size;
        private boolean hovered;

        private EntityListItem(String name, Vector2f position)
        {
            this.tag = EntityHierarchyPanel.this.textBatch.createTextLabel(name, position, new Vector4f(1f));
            this.position = position;
            size = new Vector2f(EntityHierarchyPanel.this.size.getX(), tag.getFont().getSize());
        }

        private boolean onEvent(Event event)
        {
            boolean requireRedraw = false;
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                boolean previousHoveredState = hovered;
                hovered = Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), position, size);
                requireRedraw = previousHoveredState != hovered;
            }
            return requireRedraw;
        }
    }
}
