package editor;

import haraldr.dockspace.DockPosition;
import haraldr.dockspace.Dockspace;
import haraldr.dockspace.uicomponents.InputField;
import haraldr.dockspace.uicomponents.UnlabeledCheckbox;
import haraldr.dockspace.uicomponents.UnlabeledInputField;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.SerializeField;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer3D;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.main.Application;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics3D;
import haraldr.scene.Camera;
import haraldr.scene.OrbitalCamera;
import haraldr.scene.Scene3D;

import java.lang.reflect.Field;

public class EditorApplication extends Application
{
    private Camera editorCamera;
    private Scene3D scene;
    private Entity selected = Entity.INVALID;

    private Dockspace dockSpace;
    private EntityHierarchyPanel entityHierarchyPanel;
    private Scene3DPanel scene3DPanel;

    private PropertiesPanel propertiesPanel;

    public EditorApplication()
    {
        super(new Window.WindowProperties(
                "Haraldr Editor", 1280, 720,
                ProgramArguments.getIntOrDefault("MSAA", 0),
                false, false, true, false)
        );
    }

    @Override
    protected void clientInit(Window window)
    {
        scene = new EditorTestScene();
        scene.onActivate();

        dockSpace = new Dockspace(new Vector2f(), new Vector2f(window.getWidth(), window.getHeight()));

        // Scene Panel
        dockSpace.addPanel(scene3DPanel = new Scene3DPanel(new Vector2f(700f, 30f), new Vector2f(200f, 200f), "Scene"));
        editorCamera = new OrbitalCamera(scene3DPanel.getSize().getX(), scene3DPanel.getSize().getY());
        scene3DPanel.setPanelResizeAction((position, size) ->
        {
            scene3DPanel.getSceneTexture().setPosition(position);
            scene3DPanel.getSceneTexture().setSize(size.getX(), size.getY());
            editorCamera.setAspectRatio(size.getX() / size.getY());
        });
        dockSpace.dockPanel(scene3DPanel, DockPosition.RIGHT);
        dockSpace.resizePanel(scene3DPanel, 300f);

        // Properties Panel
        dockSpace.addPanel(propertiesPanel = new PropertiesPanel(new Vector2f(), new Vector2f(), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Properties"));
        dockSpace.dockPanel(propertiesPanel, DockPosition.BOTTOM);

        // Hierarchy Panel
        dockSpace.addPanel(entityHierarchyPanel = new EntityHierarchyPanel(new Vector2f(200f), new Vector2f(200f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Hierarchy"));
        entityHierarchyPanel.refreshEntityList(scene.getRegistry());
        entityHierarchyPanel.setEntitySelectedAction(selectedEntity ->
        {
            if (!selected.equals(Entity.INVALID))
            {
                ModelComponent lastModel = scene.getRegistry().getComponent(ModelComponent.class, selected);
                lastModel.model.setOutlined(false);
            }
            selected = selectedEntity;
            propertiesPanel.clear();
            if (!selected.equals(Entity.INVALID))
            {
                ModelComponent model = scene.getRegistry().getComponent(ModelComponent.class, selected);
                model.model.setOutlined(true);
                try
                {
                    populatePropertiesPanel();
                } catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        });
        dockSpace.dockPanel(entityHierarchyPanel, DockPosition.CENTER);
    }

    private void populatePropertiesPanel() throws IllegalAccessException
    {
        for (Class<?> componentType : scene.getRegistry().getRegisteredComponentTypes())
        {
            if (scene.getRegistry().hasComponent(componentType, selected))
            {
                UIComponentList uiComponentList = new UIComponentList(componentType.getSimpleName(), propertiesPanel);
                for (Field field : componentType.getDeclaredFields())
                {
                    if (field.isAnnotationPresent(SerializeField.class))
                    {
                        field.setAccessible(true);
                        uiComponentList.addComponent(field.getName(), switch (field.getType().getSimpleName())
                        {
                            case "String" -> new UnlabeledInputField(propertiesPanel.getTextBatch(), (String)field.get(scene.getRegistry().getComponent(componentType, selected)), (((addedChar, fullText) ->
                            {
                                try
                                {
                                    field.set(scene.getRegistry().getComponent(componentType, selected), fullText);
                                } catch (IllegalAccessException e)
                                {
                                    e.printStackTrace();
                                }
                            })));
                            case "float" -> new UnlabeledInputField(propertiesPanel.getTextBatch(), (String)field.get(scene.getRegistry().getComponent(componentType, selected)), InputField.InputType.NUMBERS, (((addedChar, fullText) ->
                            {
                                try
                                {
                                    field.set(scene.getRegistry().getComponent(componentType, selected), fullText);
                                } catch (IllegalAccessException e)
                                {
                                    e.printStackTrace();
                                }
                            })));
                            default -> new UnlabeledCheckbox();
                        });
                    }
                }
                propertiesPanel.addComponentList(uiComponentList);
            }
        }
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        dockSpace.onEvent(event, window);
        scene.onEvent(event, window);
        if (scene3DPanel.isPressed() && !scene3DPanel.isHeld())
        {
            editorCamera.onEvent(event, window);
            // Select an entity
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                selected = selectEntityWithMouse(
                        new Vector2f(mousePressedEvent.xPos - scene3DPanel.getSceneTexture().getPosition().getX(), mousePressedEvent.yPos - scene3DPanel.getSceneTexture().getPosition().getY()),
                        scene3DPanel.getSceneTexture().getSize(),
                        selected, scene.getRegistry());
            }
        }

        if (Input.wasKeyPressed(event, KeyboardKey.KEY_F)) window.toggleFullscreen();

        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            editorCamera.setAspectRatio(scene3DPanel.getSceneTexture().getSize().getX() / scene3DPanel.getSceneTexture().getSize().getY());
        }
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

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {
        editorCamera.onUpdate(deltaTime, window);
        scene.onUpdate(deltaTime, window);
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer.enableDepthTest();
        Renderer3D.renderSceneToTexture(window, editorCamera, scene, scene3DPanel.getSceneTexture());

        Renderer.disableDepthTest();
        dockSpace.render();
    }

    @Override
    public void clientDispose()
    {
        dockSpace.dispose();
        scene.onDispose();
    }
}
