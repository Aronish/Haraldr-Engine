package haraldr.ecs;

import haraldr.dockspace.uicomponents.ComponentPropertyList;
import haraldr.dockspace.uicomponents.UIInputField;
import org.jetbrains.annotations.Contract;

public class BoundingSphereComponent implements Component
{
    public float radius;

    @Contract(pure = true)
    public BoundingSphereComponent(float radius)
    {
        this.radius = radius;
    }

    @Override
    public void extractComponentProperties(ComponentPropertyList componentPropertyList)
    {
        componentPropertyList.addComponent("Radius: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(radius), value -> radius = value.getValue()));
    }
}