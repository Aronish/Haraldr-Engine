package haraldr.ui.groups;

import haraldr.math.Vector2f;
import haraldr.ui.components.UIPositionable;

public class UIOffset extends UIConstraint
{
    private UIPositionable target;
    private UIPositionable anchor;
    private Vector2f offset;

    public UIOffset(UIPositionable target, UIPositionable anchor, Vector2f offset)
    {
        this.target = target;
        this.anchor = anchor;
        this.offset = offset;
    }

    @Override
    public void update()
    {
        target.setPosition(Vector2f.add(anchor.getPosition(), offset));
    }
}
