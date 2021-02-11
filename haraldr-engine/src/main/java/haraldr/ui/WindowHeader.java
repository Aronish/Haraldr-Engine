package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.ListData;
import haraldr.ui.components.UIContainer;
import haraldr.ui.components.UIDropDownMenu;
import haraldr.ui.components.UILayerable;

import java.util.ArrayList;
import java.util.List;

public class WindowHeader implements UIContainer
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector2f position, size;
    private Vector4f color;

    private float currentButtonPosition;
    private List<UIDropDownMenu> menuButtons = new ArrayList<>();

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
        UIDropDownMenu menuButton = new UIDropDownMenu(this, 0, name, Vector2f.addX(position, currentButtonPosition), listDataEntries);
        menuButtons.add(menuButton);
        currentButtonPosition += menuButton.getName().getPixelWidth() + MENU_BUTTON_PADDING;
        draw();
    }

    public void setWidth(float width)
    {
        size.setX(width);
        draw();
    }

    public void onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false;
        UILayerable.UIEventResult eventResult;
        for (UIDropDownMenu menuButton : menuButtons)
        {
            eventResult = menuButton.onEvent(event, window);
            requiresRedraw |= eventResult.requiresRedraw();
        }
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            setWidth(((WindowResizedEvent) event).width);
        }
        if (requiresRedraw) draw();
    }

    private void draw()
    {
        Batch2D batch = uiEventLayers.get(0).getBatch();
        batch.begin();
        batch.drawQuad(position, size, color);
        for (UIDropDownMenu menuButton : menuButtons)
        {
            menuButton.draw(batch);
        }
        batch.end();
    }

    public void render()
    {
        for (UIEventLayer uiEventLayer : uiEventLayers)
        {
            uiEventLayer.render();
        }
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
}