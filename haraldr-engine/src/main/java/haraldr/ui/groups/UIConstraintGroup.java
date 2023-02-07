package haraldr.ui.groups;

import haraldr.math.Vector2f;
import haraldr.ui.components.UIPositionable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIConstraintGroup extends UIComponentGroup<ConstraintInsertData>
{
    private Map<UIPositionable, List<UIConstraint>> children = new HashMap<>();

    @Override
    public void addComponent(ConstraintInsertData constraintInsertData)
    {
        children.put(constraintInsertData.component(), Arrays.stream(constraintInsertData.constraint()).toList());
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        for (List<UIConstraint> constraints : children.values())
        {
            for (UIConstraint constraint : constraints)
            {
                constraint.update();
            }
        }
    }

    @Override
    public void setSize(Vector2f size)
    {
        super.setSize(size);
        for (List<UIConstraint> constraints : children.values())
        {
            for (UIConstraint constraint : constraints)
            {
                constraint.update();
            }
        }
    }
}