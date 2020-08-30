package editor;

import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.ui.InputField;
import haraldr.graphics.ui.Pane;
import haraldr.graphics.ui.TextLabel;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.main.Application;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;
import haraldr.math.Matrix4f;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics3D;
import haraldr.scene.Camera;
import haraldr.scene.OrbitalCamera;
import haraldr.scene.Scene3D;

public class EditorApplication extends Application
{
    private Pane propertiesPane;
    private InputField entityId;

    private Camera editorCamera;
    private Scene3D scene;
    private Entity selected = Entity.INVALID;

    public EditorApplication()
    {
        super(new Window.WindowProperties(1280, 720, ProgramArguments.getIntOrDefault("MSAA", 0), false, false, false));
    }

    @Override
    protected void clientInit(Window window)
    {
        propertiesPane = new Pane(
                new Vector2f(),
                window.getWidth(), window.getHeight(),
                0.25f,
                0.3f,
                "Properties"
        );
        entityId = new InputField("Selected Entity: ", propertiesPane);
        propertiesPane.addChild(entityId);

        scene = new EditorTestScene();
        scene.onActivate();
        editorCamera = new OrbitalCamera(window.getWidth(), window.getHeight());
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            propertiesPane.onWindowResized(windowResizedEvent.width, windowResizedEvent.height);
        }
        if (Input.wasKeyPressed(event, Key.KEY_ESCAPE)) stop();
        if (Input.wasKeyPressed(event, Key.KEY_F)) window.toggleFullscreen();
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            selected = selectEntity(mousePressedEvent.xPos, mousePressedEvent.yPos, window.getWidth(), window.getHeight(), selected, scene.getRegistry());
            entityId.setText(Integer.toString(selected.id));
        }
        boolean handled = propertiesPane.onEvent(event, window); //TODO: Some kind of layering system to handle event fallthrough.
        if (!handled)
        {
            editorCamera.onEvent(event, window);
            scene.onEvent(event, window);
        }
    }

    private Entity selectEntity(int mouseX, int mouseY, int width, int height, Entity lastSelected, EntityRegistry registry)
    {
        Vector4f rayClipSpace = new Vector4f(
                (2f * mouseX) / width - 1f,
                1f - (2f * mouseY) / height,
                -1f,
                1f
        );
        Vector4f rayEyeSpace = Matrix4f.multiply(Matrix4f.invert(editorCamera.getProjectionMatrix()), rayClipSpace);
        rayEyeSpace.setZ(-1f);
        rayEyeSpace.setW(0f);

        Vector3f rayWorldSpace = new Vector3f(Matrix4f.multiply(Matrix4f.invert(editorCamera.getViewMatrix()), rayEyeSpace));
        rayWorldSpace.normalize();

        Entity selected;
        if (!lastSelected.equals(Entity.INVALID))
        {
            ModelComponent lastModel = registry.getComponent(ModelComponent.class, lastSelected);
            lastModel.model.setOutlined(false);
        }

        selected = registry.view(BoundingSphereComponent.class).find(((transform, bsphere) ->
                Physics3D.rayIntersectsSphere(editorCamera.getPosition(), rayWorldSpace, transform.position, bsphere.radius)), registry);

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
        propertiesPane.onUpdate(deltaTime);
        editorCamera.onUpdate(deltaTime, window);
        scene.onUpdate(deltaTime, window);
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer.enableDepthTest();
        Renderer3D.begin(window, editorCamera);
        scene.onRender();
        Renderer3D.end(window);

        Renderer.disableDepthTest();
        Renderer2D.begin();
        propertiesPane.render();
        Renderer2D.end();
        propertiesPane.renderText();
    }
}
