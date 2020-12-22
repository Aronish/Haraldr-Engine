package haraldr.ecs;

import haraldr.dockspace.uicomponents.ComponentPropertyList;
import haraldr.dockspace.uicomponents.UIInputField;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.Contract;

public class TransformComponent implements Component
{
    public Vector3f position;
    public Vector3f scale;
    public Vector3f rotation;

    @Contract(pure = true)
    public TransformComponent(Vector3f position, Vector3f scale, Vector3f rotation)
    {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    @Override
    public void extractComponentProperties(ComponentPropertyList componentPropertyList)
    {
        //TODO: Add VectorInputField
        componentPropertyList.addComponent("Pos X: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(position.getX()), value -> position.setX(value.getValue())));
        componentPropertyList.addComponent("Pos Y: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(position.getY()), value -> position.setY(value.getValue())));
        componentPropertyList.addComponent("Pos Z: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(position.getZ()), value -> position.setZ(value.getValue())));

        componentPropertyList.addComponent("Scale X: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(scale.getX()), value -> scale.setX(value.getValue())));
        componentPropertyList.addComponent("Scale Y: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(scale.getY()), value -> scale.setY(value.getValue())));
        componentPropertyList.addComponent("Scale Z: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(scale.getZ()), value -> scale.setZ(value.getValue())));

        componentPropertyList.addComponent("Rot X: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(rotation.getX()), value -> rotation.setX(value.getValue())));
        componentPropertyList.addComponent("Rot Y: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(rotation.getY()), value -> rotation.setY(value.getValue())));
        componentPropertyList.addComponent("Rot Z: ", new UIInputField<>(componentPropertyList.getParent().getTextBatch(), new UIInputField.FloatValue(rotation.getZ()), value -> rotation.setZ(value.getValue())));
    }
}