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
import java.util.function.Consumer;

public class UIVerticalList extends UIComponent implements Iterable<ListItem>
{
    private List<ListItem> listItems = new ArrayList<>();
    private float listHeight;
    private boolean visible;

    public UIVerticalList(UIContainer parent)
    {
        super(parent);
    }

    public void addItem(String name, Consumer<String> listItemPressAction)
    {
        addItem(new ListItem(name, Vector2f.add(position, new Vector2f(0f, listHeight)), textBatch, visible, listItemPressAction));
    }

    public void addItem(ListItem listItem) //TODO: "Merge" with EntityHierarchyPanel
    {
        listItems.add(listItem);
        listHeight += listItem.getSize().getY();
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
        boolean requiresRedraw = false;
        if (visible)
        {
            for (ListItem listItem : listItems)
            {
                if (listItem.onEvent(event)) requiresRedraw = true;
            }
        }
        return requiresRedraw;
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
    public float getVerticalSize()
    {
        return listHeight;
    }

    public List<ListItem> getListItems()
    {
        return listItems;
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