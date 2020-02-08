package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.Mesh;
import engine.graphics.ObjParser;
import engine.graphics.Renderer3D;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class ExampleLayer extends Layer
{
    private Shader objShader = new Shader("default_shaders/default3D.vert", "default_shaders/default3D.frag");
    private Shader lightShader = new Shader("default_shaders/default3D.vert", "default_shaders/light.frag");
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(4f, 2f, -1f));

    private Texture brickColor = new Texture("default_textures/BricksPaintedWhite001_COL_4K.jpg");
    private Texture brickNormals = new Texture("default_textures/BricksPaintedWhite001_NRM_4K.jpg");
    private Mesh wall = ObjParser.load("models/plane.obj");

    private boolean showNormals;

    private float sin, cos;

    public ExampleLayer(String name)
    {
        super(name);
    }

    @Override
    public void onEvent(@NotNull Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            if (window.isFocused())
            {
                perspectiveCamera.getController().handleRotation(perspectiveCamera, (MouseMovedEvent) event);
            }
        }
        if (event.eventType == EventType.MOUSE_SCROLLED)
        {
            perspectiveCamera.getController().handleScroll((MouseScrolledEvent) event);
        }
        if (event.eventType == EventType.KEY_PRESSED)
        {
            EventHandler.onKeyPress((KeyPressedEvent) event, window);
            if (((KeyPressedEvent) event).keyCode == Key.KEY_Q.keyCode)
            {
                lightShader.recompile();
                objShader.recompile();
            }
            if (((KeyPressedEvent) event).keyCode == Key.KEY_N.keyCode)
            {
                showNormals = !showNormals;
            }
        }
    }

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        sin = (float) Math.sin(Application.time / 3f);
        cos = (float) Math.cos(Application.time / 3f);
        Renderer3D.light.setPosition(new Vector3f(cos, sin, 1f));
        Renderer3D.light.setColor(new Vector3f(cos / 2f + 1f, sin / 2f + 1f, cos * sin / 2f + 1f));
        //perspectiveCamera.setPosition(new Vector3f(cos, sin, 1f));
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
    }

    @Override
    public void onRender()
    {
        Renderer3D.beginScene(perspectiveCamera);
        Renderer3D.drawCube(lightShader, Renderer3D.light.getPosition(), 0.0625f, Renderer3D.light.getColor());
        Renderer3D.drawMesh(objShader, wall, brickColor, brickNormals, new Vector3f(0f), 2f);
    }
}