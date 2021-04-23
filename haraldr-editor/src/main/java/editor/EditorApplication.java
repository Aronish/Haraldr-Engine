package editor;

import haraldr.dockspace.DockPosition;
import haraldr.dockspace.Dockspace;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.FrameInfoUpdatedEvent;
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
import haraldr.ui.UIHeader;
import haraldr.ui.UILayerStack;
import haraldr.ui.components.ListData;
import haraldr.ui.components.UILayerable;

/* TODO:
    Docking with many panels is broken
    Relativize asset paths in scene and model files
    Project abstraction
 */
public class EditorApplication extends Application
{
    private UILayerStack mainLayerStack = new UILayerStack();
    private Dockspace dockSpace;

    // Editor panels
    private ProjectManagerPanel projectManagerPanel;
    private RendererInfoPanel rendererInfoPanel;
    private EntityHierarchyPanel entityHierarchyPanel;
    private Scene3DPanel scene3DPanel;
    private PropertiesPanel propertiesPanel;

    // Scene
    private Camera editorCamera;
    private EditorScene scene;
    private Entity selected = Entity.INVALID;

    public EditorApplication()
    {
        super(new Window.WindowProperties(
                "Haraldr Editor", 1280, 720,
                ProgramArguments.getIntOrDefault("MSAA", 0),
                true, false, true, true)
        );
    }

    // TODO: Outdated
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

    // TODO: Outdated
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
    protected void clientInit(Window window)
    {
        Renderer.setClearColor(1f, 0f, 0f, 1f);
        // Window header
        UIHeader windowHeader = new UIHeader(
                mainLayerStack, 0,
                new Vector2f(),
                new Vector2f(window.getWidth(), mainLayerStack.getLayer(0).getTextBatch().getFont().getSize()),
                new Vector4f(0.4f, 0.4f, 0.4f, 1f)
        );
        windowHeader.addMenuButton(
                "File",
                new ListData("Open", () ->
                {
                    scene.openScene();
                    entityHierarchyPanel.refreshEntityList(this.scene.getEntityRegistry());
                }),
                new ListData("Save", () -> scene.saveScene()),
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
        scene = new EditorScene();

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

        // Properties Panel
        dockSpace.addPanel(propertiesPanel = new PropertiesPanel(new Vector2f(300f), new Vector2f(200f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Properties"));

        // Hierarchy Panel
        dockSpace.addPanel(entityHierarchyPanel = new EntityHierarchyPanel(new Vector2f(500f), new Vector2f(200f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Hierarchy", this::selectEntity));
        entityHierarchyPanel.refreshEntityList(scene.getEntityRegistry());

        // EditorScenePanel
        dockSpace.addPanel(rendererInfoPanel = new RendererInfoPanel(new Vector2f(300f), new Vector2f(500f, 300f), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Scene Properties"));

        dockSpace.dockPanel(scene3DPanel, DockPosition.RIGHT);
        dockSpace.dockPanel(entityHierarchyPanel, DockPosition.TOP);
        dockSpace.dockPanel(propertiesPanel, DockPosition.TOP);
        dockSpace.dockPanel(rendererInfoPanel, DockPosition.CENTER);
        dockSpace.resizePanel(scene3DPanel, 300f);
        dockSpace.resizePanel(entityHierarchyPanel, 200f);
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
        if (event.eventType == EventType.FRAME_INFO_UPDATED)
        {
            var frameInfoUpdatedEvent = (FrameInfoUpdatedEvent) event;
            rendererInfoPanel.setFrameRateInfo(frameInfoUpdatedEvent.fps, frameInfoUpdatedEvent.ups, frameInfoUpdatedEvent.frameTime);
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