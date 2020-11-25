package editor;

import haraldr.debug.Logger;
import haraldr.dockspace.DockPosition;
import haraldr.dockspace.Dockspace;
import haraldr.dockspace.uicomponents.Checkbox;
import haraldr.dockspace.uicomponents.InfoLabel;
import haraldr.dockspace.uicomponents.UnlabeledCheckbox;
import haraldr.dockspace.uicomponents.UnlabeledComponent;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
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

public class EditorApplication extends Application
{
    private Camera editorCamera;
    private Scene3D scene;
    private Entity selected = Entity.INVALID;

    private Dockspace dockSpace;
    private EntityHierarchyPanel entityHierarchyPanel;
    private Scene3DPanel scene3DPanel;

    private PropertiesPanel propertiesPanel;
    private InfoLabel selectedEntityTag;
    private Checkbox selecting;

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
        scene3DPanel.setPanelResizeAction((position, size) ->
        {
            scene3DPanel.getSceneTexture().setPosition(position);
            scene3DPanel.getSceneTexture().setSize(size.getX(), size.getY());
            editorCamera.setAspectRatio(size.getX() / size.getY());
        });

        editorCamera = new OrbitalCamera(scene3DPanel.getSize().getX(), scene3DPanel.getSize().getY());

        // Properties Panel
        dockSpace.addPanel(propertiesPanel = new PropertiesPanel(new Vector2f(), new Vector2f(), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Properties"));
        UIComponentList uiComponentList = new UIComponentList("Component", propertiesPanel);
        uiComponentList.addComponent("Test", new UnlabeledCheckbox());
        uiComponentList.addComponent("Waow cool button", new UnlabeledCheckbox());
        UnlabeledCheckbox testBox = new UnlabeledCheckbox((state) ->
        {
            Logger.info("Checked: " + state);
        });
        uiComponentList.addComponent("Nonii", testBox);
        propertiesPanel.setUiComponentList(uiComponentList);
        /*
        dockSpace.addPanel(propertiesPanel = new ControlPanel(new Vector2f(20f), new Vector2f(300f, 400f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Properties"));
        propertiesPanel.addChild(selectedEntityTag = new InfoLabel("Selected Entity", propertiesPanel));
        propertiesPanel.addChild(new Button("Center Camera", propertiesPanel, () ->
        {
            if (!selected.equals(Entity.INVALID))
            {
                editorCamera.setPosition(scene.getRegistry().getComponent(TransformComponent.class, selected).position);
            }
        }));

        InfoLabel sliderValue;
        propertiesPanel.addChild(sliderValue = new InfoLabel("Exposure Value", propertiesPanel));
        propertiesPanel.addChild(new Slider("Exposure", propertiesPanel, 0f, 2f, value ->
        {
            scene3DPanel.getHdrGammaCorrectionPass().setExposure(value);
            sliderValue.setText(Float.toString(value));
        }));

        propertiesPanel.addChild(selecting = new Checkbox("Selecting", propertiesPanel));
        */

        // Hierarchy Panel
        dockSpace.addPanel(entityHierarchyPanel = new EntityHierarchyPanel(new Vector2f(200f), new Vector2f(200f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Hierarchy"));
        entityHierarchyPanel.refreshEntityList(scene.getRegistry());
        /*
        entityHierarchyPanel.setEntitySelectedAction((selectedEntity ->
        {
            selected = selectedEntity;
            selectedEntityTag.setText(String.format("Entity ID: %d", selected.id));
        }));
        */

        // Pre-docking
        dockSpace.dockPanel(scene3DPanel, DockPosition.RIGHT);
        dockSpace.dockPanel(entityHierarchyPanel, DockPosition.TOP);
        dockSpace.dockPanel(propertiesPanel, DockPosition.CENTER);
        dockSpace.resizePanel(scene3DPanel, 300f);
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        dockSpace.onEvent(event, window);
        scene.onEvent(event, window);
        if (scene3DPanel.isPressed() && !scene3DPanel.isHeld())
        {
            editorCamera.onEvent(event, window);
/*
            // Select an entity
            if (selecting.isChecked() && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                selected = selectEntity(
                        new Vector2f(mousePressedEvent.xPos - scene3DPanel.getSceneTexture().getPosition().getX(), mousePressedEvent.yPos - scene3DPanel.getSceneTexture().getPosition().getY()),
                        scene3DPanel.getSceneTexture().getSize(),
                        selected, scene.getRegistry());
                if (!selected.equals(Entity.INVALID))
                {
                    selectedEntityTag.setText(String.format("Entity ID: %d", selected.id));
                } else
                {
                    selectedEntityTag.setText("No entity selected");
                }
            }
*/
        }

        if (Input.wasKeyPressed(event, KeyboardKey.KEY_F)) window.toggleFullscreen();

        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            editorCamera.setAspectRatio(scene3DPanel.getSceneTexture().getSize().getX() / scene3DPanel.getSceneTexture().getSize().getY());
        }
    }

    private Entity selectEntity(Vector2f mousePoint, Vector2f windowSize, Entity lastSelected, EntityRegistry registry)
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
