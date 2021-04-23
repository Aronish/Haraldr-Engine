package editor;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import haraldr.debug.Logger;
import haraldr.ecs.Component;
import haraldr.ecs.ComponentSerializer;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.graphics.CubeMap;
import haraldr.graphics.lighting.DirectionalLight;
import haraldr.graphics.lighting.PointLight;
import haraldr.graphics.lighting.SceneLights;
import haraldr.graphics.lighting.Spotlight;
import haraldr.main.IOUtils;
import haraldr.math.Vector3f;
import haraldr.scene.Scene3D;
import haraldr.ui.FileDialogs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EditorScene extends Scene3D
{
    public EditorScene()
    {
        skyBox = CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr");
    }

    public void openScene()
    {
        YamlMapping sceneData = null;
        try
        {
            String path = FileDialogs.openFile("Open scene", "yml");
            if (path.isEmpty()) return;
            sceneData = Yaml.createYamlInput(IOUtils.readFile(path, IOUtils::resourceToString)).readYamlMapping();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if (sceneData == null)
        {
            Logger.error("Couldn't load scene!");
            return;
        }

        // Skybox
        skyBox = CubeMap.createEnvironmentMap(sceneData.string("environment_map"));

        // Entity registry
        EntityRegistry entityRegistry = new EntityRegistry();
        YamlMapping entities = sceneData.yamlMapping("entities");
        for (YamlNode entityId : entities.keys())
        {
            // Create an entity
            Entity entity = new Entity(Integer.parseInt(entityId.asScalar().toString().replaceAll("[^0-9]", "")));
            entityRegistry.addEntity(entity);

            // Get serialized components for this entity
            YamlMapping components = entities.yamlMapping(entityId);
            for (YamlNode componentName : components.keys())
            {
                Class<? extends Component> componentType = EntityRegistry.getRegisteredComponentByName(componentName.toString().replaceAll("[\r\n.-]", ""));
                if (componentType != null)
                {
                    YamlMapping componentData = components.yamlMapping(componentName);

                    Class<?>[] parameterTypes = componentType.getDeclaredConstructors()[0].getParameterTypes();
                    List<Object> arguments = new ArrayList<>();

                    // Collect constructor arguments based on parameters from introspection
                    int parameterIndex = 0;
                    for (YamlNode attributeName : componentData.keys())
                    {
                        switch (parameterTypes[parameterIndex].getSimpleName())
                        {
                            case "String" -> arguments.add(componentData.value(attributeName).asScalar().value());
                            case "float" -> arguments.add(Float.parseFloat(componentData.value(attributeName).asScalar().value()));
                            case "Vector3f" -> {
                                YamlSequence sequence = componentData.yamlSequence(attributeName);
                                Vector3f vector3f = new Vector3f(sequence.floatNumber(0), sequence.floatNumber(1), sequence.floatNumber(2));
                                arguments.add(vector3f);
                            }
                        }
                    }
                    // Instantiate the component
                    Component component = null;
                    try
                    {
                        component = componentType.getConstructor(parameterTypes).newInstance(arguments.toArray());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                    {
                        e.printStackTrace();
                    }
                    if (component != null) entityRegistry.addComponent(component, entity);
                }
            }
        }
        this.entityRegistry = entityRegistry;

        // Lights
        sceneLights.dispose();
        sceneLights = new SceneLights();
        YamlMapping lights = sceneData.yamlMapping("lights");
        // Directional lights
        YamlMapping directionalLights = lights.yamlMapping("directional_lights");
        for (YamlNode lightId : directionalLights.keys())
        {
            YamlMapping light = directionalLights.yamlMapping(lightId);
            YamlSequence position = light.yamlSequence("position");
            YamlSequence direction = light.yamlSequence("direction");
            YamlSequence color = light.yamlSequence("color");
            DirectionalLight directionalLight = new DirectionalLight(
                    new Vector3f(position.floatNumber(0), position.floatNumber(1), position.floatNumber(2)),
                    new Vector3f(direction.floatNumber(0), direction.floatNumber(1), direction.floatNumber(2)),
                    new Vector3f(color.floatNumber(0), color.floatNumber(1), color.floatNumber(2))
            );
            sceneLights.addLight(directionalLight);
        }
        // Point lights
        YamlMapping pointLights = lights.yamlMapping("point_lights");
        for (YamlNode lightId : pointLights.keys())
        {
            YamlMapping light = pointLights.yamlMapping(lightId);
            YamlSequence position = light.yamlSequence("position");
            YamlSequence color = light.yamlSequence("color");
            PointLight pointLight = new PointLight(
                    new Vector3f(position.floatNumber(0), position.floatNumber(1), position.floatNumber(2)),
                    new Vector3f(color.floatNumber(0), color.floatNumber(1), color.floatNumber(2)),
                    light.floatNumber("constant"),
                    light.floatNumber("linear"),
                    light.floatNumber("quadratic")
            );
            sceneLights.addLight(pointLight);
        }
        // Directional lights
        YamlMapping spotlights = lights.yamlMapping("spotlights");
        for (YamlNode lightId : spotlights.keys())
        {
            YamlMapping light = spotlights.yamlMapping(lightId);
            YamlSequence position = light.yamlSequence("position");
            YamlSequence direction = light.yamlSequence("direction");
            YamlSequence color = light.yamlSequence("color");
            Spotlight spotlight = new Spotlight(
                    new Vector3f(position.floatNumber(0), position.floatNumber(1), position.floatNumber(2)),
                    new Vector3f(direction.floatNumber(0), direction.floatNumber(1), direction.floatNumber(2)),
                    new Vector3f(color.floatNumber(0), color.floatNumber(1), color.floatNumber(2)),
                    light.floatNumber("inner_cutoff"),
                    light.floatNumber("outer_cutoff")
            );
            sceneLights.addLight(spotlight);
        }
    }

    public void saveScene()
    {
        YamlMappingBuilder scene = Yaml.createYamlMappingBuilder();
        // Entity registry
        YamlMappingBuilder entities = Yaml.createYamlMappingBuilder();
        for (Integer entityId : entityRegistry.getActiveEntities())
        {
            YamlMappingBuilder components = Yaml.createYamlMappingBuilder();
            for (Class<? extends Component> componentType : entityRegistry.getRegisteredComponentTypes())
            {
                Entity entity = new Entity(entityId);
                if (entityRegistry.hasComponent(componentType, entity))
                {
                    // Get component data
                    ComponentSerializer componentSerializer = new ComponentSerializer(Yaml.createYamlMappingBuilder());
                    Component component = entityRegistry.getComponent(componentType, entity);
                    component.acceptVisitor(componentSerializer);

                    components = components.add(componentType.getSimpleName(), componentSerializer.getComponentDataStore().build());
                }
            }
            entities = entities.add(Integer.toString(entityId), components.build());
        }
        scene = scene.add("entities", entities.build());

        // Directional lights
        List<DirectionalLight> sceneDirectionalLights = sceneLights.getDirectionalLights();
        YamlMappingBuilder directionalLights = Yaml.createYamlMappingBuilder();
        for (int i = 0; i < sceneDirectionalLights.size(); ++i)
        {
            YamlMappingBuilder lightData = Yaml.createYamlMappingBuilder();
            DirectionalLight light = sceneDirectionalLights.get(i);
            YamlSequence position = Yaml.createYamlSequenceBuilder()
                    .add(Float.toString(light.getPosition().getX()))
                    .add(Float.toString(light.getPosition().getY()))
                    .add(Float.toString(light.getPosition().getZ()))
                    .build();
            YamlSequence direction = Yaml.createYamlSequenceBuilder()
                    .add(Float.toString(light.getDirection().getX()))
                    .add(Float.toString(light.getDirection().getY()))
                    .add(Float.toString(light.getDirection().getZ()))
                    .build();
            YamlSequence color = Yaml.createYamlSequenceBuilder()
                    .add(Float.toString(light.getColor().getX()))
                    .add(Float.toString(light.getColor().getY()))
                    .add(Float.toString(light.getColor().getZ()))
                    .build();
            lightData = lightData.add("position", position);
            lightData = lightData.add("direction", direction);
            lightData = lightData.add("color", color);
            directionalLights = directionalLights.add(Integer.toString(i), lightData.build());
        }

        // Point lights
        List<PointLight> scenePointLights = sceneLights.getPointLights();
        YamlMappingBuilder pointLights = Yaml.createYamlMappingBuilder();
        for (int i = 0; i < scenePointLights.size(); ++i)
        {
            YamlMappingBuilder lightData = Yaml.createYamlMappingBuilder();
            PointLight light = scenePointLights.get(i);
            YamlSequence position = Yaml.createYamlSequenceBuilder()
                    .add(Float.toString(light.getPosition().getX()))
                    .add(Float.toString(light.getPosition().getY()))
                    .add(Float.toString(light.getPosition().getZ()))
                    .build();
            YamlSequence color = Yaml.createYamlSequenceBuilder()
                    .add(Float.toString(light.getColor().getX()))
                    .add(Float.toString(light.getColor().getY()))
                    .add(Float.toString(light.getColor().getZ()))
                    .build();
            lightData = lightData.add("position", position);
            lightData = lightData.add("color", color);
            lightData = lightData.add("constant", Float.toString(light.getConstant()));
            lightData = lightData.add("linear", Float.toString(light.getLinear()));
            lightData = lightData.add("quadratic", Float.toString(light.getQuadratic()));
            pointLights = pointLights.add(Integer.toString(i), lightData.build());
        }
        // Spotlights
        List<Spotlight> sceneSpotlights = sceneLights.getSpotlights();
        YamlMappingBuilder spotlights = Yaml.createYamlMappingBuilder();
        for (int i = 0; i < sceneSpotlights.size(); ++i)
        {
            YamlMappingBuilder lightData = Yaml.createYamlMappingBuilder();
            Spotlight light = sceneSpotlights.get(i);
            YamlSequence position = Yaml.createYamlSequenceBuilder()
                    .add(Float.toString(light.getPosition().getX()))
                    .add(Float.toString(light.getPosition().getY()))
                    .add(Float.toString(light.getPosition().getZ()))
                    .build();
            YamlSequence color = Yaml.createYamlSequenceBuilder()
                    .add(Float.toString(light.getColor().getX()))
                    .add(Float.toString(light.getColor().getY()))
                    .add(Float.toString(light.getColor().getZ()))
                    .build();
            lightData = lightData.add("position", position);
            lightData = lightData.add("color", color);
            lightData = lightData.add("inner_cutoff", Float.toString(light.getInnerCutOff()));
            lightData = lightData.add("outer_cutoff", Float.toString(light.getOuterCutOff()));
            spotlights = spotlights.add(Integer.toString(i), lightData.build());
        }

        YamlMappingBuilder lights = Yaml.createYamlMappingBuilder();
        lights = lights.add("directional_lights", directionalLights.build());
        lights = lights.add("point_lights", pointLights.build());
        lights = lights.add("spotlights", spotlights.build());

        scene = scene.add("lights", lights.build());

        scene = scene.add("environment_map", skyBox.getPath());

        // Save to file
        String savePath = FileDialogs.saveFile("Save scene", "yml");

        if (!savePath.isEmpty())
        {
            List<String> lines = new ArrayList<>();
            lines.add(scene.build().toString());
            try
            {
                Files.write(Paths.get(savePath), lines, StandardCharsets.UTF_8);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
