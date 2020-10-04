package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.dockspace.Dockspace;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.graphics.Framebuffer;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.Shader;
import haraldr.graphics.ShaderDataType;
import haraldr.graphics.VertexArray;
import haraldr.graphics.VertexBuffer;
import haraldr.graphics.VertexBufferElement;
import haraldr.graphics.VertexBufferLayout;
import haraldr.graphics.ui.InfoLabel;
import haraldr.graphics.ui.Pane;
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
    private InfoLabel entityId;

    private Camera editorCamera;
    private Scene3D scene;
    private Entity selected = Entity.INVALID;

    private Dockspace dockSpace;
    private DockablePanel scenePanel;

    private Framebuffer sceneFramebuffer;
    private VertexArray SCREEN_QUAD;
    private VertexBuffer quadVertices;
    private Shader postProcessingShader;
    private float exposure = 0.5f;
    private Matrix4f pixelOrthographic;

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
        float[] quadVertexData = {
                0f, 250f,   0f, 1f,
                250f, 250f, 1f, 1f,
                250f, 0f,   1f, 0f,
                0f, 0f,     0f, 0f
        };
        quadVertices = new VertexBuffer(
                quadVertexData,
                new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2), new VertexBufferElement(ShaderDataType.FLOAT2)),
                VertexBuffer.Usage.DYNAMIC_DRAW
        );
        SCREEN_QUAD = new VertexArray();
        SCREEN_QUAD.setVertexBuffers(quadVertices);
        SCREEN_QUAD.setIndexBufferData(new int[] { 0, 1, 2, 0, 2, 3 });
        /*
        propertiesPane = new Pane(
                new Vector2f(),
                window.getWidth(), window.getHeight(),
                0.25f,
                true,
                "Properties"
        );
        entityId = new InfoLabel("Selected", propertiesPane);
        propertiesPane.addChild(entityId);

        Button centerCamera = new Button("Center Camera", propertiesPane, () ->
        {
            if (!selected.equals(Entity.INVALID))
            {
                editorCamera.setPosition(scene.getRegistry().getComponent(TransformComponent.class, selected).position);
            }
        });
        propertiesPane.addChild(centerCamera);
        */
        scene = new EditorTestScene();
        scene.onActivate();

        dockSpace = new Dockspace(new Vector2f(), new Vector2f(window.getWidth(), window.getHeight()));
        dockSpace.addPanel(scenePanel = new DockablePanel(new Vector2f(), new Vector2f(250f), new Vector4f(0.8f, 0.2f, 0.3f, 1f)));

        editorCamera = new OrbitalCamera(scenePanel.getSize().getX(), scenePanel.getSize().getY());
        //editorCamera = new OrbitalCamera(window.getWidth(), window.getHeight());

        sceneFramebuffer = new Framebuffer();
        sceneFramebuffer.setColorAttachment(new Framebuffer.ColorAttachment((int)scenePanel.getSize().getX(), (int)scenePanel.getSize().getY() - (int)scenePanel.getHeaderHeight(), Framebuffer.ColorAttachment.Format.RGB16F));
        sceneFramebuffer.setDepthBuffer(new Framebuffer.RenderBuffer((int)scenePanel.getSize().getX(), (int)scenePanel.getSize().getY() - (int)scenePanel.getHeaderHeight(), Framebuffer.RenderBuffer.Format.DEPTH_24_STENCIL_8));
        //sceneFramebuffer.setColorAttachment(new Framebuffer.ColorAttachment(window.getWidth(), window.getHeight(), Framebuffer.ColorAttachment.Format.RGB16F));
        //sceneFramebuffer.setDepthBuffer(new Framebuffer.RenderBuffer(window.getWidth(), window.getHeight(), Framebuffer.RenderBuffer.Format.DEPTH_24_STENCIL_8));
        Renderer3D.setFramebuffer(sceneFramebuffer);
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        dockSpace.onEvent(event, window);
        float[] quadVertexData = {
                scenePanel.getPosition().getX(),                                scenePanel.getPosition().getY() + scenePanel.getSize().getY(),    0f, 1f,
                scenePanel.getPosition().getX() + scenePanel.getSize().getX(),  scenePanel.getPosition().getY() + scenePanel.getSize().getY(),    1f, 1f,
                scenePanel.getPosition().getX() + scenePanel.getSize().getX(),  scenePanel.getPosition().getY() + scenePanel.getHeaderHeight(),                                  1f, 0f,
                scenePanel.getPosition().getX(),                                scenePanel.getPosition().getY() + scenePanel.getHeaderHeight(),                                  0f, 0f
        };
        quadVertices.setData(quadVertexData);
        editorCamera.setAspectRatio(scenePanel.getSize().getX() / (scenePanel.getSize().getY() - scenePanel.getHeaderHeight()));
        sceneFramebuffer.resize((int)scenePanel.getSize().getX(), (int)scenePanel.getSize().getY() - (int)scenePanel.getHeaderHeight());
        /*
        boolean handled = propertiesPane.onEvent(event, window); //TODO: Some kind of layering system to handle event fallthrough.
        if (Input.wasKeyPressed(event, KeyboardKey.KEY_ESCAPE)) stop();
        if (Input.wasKeyPressed(event, KeyboardKey.KEY_F)) window.toggleFullscreen();
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            if (Input.wasMousePressed(mousePressedEvent, MouseButton.MOUSE_BUTTON_1))
            {
                selected = selectEntity(mousePressedEvent.xPos, mousePressedEvent.yPos, window.getWidth(), window.getHeight(), selected, scene.getRegistry());
                if (!selected.equals(Entity.INVALID))
                {
                    entityId.setText(String.format("Entity ID: %d", selected.id));
                } else
                {
                    entityId.setText("No entity selected");
                }
            }
        }
        if (!handled)
        {*/
            editorCamera.onEvent(event, window);
            scene.onEvent(event, window);
        //}
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
        //propertiesPane.onUpdate(deltaTime);
        editorCamera.onUpdate(deltaTime, window);
        scene.onUpdate(deltaTime, window);
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer.setViewPort(0, 0, (int)scenePanel.getSize().getX(), (int)scenePanel.getSize().getY() - (int)scenePanel.getHeaderHeight());
        Renderer.enableDepthTest();
        Renderer3D.begin(window, editorCamera);
        scene.onRender();
        Renderer3D.end(window);

        Renderer.setViewPort(0, 0, window.getWidth(), window.getHeight());
        Renderer.disableDepthTest();
        Renderer2D.begin();
        dockSpace.render();
        //propertiesPane.render();
        Renderer2D.end();

        postProcessingShader.bind();
        postProcessingShader.setFloat("u_Exposure", exposure);
        postProcessingShader.setMatrix4f("projection", pixelOrthographic);
        SCREEN_QUAD.bind();
        SCREEN_QUAD.drawElements();
        //propertiesPane.renderText();
    }

    @Override
    public void clientDispose()
    {
        scene.onDispose();
        SCREEN_QUAD.delete();
        sceneFramebuffer.delete();
    }
}
