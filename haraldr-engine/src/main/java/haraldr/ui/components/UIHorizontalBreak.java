package haraldr.ui.components;

public class UIHorizontalBreak extends UIComponent // Quite useless as a whole class
{
    private int height;

    public UIHorizontalBreak(UIContainer parent, int layerIndex, int height)
    {
        super(parent, layerIndex);
        this.height = height;
    }

    @Override
    public float getVerticalSize()
    {
        return height;
    }
}
