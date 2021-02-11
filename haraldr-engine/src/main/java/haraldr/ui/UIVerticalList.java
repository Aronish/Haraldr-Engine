package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UIVerticalList extends UIComponent implements Iterable<ListItem>
{
    private List<ListItem> listItems = new ArrayList<>();
    private float listHeight;
    private boolean visible = true;
    private Vector4f backgroundColor;

    public UIVerticalList(UIContainer parent, int layerIndex)
    {
        this(parent, layerIndex, new Vector4f());
    }

    public UIVerticalList(UIContainer parent, int layerIndex, Vector4f backgroundColor)
    {
        super(parent, layerIndex);
        this.backgroundColor = backgroundColor;
    }

    public void addItem(String name, ListItem.ListItemCallback listItemCallback)
    {
        addItem(name, 0f, listItemCallback);
    }

    public void addItem(String name, float width, ListItem.ListItemCallback listItemCallback)
    {
        ListItem listItem = new ListItem(name, Vector2f.add(position, new Vector2f(0f, listHeight)), textBatch, visible, listItemCallback);
        listItem.setWidth(width);
        listHeight += listItem.getSize().getY();
        listItems.add(listItem);
    }

    public void clear()
    {
        listItems.clear();
        listHeight = 0f;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
        for (ListItem listItem : listItems)
        {
            listItem.getTag().setEnabled(visible);
        }
        textBatch.refreshTextMeshData();
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
    public void setSize(Vector2f size)
    {
        for (ListItem listItem : listItems)
        {
            listItem.setWidth(size.getX());
        }
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        for (ListItem listItem : listItems)
        {
            listItem.getTag().setEnabled(enabled);
        }
    }

    @Override
    public UIEventResult onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false, consumed = false;
        if (visible)
        {
            ListItem pressedItem = null;
            for (ListItem listItem : listItems)
            {
                ListItem.ListItemEventResult eventResult = listItem.onEvent(event);
                if (eventResult.requiresRedraw()) requiresRedraw = true;
                if (eventResult.pressedItem() != null)
                {
                    pressedItem = listItem;
                    consumed = true;
                }
            }
            if (pressedItem != null) pressedItem.getListItemCallback().onPress();
        }
        return new UIEventResult(requiresRedraw, consumed);
    }

    @Override
    public void draw(Batch2D batch)
    {
        if (visible)
        {
            for (ListItem listItem : listItems)
            {
                batch.drawQuad(listItem.getPosition(), listItem.getSize(), listItem.isHovered() ? new Vector4f(0.6f, 0.6f, 0.6f, 1f) : backgroundColor);
            }
        }
    }

    @Override
    public float getVerticalSize()
    {
        return listHeight;
    }

    @NotNull
    @Override
    public Iterator<ListItem> iterator()
    {
        return listItems.iterator();
    }

    public boolean isVisible()
    {
        return visible;
    }
}