package haraldr.dockspace;

import haraldr.dockspace.uicomponents.Font;
import haraldr.dockspace.uicomponents.TextBatch;
import haraldr.dockspace.uicomponents.TextLabel;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.ArrayList;
import java.util.List;

public class WindowHeader
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector2f position, size;
    private Vector4f color;

    private float currentButtonPosition;
    private List<MenuButton> menuButtons = new ArrayList<>();
    private TextBatch textBatch = new TextBatch(Font.DEFAULT_FONT);

    private Batch2D renderBatch = new Batch2D();

    public WindowHeader(Vector2f position, float size, Vector4f color)
    {
        this.position = position;
        this.size = new Vector2f(size, 20f);
        this.color = color;
        currentButtonPosition = position.getX();

        addMenuButton("File");
        addMenuButton("Edit");
    }

    private void addMenuButton(String name)
    {
        MenuButton menuButton = new MenuButton(name, Vector2f.add(position, new Vector2f(currentButtonPosition, position.getY())));
        menuButtons.add(menuButton);
        currentButtonPosition += menuButton.name.getPixelWidth() + MENU_BUTTON_PADDING;

        renderToBatch();
    }

    public void setWidth(float width)
    {
        size.setX(width);
        renderToBatch();
    }

    public void onEvent(Event event)
    {
        for (MenuButton menuButton : menuButtons)
        {
            if (menuButton.onEvent(event)) renderToBatch();
        }
    }

    private void renderToBatch()
    {
        renderBatch.begin();
        renderBatch.drawQuad(position, size, color);
        for (MenuButton menuButton : menuButtons)
        {
            renderBatch.drawQuad(menuButton.position, menuButton.size, menuButton.hovered ? new Vector4f(0.6f, 0.6f, 0.6f, 1f) : new Vector4f(0.5f, 0.5f, 0.5f, 1f));
        }
        renderBatch.end();
    }

    public void render()
    {
        renderBatch.render();
        textBatch.render();
    }

    public Vector2f getSize()
    {
        return size;
    }

    private class MenuButton
    {
        private TextLabel name;
        private Vector2f position, size;
        private boolean hovered;

        private MenuButton(String name, Vector2f position)
        {
            this.name = WindowHeader.this.textBatch.createTextLabel(name, Vector2f.add(position, new Vector2f(MENU_BUTTON_PADDING / 2f, 0f)), new Vector4f(1f));
            this.position = position;
            size = new Vector2f(this.name.getPixelWidth() + MENU_BUTTON_PADDING, WindowHeader.this.size.getY());
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