package haraldr.dockspace.uicomponents;

import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.ComponentVisitor;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.TagComponent;
import haraldr.ecs.TransformComponent;
import haraldr.main.IOUtils;
import haraldr.math.Quaternion;
import haraldr.math.Vector3f;
import jsonparser.JSONObject;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.util.List;

public class ComponentUIVisitor implements ComponentVisitor
{
    private ComponentPropertyList componentPropertyList;

    public void setComponentPropertyList(ComponentPropertyList componentPropertyList)
    {
        this.componentPropertyList = componentPropertyList;
    }

    @Override
    public void visit(BoundingSphereComponent boundingSphereComponent)
    {
        componentPropertyList.addComponent(
                "Radius: ",
                new UIInputField<>(
                        componentPropertyList.getParent().getTextBatch(),
                        new UIInputField.FloatValue(boundingSphereComponent.radius),
                        value -> boundingSphereComponent.radius = value.getValue()
                )
        );
    }

    @Override
    public void visit(ModelComponent modelComponent)
    {
        //TODO: When UIDropDownList becomes available, fix material property editing
        JSONObject updatedModelDefinition = modelComponent.model.getModelDefinition();
        JSONObject materialProperties = updatedModelDefinition.getJSONObject("material").getJSONObject("properties");

        UIInfoLabel meshPathLabel = new UIInfoLabel(componentPropertyList.getParent().getTextBatch(), updatedModelDefinition.getString("mesh"));
        componentPropertyList.addComponent("Mesh: ", meshPathLabel);
        componentPropertyList.addComponent(
                "Load Mesh: ",
                new UIButton(() ->
                {
                    String meshPath;
                    try (MemoryStack stack = MemoryStack.stackPush())
                    {
                        PointerBuffer filterPatterns = stack.mallocPointer(1);
                        filterPatterns.put(IOUtils.stringToByteBuffer("*.obj"));
                        meshPath = TinyFileDialogs.tinyfd_openFileDialog("Select mesh", "", filterPatterns, "", false);
                        if (meshPath == null) meshPath = "";
                    }

                    if (!meshPath.isBlank() && meshPath.endsWith(".obj"))
                    {
                        updatedModelDefinition.put("mesh", meshPath);
                        modelComponent.model.refresh(updatedModelDefinition);
                        meshPathLabel.setValue(meshPath.substring(meshPath.lastIndexOf("\\") + 1));
                    }
                })
        );

        componentPropertyList.addComponent(
                "Color: ",
                new UIVector3(
                        componentPropertyList.getParent().getTextBatch(),
                        new Vector3f(0f), new Vector3f(1f), new Vector3f(materialProperties.getJSONArray("color")), 0.01f,
                        (r, g, b) ->
                        {
                            materialProperties.put("color", List.of(r, g, b));
                            modelComponent.model.refresh(updatedModelDefinition);
                        }
                )
        );

        componentPropertyList.addComponent(
                "Metalness: ",
                new UISlider(materialProperties.getFloat("metalness"), (value ->
                {
                    materialProperties.put("metalness", value);
                    modelComponent.model.refresh(updatedModelDefinition);
                }))
        );

        componentPropertyList.addComponent(
                "Roughness: ",
                new UISlider(materialProperties.getFloat("roughness"), (value ->
                {
                    materialProperties.put("roughness", value);
                    modelComponent.model.refresh(updatedModelDefinition);
                }))
        );
    }

    @Override
    public void visit(TagComponent tagComponent)
    {
        componentPropertyList.addComponent(
                "Tag: ",
                new UIInputField<>(
                        componentPropertyList.getParent().getTextBatch(),
                        new UIInputField.StringValue(tagComponent.tag),
                        value -> tagComponent.tag = value.getValue()
                )
        );
    }

    @Override
    public void visit(TransformComponent transformComponent)
    {
        componentPropertyList.addComponent(
                "Position: ",
                new UIVector3(
                        componentPropertyList.getParent().getTextBatch(),
                        transformComponent.position,
                        (x, y, z) ->
                        {
                            transformComponent.position.setX(x);
                            transformComponent.position.setY(y);
                            transformComponent.position.setZ(z);
                        }
                )
        );
        componentPropertyList.addComponent(
                "Scale: ",
                new UIVector3Linkable(
                        componentPropertyList.getParent().getTextBatch(),
                        new Vector3f(0f), new Vector3f(Float.MAX_VALUE), transformComponent.scale, true,
                        (x, y, z) ->
                        {
                            transformComponent.scale.setX(x);
                            transformComponent.scale.setY(y);
                            transformComponent.scale.setZ(z);
                        }
                )
        );
        componentPropertyList.addComponent(
                //TODO: Gimbal lock still exists
                "Rotation: ",
                new UIVector3(
                        componentPropertyList.getParent().getTextBatch(),
                        Quaternion.toEulerAngles(transformComponent.rotationQuaternion), 0.3f,
                        (x, y, z) ->
                        {
                            transformComponent.rotation.setX(x);
                            transformComponent.rotation.setY(y);
                            transformComponent.rotation.setZ(z);
                            transformComponent.rotationQuaternion = Quaternion.fromEulerAngles(transformComponent.rotation);
                        }
                )
        );
    }
}
