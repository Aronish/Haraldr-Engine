package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Layer;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class WindowHeader
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector2f position, size;
    private Vector4f color;

    private float currentButtonPosition;
    private List<MenuButton> menuButtons = new ArrayList<>();
    private Layer contextMenuLayer;

    private Batch2D headerBatch;
    private TextBatch headerTextBatch;

    public WindowHeader(Vector2f position, float size, Vector4f color, Layer layer, Layer contextMenuLayer)
    {
        this.position = position;
        this.size = new Vector2f(size, 20f);
        this.color = color;
        this.contextMenuLayer = contextMenuLayer;
        currentButtonPosition = position.getX();
        headerBatch = contextMenuLayer.createBatch2D();
        headerTextBatch = contextMenuLayer.createTextBatch();
    }

    public void addMenuButton(String name, ListData... listDataEntries)
    {
        MenuButton menuButton = new MenuButton(name, Vector2f.add(position, new Vector2f(currentButtonPosition, 0f)), contextMenuLayer, listDataEntries);
        menuButtons.add(menuButton);
        currentButtonPosition += menuButton.name.getPixelWidth() + MENU_BUTTON_PADDING;
        draw();
    }

    public void setWidth(float width)
    {
        size.setX(width);
        draw();
    }

    public void onEvent(Event event, Window window)
    {
        for (MenuButton menuButton : menuButtons)
        {
            if (menuButton.onEvent(event, window)) draw();
        }
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            setWidth(((WindowResizedEvent) event).width);
        }
    }

    private void draw()
    {
        headerBatch.begin();
        headerBatch.drawQuad(position, size, color);
        for (MenuButton menuButton : menuButtons)
        {
            headerBatch.drawQuad(menuButton.position, menuButton.size, menuButton.hovered ? new Vector4f(0.6f, 0.6f, 0.6f, 1f) : new Vector4f(0.4f, 0.4f, 0.4f, 1f));
            menuButton.actions.draw(headerBatch);
        }
        headerBatch.end();
    }

    public void render()
    {
        headerBatch.render();
        headerTextBatch.render();
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
        private UIVerticalList actions;

        private MenuButton(String name, Vector2f position, Layer contextMenuLayer, ListData... listDataEntries)
        {
            this.name = WindowHeader.this.headerTextBatch.createTextLabel(name, Vector2f.add(position, new Vector2f(MENU_BUTTON_PADDING / 2f, 0f)), new Vector4f(1f));
            this.position = new Vector2f(position);
            size = new Vector2f(this.name.getPixelWidth() + MENU_BUTTON_PADDING, WindowHeader.this.size.getY());

            actions = new UIVerticalList(contextMenuLayer);
            float widestEntry = 0f;
            for (ListData listDataEntry : listDataEntries)
            {
                actions.addItem(listDataEntry.name, listDataEntry.listItemPressAction);
                float labelWidth = WindowHeader.this.headerTextBatch.getFont().getPixelWidth(listDataEntry.name);
                if (labelWidth > widestEntry) widestEntry = labelWidth;
            }
            actions.setPosition(Vector2f.add(position, new Vector2f(0f, size.getY())));
            actions.setWidth(widestEntry);
            actions.setVisible(false);
        }

        private boolean onEvent(Event event, Window window)
        {
            boolean requireRedraw = false;
            if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                boolean pressed = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size);
                if (pressed)
                {
                    actions.setVisible(!actions.isVisible());
                    requireRedraw = true;
                }
            }
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                boolean previousHoveredState = hovered;
                hovered = Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), position, size);
                requireRedraw = previousHoveredState != hovered;
            }
            requireRedraw |= actions.onEvent(event, window);
            return requireRedraw;
        }
    }

    public static class ListData // Records plz
    {
        private final String name;
        private final ListItem.ListItemPressAction listItemPressAction;

        @Contract(pure = true)
        public ListData(String name, ListItem.ListItemPressAction listItemPressAction)
        {
            this.name = name;
            this.listItemPressAction = listItemPressAction;
        }
    }
}