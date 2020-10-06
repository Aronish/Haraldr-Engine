package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.dockspace.Dockspace;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.RenderTexture;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.Shader;
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
    private Camera editorCamera;
    private Scene3D scene;
    private Entity selected = Entity.INVALID;

    private Dockspace dockSpace;
    private DockablePanel scenePanel;

    private Shader postProcessingShader;
    private float exposure = 0.5f;
    private Matrix4f pixelOrthographic;

    private RenderTexture sceneTexture;

    public EditorApplication()
    {
        super(new Window.WindowProperties(
                1280, 720,
                ProgramArguments.getIntOrDefault("MSAA", 0),
                false, false, true, false)
        );
    }

    @Override
    protected void clientInit(Window window)
    {
        pixelOrthographic = Matrix4f.orthographic(0, window.getWidth(), window.getHeight(), 0f, -1, 1f);
        postProcessingShader = Shader.create("internal_shaders/hdr_gamma_correct.glsl");

        scene = new EditorTestScene();
        scene.onActivate();

        dockSpace = new Dockspace(new Vector2f(), new Vector2f(window.getWidth(), window.getHeight()));
        dockSpace.addPanel(scenePanel = new DockablePanel(new Vector2f(), new Vector2f(250f), new Vector4f(0.8f, 0.2f, 0.3f, 1f)));
        scenePanel.setPanelResizeAction((position, size) ->
        {
            sceneTexture.setPosition(position);
            sceneTexture.setSize(size.getX(), size.getY());
        });

        sceneTexture = new RenderTexture(
                Vector2f.add(scenePanel.getPosition(), new Vector2f(0f, scenePanel.getHeaderHeight())),
                Vector2f.add(scenePanel.getSize(), new Vector2f(0f, -scenePanel.getHeaderHeight()))
        );

        editorCamera = new OrbitalCamera(scenePanel.getSize().getX(), scenePanel.getSize().getY());
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        dockSpace.onEvent(event, window);
        editorCamera.onEvent(event, window);
        scene.onEvent(event, window);

        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            editorCamera.setAspectRatio(sceneTexture.getSize().getX() / sceneTexture.getSize().getY());
            pixelOrthographic = Matrix4f.orthographic(0f, window.getWidth(), window.getHeight(), 0f, -1f, 1f);
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
        editorCamera.onUpdate(deltaTime, window);
        scene.onUpdate(deltaTime, window);
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer.setViewPort(0, 0, (int)sceneTexture.getSize().getX(), (int)sceneTexture.getSize().getY());
        Renderer.enableDepthTest();
        Renderer3D.begin(window, editorCamera, sceneTexture.getFramebuffer());
        scene.onRender();
        Renderer3D.end(window, sceneTexture.getFramebuffer());

        Renderer.setViewPort(0, 0, window.getWidth(), window.getHeight());
        Renderer.disableDepthTest();
        Renderer2D.begin();
        dockSpace.render();
        Renderer2D.end();

        postProcessingShader.bind();
        postProcessingShader.setFloat("u_Exposure", exposure);
        postProcessingShader.setMatrix4f("projection", pixelOrthographic);
        sceneTexture.getFramebuffer().getColorAttachmentTexture().bind(0);
        sceneTexture.getQuad().bind();
        sceneTexture.getQuad().drawElements();
    }

    @Override
    public void clientDispose()
    {
        scene.onDispose();
        sceneTexture.delete();
    }
}
