package haraldr.ui.groups;

import haraldr.math.Vector2f;
import haraldr.ui.components.UIPositionable;

public class UIRelativePosition extends UIConstraint
{
    private UIPositionable target;
    private UIPositionable anchor;
    private UISide side;
    private float offset;

    public UIRelativePosition(UIPositionable target, UIPositionable anchor, UISide side, float offset)
    {
        this.target = target;
        this.anchor = anchor;
        this.side = side;
        this.offset = offset;
    }

    @Override
    public void update()
    {
        switch (side)
        {
            case LEFT -> target.setPosition(new Vector2f(anchor.getPosition().getX() - target.getSize().getX() - offset, anchor.getPosition().getY()));
            case TOP -> target.setPosition(new Vector2f(anchor.getPosition().getX(), anchor.getPosition().getY() - target.getSize().getY() - offset));
            case RIGHT -> target.setPosition(new Vector2f(anchor.getPosition().getX() + anchor.getSize().getX() + offset, anchor.getPosition().getY()));
            case BOTTOM -> target.setPosition(new Vector2f(anchor.getPosition().getX(), anchor.getPosition().getY() + anchor.getSize().getY() + offset));
        }
    }
}