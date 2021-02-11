package editor;

import haraldr.debug.Logger;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.ComponentVisitor;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.TagComponent;
import haraldr.ecs.TransformComponent;
import haraldr.main.IOUtils;
import haraldr.math.Quaternion;
import haraldr.math.Vector3f;
import haraldr.ui.FileDialogs;
import haraldr.ui.components.UIButton;
import haraldr.ui.components.UIDropDownMenu;
import haraldr.ui.components.UIInfoLabel;
import haraldr.ui.components.UIInputField;
import haraldr.ui.components.UIVector3;
import haraldr.ui.components.UIVector3Linkable;
import jsonparser.JSONArray;
import jsonparser.JSONObject;

public class ComponentUIVisitor implements ComponentVisitor
{
    private ECSComponentGroup ecsComponentGroup;

    public void setComponentPropertyList(ECSComponentGroup ECSComponentGroup)
    {
        this.ecsComponentGroup = ECSComponentGroup;
    }

    @Override
    public void visit(BoundingSphereComponent boundingSphereComponent)
    {
        ecsComponentGroup.getComponentList().addComponent(
                "Radius: ",
                new UIInputField<>(
                        ecsComponentGroup,
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

        UIInfoLabel meshPathLabel = new UIInfoLabel(ecsComponentGroup, 0, updatedModelDefinition.getString("mesh"));
        ecsComponentGroup.getComponentList().addComponent("Mesh: ", meshPathLabel);
        ecsComponentGroup.getComponentList().addComponent(
                "Load Mesh: ",
                new UIButton(ecsComponentGroup, 0, () ->
                {
                    String meshPath = FileDialogs.openFile("Select mesh", "obj");

                    if (!meshPath.isBlank() && meshPath.endsWith(".obj"))
                    {
                        updatedModelDefinition.put("mesh", meshPath);
                        modelComponent.model.refresh(updatedModelDefinition);
                        meshPathLabel.setValue(meshPath.substring(meshPath.lastIndexOf("\\") + 1));
                    }
                })
        );

        // Material types
        UIDropDownMenu uiDropDownMenu = new UIDropDownMenu(ecsComponentGroup, 0);
        JSONArray materialTypes = new JSONObject(IOUtils.readResource("default_models/material_specification.json", IOUtils::resourceToString)).names();
        for (Object materialType : materialTypes.toList())
        {
            uiDropDownMenu.addMenuItem(((String)materialType).charAt(0) + ((String)materialType).substring(1).toLowerCase(), Logger::info);
            // TODO: Lambda for selecting and changing type here
        }
        ecsComponentGroup.getComponentList().addComponent("Material Type: ", uiDropDownMenu);
        // TODO: Rework once serialization exists

        //uiComponentList.getComponentList().addComponent(
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

        //uiComponentList.getComponentList().addComponent(
        //        "Metalness: ",
        //        new UISlider(materialProperties.getFloat("metalness"), (value ->
        //        {
        //            materialProperties.put("metalness", value);
        //            modelComponent.model.refresh(updatedModelDefinition);
        //        }))
        //);

        //uiComponentList.getComponentList().addComponent(
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
        ecsComponentGroup.getComponentList().addComponent(
                "Tag: ",
                new UIInputField<>(
                        ecsComponentGroup,
                        0, new UIInputField.StringValue(tagComponent.tag),
                        value -> tagComponent.tag = value.getValue()
                )
        );
    }

    @Override
    public void visit(TransformComponent transformComponent)
    {
        ecsComponentGroup.getComponentList().addComponent(
                "Position: ",
                new UIVector3(
                        ecsComponentGroup,
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
        ecsComponentGroup.getComponentList().addComponent(
                "Scale: ",
                new UIVector3Linkable(
                        ecsComponentGroup,
                        0, new Vector3f(0f), new Vector3f(Float.MAX_VALUE), transformComponent.scale, linkedScale,
                        (x, y, z) ->
                        {
                            transformComponent.scale.setX(x);
                            transformComponent.scale.setY(y);
                            transformComponent.scale.setZ(z);
                        }
                )
        );
        ecsComponentGroup.getComponentList().addComponent(
                "Rotation: ",
                new UIVector3(
                        ecsComponentGroup,
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
