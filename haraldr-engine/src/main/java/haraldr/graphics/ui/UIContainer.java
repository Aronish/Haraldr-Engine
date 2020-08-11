package haraldr.graphics.ui;

import haraldr.math.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class UIContainer
{
    private int columns, rows; //Later GridLayout
    private int columnSize, rowSize;
    private Map<UIComponent, Vector2f> components = new HashMap<>();

    public UIContainer(int columns, int rows, int width, int height)
    {
        this.columns = columns;
        this.rows = rows;
        resize(width, height);
    }

    public void resize(int width, int height)
    {
        columnSize = width / columns;
        rowSize = height / rows;
        for (Map.Entry<UIComponent, Vector2f> entry : components.entrySet())
        {
            entry.getKey().setPosition(columnSize * entry.getValue().getX(), rowSize * entry.getValue().getY());
            entry.getKey().setSize(columnSize, rowSize);
        }
    }

    public void addComponent(UIComponent component, int column, int row)
    {
        component.setPosition(columnSize * column, rowSize * row);
        component.setSize(columnSize, rowSize);
        components.put(component, new Vector2f(column, row));
    }

    public void render()
    {
        for (UIComponent component : components.keySet())
        {
            component.renderAll(new Vector2f());
        }
    }
}
