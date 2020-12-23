package haraldr.ecs;

import haraldr.dockspace.uicomponents.ComponentPropertyList;
import haraldr.dockspace.uicomponents.UIVector3;
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
        componentPropertyList.addComponent("Position: ", new UIVector3(componentPropertyList.getParent().getTextBatch(), position, (x, y, z) ->
        {
            position.setX(x);
            position.setY(y);
            position.setZ(z);
        }));
        componentPropertyList.addComponent("Scale: ", new UIVector3(componentPropertyList.getParent().getTextBatch(), scale, true, (x, y, z) ->
        {
            scale.setX(x);
            scale.setY(y);
            scale.setZ(z);
        }));
        componentPropertyList.addComponent("Rotation: ", new UIVector3(componentPropertyList.getParent().getTextBatch(), rotation, 0.3f, (x, y, z) ->
        {
            rotation.setX(x);
            rotation.setY(y);
            rotation.setZ(z);
        }));
    }
}