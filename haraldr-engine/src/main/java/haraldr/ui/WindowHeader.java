package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.ArrayList;
import java.util.List;

public class WindowHeader implements UIContainer
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector2f position, size;
    private Vector4f color;

    private float currentButtonPosition;
    private List<MenuButton> menuButtons = new ArrayList<>();

    private List<UILayer> uiLayers = new ArrayList<>();

    public WindowHeader(Vector2f position, float size, Vector4f color)
    {
        uiLayers.add(new UILayer());

        this.position = position;
        this.size = new Vector2f(size, 20f);
        this.color = color;
        currentButtonPosition = position.getX();
    }

    public void addMenuButton(String name, ListData... listDataEntries)
    {
        MenuButton menuButton = new MenuButton(name, Vector2f.add(position, new Vector2f(currentButtonPosition, 0f)), listDataEntries);
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
        Batch2D batch = uiLayers.get(0).getBatch();
        batch.begin();
        batch.drawQuad(position, size, color);
        for (MenuButton menuButton : menuButtons)
        {
            batch.drawQuad(menuButton.position, menuButton.size, menuButton.hovered ? new Vector4f(0.6f, 0.6f, 0.6f, 1f) : new Vector4f(0.4f, 0.4f, 0.4f, 1f));
            menuButton.actions.draw(batch);
        }
        batch.end();
    }

    public void render()
    {
        uiLayers.forEach(UILayer::render);
    }

    public Vector2f getSize()
    {
        return size;
    }

    @Override
    public UILayer getLayer(int index)
    {
        if (index >= uiLayers.size())
        {
            UILayer layer = new UILayer();
            uiLayers.add(layer);
            return layer;
        }
        return uiLayers.get(index);
    }

    @Override
    public List<UILayer> getLayers()
    {
        return uiLayers;
    }

    private class MenuButton //TODO: Clean up
    {
        private TextLabel name;
        private Vector2f position, size;
        private boolean hovered;
        private UIVerticalList actions;

        private MenuButton(String name, Vector2f position, ListData... listDataEntries)
        {
            this.name = WindowHeader.this.getLayers().get(0).getTextBatch().createTextLabel(name, Vector2f.add(position, new Vector2f(MENU_BUTTON_PADDING / 2f, 0f)), new Vector4f(1f));
            this.position = new Vector2f(position);
            size = new Vector2f(this.name.getPixelWidth() + MENU_BUTTON_PADDING, WindowHeader.this.size.getY());

            actions = new UIVerticalList(WindowHeader.this, 0, new Vector4f(0.4f, 0.4f, 0.4f, 1f));
            float widestEntry = 0f;
            for (ListData listDataEntry : listDataEntries)
            {
                actions.addItem(listDataEntry.name, listDataEntry.listItemCallback);
                float labelWidth = this.name.getFont().getPixelWidth(listDataEntry.name);
                if (labelWidth > widestEntry) widestEntry = labelWidth;
            }
            actions.setPosition(Vector2f.add(position, new Vector2f(0f, size.getY())));
            actions.setWidth(widestEntry);
            actions.setVisible(false);
        }

        private boolean onEvent(Event event, Window window)
        {
            boolean requiresRedraw = false;
            if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                boolean pressed = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size);
                if (pressed)
                {
                    actions.setVisible(!actions.isVisible());
                    requiresRedraw = true;
                }
            }
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                boolean previousHoveredState = hovered;
                hovered = Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), position, size);
                requiresRedraw = previousHoveredState != hovered;
            }
            requiresRedraw |= actions.onEvent(event, window).requiresRedraw();
            return requiresRedraw;
        }
    }

    public static record ListData(String name, ListItem.ListItemCallback listItemCallback) {}
}