package haraldr.ui.groups;

import haraldr.math.Vector2f;
import haraldr.ui.components.UIPositionable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIConstraintGroup extends UIComponentGroup<ConstraintInsertData>
{
    private Map<UIPositionable, List<UIConstraint>> uiComponents = new HashMap<>(); // TODO: Separate position and size constraints

    @Override
    public void addComponent(ConstraintInsertData constraintInsertData)
    {
        uiComponents.put(constraintInsertData.component(), Arrays.stream(constraintInsertData.constraint()).toList());
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        for (List<UIConstraint> constraints : uiComponents.values())
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
        for (List<UIConstraint> constraints : uiComponents.values())
        {
            for (UIConstraint constraint : constraints)
            {
                constraint.update();
            }
        }
    }
}