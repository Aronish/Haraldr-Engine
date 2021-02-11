package editor;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;
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
import haraldr.ui.WindowHeader;
import haraldr.ui.components.ListData;

public class EditorApplication extends Application
{
    private WindowHeader windowHeader;
    private Dockspace dockSpace;

    // Editor panels
    private EntityHierarchyPanel entityHierarchyPanel;
    private Scene3DPanel scene3DPanel;
    private PropertiesPanel propertiesPanel;

    // Scene
    private Camera editorCamera;
    private Scene3D scene;
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
            ModelComponent lastModel = scene.getRegistry().getComponent(ModelComponent.class, selected);
            lastModel.model.setOutlined(false);
        }
        selected = entity;
        propertiesPanel.clear();
        if (!selected.equals(Entity.INVALID))
        {
            ModelComponent model = scene.getRegistry().getComponent(ModelComponent.class, selected);
            model.model.setOutlined(true);
            propertiesPanel.populateWithEntity(selected, scene.getRegistry());
        }
        entityHierarchyPanel.refreshEntityList(scene.getRegistry());
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

    private void saveScene()
    {
    }

    @Override
    protected void clientInit(Window window)
    {
        // Window header
        windowHeader = new WindowHeader(new Vector2f(), window.getWidth(), new Vector4f(0.4f, 0.4f, 0.4f, 1f));
        windowHeader.addMenuButton(
                "File",
                new ListData("Open", Logger::info),
                new ListData("Save", this::saveScene),
                new ListData("Exit", this::stop)
        );

        // Dockspace
        dockSpace = new Dockspace(
                new Vector2f(0f, windowHeader.getSize().getY()),
                new Vector2f(window.getWidth(), window.getHeight() - windowHeader.getSize().getY())
        );

        // Scene
        scene = new EditorTestScene();
        scene.onActivate();

        // Scene Panel
        dockSpace.addPanel(scene3DPanel = new Scene3DPanel(new Vector2f(100f, 100f), new Vector2f(200f), "Scene"));
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
        dockSpace.addPanel(propertiesPanel = new PropertiesPanel(new Vector2f(300f), new Vector2f(200f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Properties"));
        dockSpace.dockPanel(propertiesPanel, DockPosition.BOTTOM);

        // Hierarchy Panel
        dockSpace.addPanel(entityHierarchyPanel = new EntityHierarchyPanel(new Vector2f(500f), new Vector2f(200f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Hierarchy", this::selectEntity));
        entityHierarchyPanel.refreshEntityList(scene.getRegistry());
        dockSpace.dockPanel(entityHierarchyPanel, DockPosition.CENTER);
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        //TODO: Fix handled check
        windowHeader.onEvent(event, window);
        if (!event.isHandled()) dockSpace.onEvent(event, window);
        if (!event.isHandled())
        {
            scene.onEvent(event, window);
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
                        this.selected, scene.getRegistry());
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
        scene.onUpdate(deltaTime, window);
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer.enableDepthTest();
        Renderer3D.renderSceneToTexture(window, editorCamera, scene, scene3DPanel.getSceneTexture());

        Renderer.disableDepthTest();
        dockSpace.render();
        windowHeader.render();
    }

    @Override
    public void clientDispose()
    {
        dockSpace.dispose();
        scene.onDispose();
    }
}
