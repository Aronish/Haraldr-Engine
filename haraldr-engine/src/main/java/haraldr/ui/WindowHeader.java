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
import haraldr.ui.components.ListItem;
import haraldr.ui.components.UIContainer;
import haraldr.ui.components.UIVerticalList;

import java.util.ArrayList;
import java.util.List;

public class WindowHeader implements UIContainer
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector2f position, size;
    private Vector4f color;

    private float currentButtonPosition;
    private List<MenuButton> menuButtons = new ArrayList<>();

    private List<UIEventLayer> uiEventLayers = new ArrayList<>();

    public WindowHeader(Vector2f position, float size, Vector4f color)
    {
        UIEventLayer mainLayer = new UIEventLayer();
        uiEventLayers.add(mainLayer);

        this.position = position;
        this.size = new Vector2f(size, mainLayer.getTextBatch().getFont().getSize());
        this.color = color;
        currentButtonPosition = position.getX();
    }

    public void addMenuButton(String name, ListData... listDataEntries)
    {
        MenuButton menuButton = new MenuButton(name, Vector2f.addX(position, currentButtonPosition), listDataEntries);
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
        Batch2D batch = uiEventLayers.get(0).getBatch();
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
        uiEventLayers.forEach(UIEventLayer::render);
    }

    public Vector2f getSize()
    {
        return size;
    }

    @Override
    public UIEventLayer getLayer(int index)
    {
        if (index >= uiEventLayers.size())
        {
            UIEventLayer layer = new UIEventLayer();
            uiEventLayers.add(layer);
            return layer;
        }
        return uiEventLayers.get(index);
    }

    @Override
    public List<UIEventLayer> getLayers()
    {
        return uiEventLayers;
    }

    private class MenuButton //TODO: Clean up
    {
        private TextLabel name;
        private Vector2f position, size;
        private boolean hovered;
        private UIVerticalList actions;

        private MenuButton(String name, Vector2f position, ListData... listDataEntries)
        {
            this.name = WindowHeader.this.getLayers().get(0).getTextBatch().createTextLabel(name, Vector2f.addX(position, MENU_BUTTON_PADDING / 2f), new Vector4f(1f));
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
            actions.setPosition(Vector2f.addX(position, size.getY()));
            actions.setSize(new Vector2f(widestEntry, 0f));
            actions.setEnabled(false);
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
                    actions.setEnabled(!actions.isEnabled());
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