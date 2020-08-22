package haraldr.graphics.ui;

import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.List;

public class GridLayout
{
    public static final GridLayout EMPTY = new GridLayout();

    private int columns;
    private int rows;
    private int columnSize;
    private int rowSize;
    private Vector2f paddingTopBottom;
    private Vector2f paddingLeftRight;
    private Vector2f marginLeftTop = new Vector2f();

    private GridLayout()
    {
    }

    public GridLayout(int columns, int rows, Vector4f padding)
    {
        this.columns = columns;
        this.rows = rows;
    }

    public GridLayout(int columns, int rows, Vector4f padding, Vector2f marginLeftTop)
    {
        this.columns = columns;
        this.rows = rows;
        this.paddingTopBottom = new Vector2f(padding.getX(), padding.getY());
        this.paddingLeftRight = new Vector2f(padding.getZ(), padding.getW());
        this.marginLeftTop = marginLeftTop;
    }

    public GridLayout(int columns, int rows, int width, int height, Vector4f padding)
    {
        this.columns = columns;
        this.rows = rows;
        this.columnSize = width / columns;
        this.rowSize = height / rows;
        this.paddingTopBottom = new Vector2f(padding.getX(), padding.getY());
        this.paddingLeftRight = new Vector2f(padding.getZ(), padding.getW());
    }

    public GridLayout(int columns, int rows, int width, int height, Vector4f padding, Vector2f margin)
    {
        this.columns = columns;
        this.rows = rows;
        this.columnSize = width / columns;
        this.rowSize = height / rows;
        paddingTopBottom = new Vector2f(padding.getX(), padding.getY());
        paddingLeftRight = new Vector2f(padding.getZ(), padding.getW());
        marginLeftTop = margin;
    }

    public void setSize(int width, int height)
    {
        if (!(rows == 0 || columns == 0))
        {
            this.columnSize = width / columns;
            this.rowSize = height / rows;
        }
    }

    public void setPadding(Vector4f padding)
    {
        paddingTopBottom = new Vector2f(padding.getX(), padding.getY());
        paddingLeftRight = new Vector2f(padding.getZ(), padding.getW());
    }

    public void setMarginLeftTop(Vector2f marginLeftTop)
    {
        this.marginLeftTop = marginLeftTop;
    }

    public int orderComponents(List<? extends UIComponent> components, Vector2f parentPosition, int startIndex)
    {
        Vector2f adjustedPosition = Vector2f.add(parentPosition, marginLeftTop);
        for (int i = 0; i < components.size(); ++i)
        {
            int index = startIndex + i;
            UIComponent component = components.get(i);
            int horizontalSize = (int) (columnSize - paddingLeftRight.getX() - paddingLeftRight.getY());
            int verticalSize = (int) (rowSize - paddingTopBottom.getX() - paddingTopBottom.getY());
            component.setPosition(
                    (int) (index % columns * columnSize + paddingLeftRight.getX() + adjustedPosition.getX()),
                    (int) (index / columns % rows * rowSize + paddingTopBottom.getX() + adjustedPosition.getY())
            );
            component.setSize(horizontalSize, verticalSize);
        }
        return components.size();
    }

    public int getMaxSlots()
    {
        return columns * rows;
    }

    public Vector2f getSize()
    {
        return new Vector2f(columnSize, rowSize);
    }
}
