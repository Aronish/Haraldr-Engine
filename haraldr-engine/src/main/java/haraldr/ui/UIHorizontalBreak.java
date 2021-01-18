package haraldr.ui;

public class UIHorizontalBreak extends UIComponent // Quite useless as a whole class
{
    private int height;

    public UIHorizontalBreak(int height)
    {
        this(null, height);
    }

    public UIHorizontalBreak(UIContainer parent, int height)
    {
        super(parent);
        this.height = height;
    }

    @Override
    public float getVerticalSize()
    {
        return height;
    }
}
