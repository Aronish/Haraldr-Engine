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
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WindowHeader implements UIContainer
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector2f position, size;
    private Vector4f color;

    private float currentButtonPosition;
    private List<MenuButton> menuButtons = new ArrayList<>();

    private Batch2D mainBatch = new Batch2D();
    private TextBatch textBatch = new TextBatch(Font.DEFAULT_FONT);

    public WindowHeader(Vector2f position, float size, Vector4f color)
    {
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
        mainBatch.begin();
        mainBatch.drawQuad(position, size, color);
        for (MenuButton menuButton : menuButtons)
        {
            mainBatch.drawQuad(menuButton.position, menuButton.size, menuButton.hovered ? new Vector4f(0.6f, 0.6f, 0.6f, 1f) : new Vector4f(0.4f, 0.4f, 0.4f, 1f));
            menuButton.actions.draw(mainBatch);
        }
        mainBatch.end();
    }

    public void render()
    {
        mainBatch.render();
        textBatch.render();
    }

    public Vector2f getSize()
    {
        return size;
    }

    @Override
    public Batch2D getMainBatch()
    {
        return mainBatch;
    }

    @Override
    public TextBatch getTextBatch()
    {
        return textBatch;
    }

    private class MenuButton
    {
        private TextLabel name;
        private Vector2f position, size;
        private boolean hovered;
        private UIVerticalList actions;

        private MenuButton(String name, Vector2f position, ListData... listDataEntries)
        {
            this.name = WindowHeader.this.textBatch.createTextLabel(name, Vector2f.add(position, new Vector2f(MENU_BUTTON_PADDING / 2f, 0f)), new Vector4f(1f));
            this.position = new Vector2f(position);
            size = new Vector2f(this.name.getPixelWidth() + MENU_BUTTON_PADDING, WindowHeader.this.size.getY());

            actions = new UIVerticalList(WindowHeader.this);
            float widestEntry = 0f;
            for (ListData listDataEntry : listDataEntries)
            {
                actions.addItem(listDataEntry.name, listDataEntry.listItemPressAction);
                float labelWidth = WindowHeader.this.textBatch.getFont().getPixelWidth(listDataEntry.name);
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
            if (actions.onEvent(event, window)) requiresRedraw = true;
            return requiresRedraw;
        }
    }

    public static class ListData // Records plz
    {
        private final String name;
        private final Consumer<String> listItemPressAction;

        @Contract(pure = true)
        public ListData(String name, Consumer<String> listItemPressAction)
        {
            this.name = name;
            this.listItemPressAction = listItemPressAction;
        }
    }
}