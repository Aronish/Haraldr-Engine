package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class UIVerticalList extends UIComponent
{
    private List<ListItem> listItems = new ArrayList<>();
    private float listHeight;
    private boolean visible;

    public UIVerticalList(UIContainer parent)
    {
        super(parent);
    }

    public void addItem(String name, ListItem.ListItemPressAction listItemPressAction)
    {
        ListItem listItem;
        listItems.add(listItem = new ListItem(name, Vector2f.add(position, new Vector2f(0f, listHeight)), textBatch, listItemPressAction));
        listHeight += listItem.getSize().getY();
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
        //textBatch.setVisible(visible); // TODO: Need a way to create more text batches in children
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        for (int i = 0; i < listItems.size(); ++i)
        {
            listItems.get(i).setPosition(Vector2f.add(position, new Vector2f(0f, i * listItems.get(i).getSize().getY())));
        }
    }

    @Override
    public void setWidth(float width)
    {
        for (ListItem listItem : listItems)
        {
            listItem.setWidth(width);
        }
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        if (visible)
        {
            for (ListItem listItem : listItems)
            {
                //if (listItem.onEvent(event)) draw();
            }
        }
        return false;
    }

    @Override
    public void draw(Batch2D batch)
    {
        if (visible)
        {
            for (ListItem listItem : listItems)
            {
                batch.drawQuad(listItem.getPosition(), listItem.getSize(), listItem.isHovered() ? new Vector4f(0.6f, 0.6f, 0.6f, 1f) : new Vector4f(0.4f, 0.4f, 0.4f, 1f));
            }
        }
    }

    @Override
    public void onDispose()
    {
    }

    @Override
    public float getVerticalSize()
    {
        return listHeight;
    }

    public boolean isVisible()
    {
        return visible;
    }
}