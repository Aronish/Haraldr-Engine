package editor;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import haraldr.debug.Logger;
import haraldr.dockspace.DockPosition;
import haraldr.dockspace.Dockspace;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Component;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.DynamicScene;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.lighting.DirectionalLight;
import haraldr.graphics.lighting.PointLight;
import haraldr.graphics.lighting.SceneLights;
import haraldr.graphics.lighting.Spotlight;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.main.Application;
import haraldr.main.IOUtils;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics3D;
import haraldr.scene.Camera;
import haraldr.scene.OrbitalCamera;
import haraldr.ui.FileDialogs;
import haraldr.ui.UIHeader;
import haraldr.ui.UILayerStack;
import haraldr.ui.components.ListData;
import haraldr.ui.components.UILayerable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EditorApplication extends Application
{
    private UILayerStack mainLayerStack = new UILayerStack();
    private Dockspace dockSpace;

    // Editor panels
    private ProjectManagerPanel projectManagerPanel;
    private EntityHierarchyPanel entityHierarchyPanel;
    private Scene3DPanel scene3DPanel;
    private PropertiesPanel propertiesPanel;

    // Scene
    private Camera editorCamera;
    private DynamicScene scene;
    private Entity selected = Entity.INVALID;

    public EditorApplication()
    {
        super(new Window.WindowProperties(
                "Haraldr Editor", 1280, 720,
                ProgramArguments.getIntOrDefault("MSAA", 0),
                false, false, true, false)
        );
    }

    private void selectEntity(Entity entity)
    {
        if (!selected.equals(Entity.INVALID))
        {
            ModelComponent lastModel = scene.getEntityRegistry().getComponent(ModelComponent.class, selected);
            lastModel.model.setOutlined(false);
        }
        selected = entity;
        propertiesPanel.clear();
        if (!selected.equals(Entity.INVALID))
        {
            ModelComponent model = scene.getEntityRegistry().getComponent(ModelComponent.class, selected);
            model.model.setOutlined(true);
            propertiesPanel.populateWithEntity(selected, scene.getEntityRegistry());
        }
        entityHierarchyPanel.refreshEntityList(scene.getEntityRegistry());
    }

    private Entity selectEntityWithMouse(Vector2f mousePoint, Vector2f windowSize, Entity lastSelected, EntityRegistry registry)
    {
        Vector3f ray = Physics3D.castRayFromMouse(mousePoint, windowSize, editorCamera.getViewMatrix(), editorCamera.getProjectionMatrix());

        Entity selected;
        if (!lastSelected.equals(Entity.INVALID))
        {
            ModelComponent lastModel = registry.getComponent(ModelComponent.class, lastSelected);
            lastModel.model.setOutlined(false);
        }

        selected = registry.view(BoundingSphereComponent.class).find(((transform, bsphere) ->
                Physics3D.rayIntersectsSphere(editorCamera.getPosition(), ray, transform.position, bsphere.radius)), registry);

        if (!selected.equals(Entity.INVALID))
        {
            ModelComponent model = registry.getComponent(ModelComponent.class, selected);
            model.model.setOutlined(true);
        }
        return selected;
    }

    private void openScene()
    {
        YamlMapping sceneData = null;
        try
        {
            sceneData = Yaml.createYamlInput(IOUtils.readFile(FileDialogs.openFile("Open scene", "yml"), IOUtils::resourceToString)).readYamlMapping();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if (sceneData == null)
        {
            Logger.error("Couldn't load scene!");
            return;
        }

        // Entity registry
        EntityRegistry sceneEntityRegistry = new EntityRegistry();
        YamlMapping entities = sceneData.yamlMapping("entities");
        for (YamlNode entityId : entities.keys())
        {
            // Create an entity
            Entity entity = new Entity(Integer.parseInt(entityId.asScalar().toString().replaceAll("[^0-9]", "")));
            sceneEntityRegistry.addEntity(entity);

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
                    if (component != null) sceneEntityRegistry.addComponent(component, entity);
                }
            }
        }

        SceneLights sceneLights = new SceneLights();
        // Lights
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

        scene.setEntityRegistry(sceneEntityRegistry);
        scene.setSceneLights(sceneLights);
        entityHierarchyPanel.refreshEntityList(this.scene.getEntityRegistry());
    }

    private void saveScene()
    {
        YamlMappingBuilder scene = Yaml.createYamlMappingBuilder();
        // Entity registry
        YamlMappingBuilder entities = Yaml.createYamlMappingBuilder();
        for (Integer entityId : this.scene.getEntityRegistry().getActiveEntities())
        {
            YamlMappingBuilder components = Yaml.createYamlMappingBuilder();
            for (Class<? extends Component> componentType : this.scene.getEntityRegistry().getRegisteredComponentTypes())
            {
                Entity entity = new Entity(entityId);
                if (this.scene.getEntityRegistry().hasComponent(componentType, entity))
                {
                    // Get component data
                    ComponentSerializer componentSerializer = new ComponentSerializer(Yaml.createYamlMappingBuilder());
                    Component component = this.scene.getEntityRegistry().getComponent(componentType, entity);
                    component.acceptVisitor(componentSerializer);

                    components = components.add(componentType.getSimpleName(), componentSerializer.getComponentDataStore().build());
                }
            }
            entities = entities.add(Integer.toString(entityId), components.build());
        }
        scene = scene.add("entities", entities.build());

        // Lights
        SceneLights sceneLights = this.scene.getSceneLights();
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

        scene = scene.add("environment_map", this.scene.getEnvironmentMapPath());

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

    @Override
    protected void clientInit(Window window)
    {
        // Window header
        UIHeader windowHeader = new UIHeader(
                mainLayerStack, 0,
                new Vector2f(),
                new Vector2f(window.getWidth(), mainLayerStack.getLayer(0).getTextBatch().getFont().getSize()),
                new Vector4f(0.4f, 0.4f, 0.4f, 1f)
        );
        windowHeader.addMenuButton(
                "File",
                new ListData("Open", this::openScene),
                new ListData("Save", this::saveScene),
                new ListData("Exit", this::stop)
        );

        // Dockspace
        dockSpace = new Dockspace(
                mainLayerStack, 0,
                new Vector2f(0f, windowHeader.getSize().getY()),
                new Vector2f(window.getWidth(), window.getHeight() - windowHeader.getSize().getY())
        );

        mainLayerStack.draw();

        // Scene
        scene = new DynamicScene();

        // Project Manager Panel
        //dockSpace.addPanel(projectManagerPanel = new ProjectManagerPanel(new Vector2f(window.getWidth() / 2f - 300f, window.getHeight() / 2f - 200f), new Vector2f(600f, 400f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Manage Projects"));

        // Scene Panel
        dockSpace.addPanel(scene3DPanel = new Scene3DPanel(new Vector2f(100f), new Vector2f(200f), "Scene"));
        editorCamera = new OrbitalCamera(scene3DPanel.getSize().getX(), scene3DPanel.getSize().getY());
        scene3DPanel.setPanelDimensionChangeAction((position, size) ->
        {
            scene3DPanel.getSceneTexture().setPosition(position);
            scene3DPanel.getSceneTexture().setSize(size.getX(), size.getY());
            editorCamera.setAspectRatio(size.getX() / size.getY());
        });
        dockSpace.dockPanel(scene3DPanel, DockPosition.RIGHT);
        dockSpace.resizePanel(scene3DPanel, 300f);

        // Properties Panel
        dockSpace.addPanel(propertiesPanel = new PropertiesPanel(new Vector2f(300f), new Vector2f(200f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Properties"));
        dockSpace.dockPanel(propertiesPanel, DockPosition.BOTTOM);

        // Hierarchy Panel
        dockSpace.addPanel(entityHierarchyPanel = new EntityHierarchyPanel(new Vector2f(500f), new Vector2f(200f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Hierarchy", this::selectEntity));
        entityHierarchyPanel.refreshEntityList(scene.getEntityRegistry());
        dockSpace.dockPanel(entityHierarchyPanel, DockPosition.CENTER);
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        UILayerable.UIEventResult uiEventResult = mainLayerStack.onEvent(event, window);
        if (uiEventResult.requiresRedraw()) mainLayerStack.draw();

        if (!uiEventResult.consumed())
        {
            editorCamera.onEvent(event, window, scene3DPanel.isHovered());
        }

        if (scene3DPanel.isHovered() && scene3DPanel.isContentPressed() && !scene3DPanel.isHeaderPressed())
        {
            // Select an entity
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                Entity selected = selectEntityWithMouse(
                        new Vector2f(mousePressedEvent.xPos - scene3DPanel.getSceneTexture().getPosition().getX(), mousePressedEvent.yPos - scene3DPanel.getSceneTexture().getPosition().getY()),
                        scene3DPanel.getSceneTexture().getSize(),
                        this.selected, scene.getEntityRegistry());
                selectEntity(selected);
            }
        }

        if (Input.wasKeyPressed(event, KeyboardKey.KEY_F)) window.toggleFullscreen();

        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            editorCamera.setAspectRatio(scene3DPanel.getSceneTexture().getSize().getX() / scene3DPanel.getSceneTexture().getSize().getY());
        }
    }

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {
        editorCamera.onUpdate(deltaTime, window);
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer.enableDepthTest();
        Renderer3D.renderSceneToTexture(window, editorCamera, scene, scene3DPanel.getSceneTexture());

        Renderer.disableDepthTest();
        mainLayerStack.render();
    }

    @Override
    public void clientDispose()
    {
        dockSpace.dispose();
        scene.dispose();
    }
}