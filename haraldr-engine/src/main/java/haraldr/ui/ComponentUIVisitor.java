package haraldr.ui;

import haraldr.debug.Logger;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.ComponentVisitor;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.TagComponent;
import haraldr.ecs.TransformComponent;
import haraldr.main.IOUtils;
import haraldr.math.Quaternion;
import haraldr.math.Vector3f;
import jsonparser.JSONArray;
import jsonparser.JSONObject;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class ComponentUIVisitor implements ComponentVisitor // TODO: Wrap JSONParser and TinyFileDialogs and move to editor.
{
    private UIComponentList uiComponentList;

    public void setComponentPropertyList(UIComponentList UIComponentList)
    {
        this.uiComponentList = UIComponentList;
    }

    @Override
    public void visit(BoundingSphereComponent boundingSphereComponent)
    {
        uiComponentList.addComponent(
                "Radius: ",
                new UIInputField<>(
                        uiComponentList,
                        0, new UIInputField.FloatValue(boundingSphereComponent.radius),
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

        UIInfoLabel meshPathLabel = new UIInfoLabel(uiComponentList, 0, updatedModelDefinition.getString("mesh"));
        uiComponentList.addComponent("Mesh: ", meshPathLabel);
        uiComponentList.addComponent(
                "Load Mesh: ",
                new UIButton(uiComponentList, 0, () ->
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

        // Material types
        UIDropDownMenu uiDropDownMenu = new UIDropDownMenu(uiComponentList, 0);
        JSONArray materialTypes = new JSONObject(IOUtils.readResource("default_models/material_specification.json", IOUtils::resourceToString)).names();
        for (Object materialType : materialTypes.toList())
        {
            uiDropDownMenu.addMenuItem(((String)materialType).charAt(0) + ((String)materialType).substring(1).toLowerCase(), Logger::info);
            // TODO: Lambda for selecting and changing type here
        }
        uiComponentList.addComponent("Material Type: ", uiDropDownMenu);
        // TODO: Rework once serialization exists

        //uiComponentList.addComponent(
        //        "Color: ",
        //        new UIVector3(
        //                uiComponentList.getParent().getTextBatch(),
        //                new Vector3f(0f), new Vector3f(1f), new Vector3f(materialProperties.getJSONArray("color")), 0.01f,
        //                (r, g, b) ->
        //                {
        //                    materialProperties.put("color", List.of(r, g, b));
        //                    modelComponent.model.refresh(updatedModelDefinition);
        //                }
        //        )
        //);

        //uiComponentList.addComponent(
        //        "Metalness: ",
        //        new UISlider(materialProperties.getFloat("metalness"), (value ->
        //        {
        //            materialProperties.put("metalness", value);
        //            modelComponent.model.refresh(updatedModelDefinition);
        //        }))
        //);

        //uiComponentList.addComponent(
        //        "Roughness: ",
        //        new UISlider(materialProperties.getFloat("roughness"), (value ->
        //        {
        //            materialProperties.put("roughness", value);
        //            modelComponent.model.refresh(updatedModelDefinition);
        //        }))
        //);
    }

    @Override
    public void visit(TagComponent tagComponent)
    {
        uiComponentList.addComponent(
                "Tag: ",
                new UIInputField<>(
                        uiComponentList,
                        0, new UIInputField.StringValue(tagComponent.tag),
                        value -> tagComponent.tag = value.getValue()
                )
        );
    }

    @Override
    public void visit(TransformComponent transformComponent)
    {
        uiComponentList.addComponent(
                "Position: ",
                new UIVector3(
                        uiComponentList,
                        0, transformComponent.position,
                        (x, y, z) ->
                        {
                            transformComponent.position.setX(x);
                            transformComponent.position.setY(y);
                            transformComponent.position.setZ(z);
                        }
                )
        );
        boolean linkedScale = transformComponent.scale.getX() == transformComponent.scale.getY() && transformComponent.scale.getX() == transformComponent.scale.getZ();
        uiComponentList.addComponent(
                "Scale: ",
                new UIVector3Linkable(
                        uiComponentList,
                        0, new Vector3f(0f), new Vector3f(Float.MAX_VALUE), transformComponent.scale, linkedScale,
                        (x, y, z) ->
                        {
                            transformComponent.scale.setX(x);
                            transformComponent.scale.setY(y);
                            transformComponent.scale.setZ(z);
                        }
                )
        );
        uiComponentList.addComponent(
                "Rotation: ",
                new UIVector3(
                        uiComponentList,
                        0, transformComponent.rotation, 0.3f,
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
