package haraldr.ui.groups;

import haraldr.ui.components.UIPositionable;

public class UIRelativeWidth extends UIConstraint
{
    private UIPositionable target;
    private UIPositionable anchor;
    private float relativeWidth;

    public UIRelativeWidth(UIPositionable target, UIPositionable anchor, float relativeWidth)
    {
        this.target = target;
        this.anchor = anchor;
        this.relativeWidth = relativeWidth;
    }

    @Override
    public void update()
    {
        target.setSize(target.getSize().setX(relativeWidth * anchor.getSize().getX()));
    }
}
