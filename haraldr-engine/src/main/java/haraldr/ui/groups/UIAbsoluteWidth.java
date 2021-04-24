package haraldr.ui.groups;

import haraldr.ui.components.UIPositionable;

public class UIAbsoluteWidth extends UIConstraint
{
    private UIPositionable target;
    private float width;

    public UIAbsoluteWidth(UIPositionable target, float width)
    {
        this.target = target;
        this.width = width;
    }

    @Override
    public void update()
    {
        target.setSize(target.getSize().setX(width));
    }
}