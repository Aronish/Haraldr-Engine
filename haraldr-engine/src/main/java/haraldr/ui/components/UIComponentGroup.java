package haraldr.ui.components;

public abstract class UIComponentGroup extends UIComponent
{
    public UIComponentGroup(UIContainer parent, int layerIndex)
    {
        super(parent, layerIndex);
    }

    public abstract void update();
}