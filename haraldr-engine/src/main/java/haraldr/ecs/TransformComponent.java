package haraldr.ecs;

import haraldr.dockspace.uicomponents.ComponentPropertyList;
import haraldr.dockspace.uicomponents.UnlabeledInputField;
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
        componentPropertyList.addComponent("Pos X: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(position.getX()), value -> position.setX(value.getValue())));
        componentPropertyList.addComponent("Pos Y: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(position.getY()), value -> position.setY(value.getValue())));
        componentPropertyList.addComponent("Pos Z: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(position.getZ()), value -> position.setZ(value.getValue())));

        componentPropertyList.addComponent("Scale X: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(scale.getX()), value -> scale.setX(value.getValue())));
        componentPropertyList.addComponent("Scale Y: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(scale.getY()), value -> scale.setY(value.getValue())));
        componentPropertyList.addComponent("Scale Z: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(scale.getZ()), value -> scale.setZ(value.getValue())));

        componentPropertyList.addComponent("Rot X: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(rotation.getX()), value -> rotation.setX(value.getValue())));
        componentPropertyList.addComponent("Rot Y: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(rotation.getY()), value -> rotation.setY(value.getValue())));
        componentPropertyList.addComponent("Rot Z: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.FloatValue(rotation.getZ()), value -> rotation.setZ(value.getValue())));
    }
}