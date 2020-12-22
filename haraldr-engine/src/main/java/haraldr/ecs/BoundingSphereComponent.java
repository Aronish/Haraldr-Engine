package haraldr.ecs;

import haraldr.dockspace.uicomponents.ComponentPropertyList;
import haraldr.dockspace.uicomponents.UnlabeledInputField;
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
        componentPropertyList.addComponent("Radius: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(radius), value -> radius = value.getValue()));
    }
}