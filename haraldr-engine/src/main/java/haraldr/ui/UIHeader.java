package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.ListData;
import haraldr.ui.components.UIComponent;
import haraldr.ui.components.UIContainer;
import haraldr.ui.components.UIDropDownMenu;

public class UIHeader extends UIComponent
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector4f color;
    private float currentButtonPosition;

    public UIHeader(UIContainer parent, int layerIndex, Vector2f position, Vector2f size, Vector4f color)
    {
        super(parent, layerIndex);

        setPosition(position);
        setSize(size);
        this.color = color;
        currentButtonPosition = position.getX();
    }

    public void addMenuButton(String name, ListData... listDataEntries)
    {
        UIDropDownMenu menuButton = new UIDropDownMenu(parent, 0, name, Vector2f.addX(position, currentButtonPosition), listDataEntries, true);
        currentButtonPosition += menuButton.getName().getPixelWidth() + MENU_BUTTON_PADDING;
    }

    @Override
    public UIEventResult onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false;
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            setSize(new Vector2f(((WindowResizedEvent)event).width, size.getY()));
            requiresRedraw = true;
        }
        return new UIEventResult(requiresRedraw, false);
    }

    @Override
    public void draw(Batch2D batch)
    {
        batch.drawQuad(position, size, color);
    }
}