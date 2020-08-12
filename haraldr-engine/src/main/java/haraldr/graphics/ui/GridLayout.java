package haraldr.graphics.ui;

import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.List;

public class GridLayout
{
    public static final GridLayout EMPTY = new GridLayout();

    private int columns, rows, columnSize, rowSize;
    private Vector2f paddingTopBottom, paddingLeftRight;
    private Vector2f marginTopLeft;

    private GridLayout()
    {
    }

    public GridLayout(int columns, int rows, int width, int height, Vector4f padding)
    {
        this.columns = columns;
        this.rows = rows;
        this.columnSize = width / columns;
        this.rowSize = height / rows;
        this.paddingTopBottom = new Vector2f(padding.getX(), padding.getY());
        this.paddingLeftRight = new Vector2f(padding.getZ(), padding.getW());
        marginTopLeft = new Vector2f();
    }

    public GridLayout(int columns, int rows, int width, int height, Vector4f padding, Vector2f margin)
    {
        this.columns = columns;
        this.rows = rows;
        this.columnSize = width / columns;
        this.rowSize = height / rows;
        paddingTopBottom = new Vector2f(padding.getX(), padding.getY());
        paddingLeftRight = new Vector2f(padding.getZ(), padding.getW());
        marginTopLeft = new Vector2f(margin.getX(), margin.getY());
        marginTopLeft = margin;
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

    public void setMarginTopLeft(Vector2f marginTopLeft)
    {
        this.marginTopLeft = marginTopLeft;
    }

    public void orderComponents(List<? extends UIComponent> components, Vector2f parentPosition)
    {
        for (int i = 0; i < components.size(); ++i)
        {
            UIComponent component = components.get(i);
            int verticalSize = (int) (rowSize - paddingTopBottom.getX() - paddingTopBottom.getY());
            int horizontalSize = (int) (columnSize - paddingLeftRight.getX() - paddingLeftRight.getY());
            component.setPosition(
                    (int) (i % columns * columnSize + paddingLeftRight.getX() + marginTopLeft.getY() + parentPosition.getX()),
                    (int) (i / columns % rows * rowSize + marginTopLeft.getX() + paddingTopBottom.getX() + parentPosition.getY())
            );
            component.setSize(horizontalSize, verticalSize);
        }
    }

    public int getMaxSlots()
    {
        return columns * rows;
    }
}
