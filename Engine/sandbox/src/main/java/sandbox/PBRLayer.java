package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.graphics.CubeMap;
import engine.graphics.DefaultModels;
import engine.graphics.ObjParser;
import engine.graphics.Renderer3D;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.graphics.VertexArray;
import engine.graphics.pbr.PBRRenderer;
import engine.input.Input;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class PBRLayer extends Layer
{
    private CubeMap environmentMap = new CubeMap("default_hdris/cape_hill_4k.hdr");

    private static final int SPHERE_COUNT = 7;
    private Matrix4f[][] spherePositions = new Matrix4f[SPHERE_COUNT][SPHERE_COUNT];
    private Vector3f sphereColor = new Vector3f(1f, 0f, 0f);

    private Vector3f lightPosition = new Vector3f(4f, 2f, 5f), lightColor = new Vector3f(10f, 10f, 5f);

    private Texture albedo      = new Texture("default_textures/MetalSpottyDiscoloration001_COL_4K_METALNESS.jpg", true);
    private Texture normal      = new Texture("default_textures/MetalSpottyDiscoloration001_NRM_4K_METALNESS.jpg", false);
    private Texture metallic    = new Texture("default_textures/MetalSpottyDiscoloration001_METALNESS_4K_METALNESS.jpg", false);
    private Texture roughness   = new Texture("default_textures/MetalSpottyDiscoloration001_ROUGHNESS_4K_METALNESS.jpg", false);

    private VertexArray mesh = ObjParser.loadMesh("models/suzanne_semi_smooth.obj");

    public PBRLayer(String name)
    {
        super(name);
        for (int y = 0; y < SPHERE_COUNT; ++y)
        {
            for (int x = 0; x < SPHERE_COUNT; ++x)
            {
                spherePositions[x][y] = Matrix4f.translate(new Vector3f(x / 2f, y / 2f, 0f)).multiply(Matrix4f.scale(new Vector3f(0.2f)));
            }
        }
    }

    @Override
    public void onAttach(Window window)
    {

    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            EventHandler.onKeyPress((KeyPressedEvent) event, window);
        }
    }

    private float sin, cos;

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     PBRRenderer.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   PBRRenderer.addExposure(-1f * deltaTime);
        sin = (float) Math.sin(Application.time / 3f);
        cos = (float) Math.cos(Application.time / 3f);
        lightPosition = new Vector3f(sin * 2f, 0f, cos * 2f);
    }

    @Override
    public void onRender()
    {
        Shader.PBR.bind();
        albedo.bind(0);
        normal.bind(1);
        metallic.bind(2);
        roughness.bind(3);
        Shader.PBR.setVector3f(Renderer3D.getPerspectiveCamera().getPosition(), "viewPosition");
        Shader.PBR.setVector3f(lightPosition, "lightPosition");
        Shader.PBR.setVector3f(lightColor, "lightColor");
        Shader.PBR.setMatrix4f(Matrix4f.scale(new Vector3f(0.75f)), "model");
        mesh.bind();
        mesh.drawElements();
        /*
        for (int y = 0; y < SPHERE_COUNT; ++y)
        {
            for (int x = 0; x < SPHERE_COUNT; ++x)
            {
                Shader.PBR.setVector3f(sphereColor, "albedo");
                Shader.PBR.setFloat(y / (float) SPHERE_COUNT, "metallic");
                Shader.PBR.setFloat((x + 0.25f) / SPHERE_COUNT, "roughness");
                Shader.PBR.setMatrix4f(spherePositions[x][y], "model");
                DefaultModels.SPHERE.bind();
                DefaultModels.SPHERE.drawElements();
            }
        }
        */
        Shader.LIGHT_SHADER.bind();
        Shader.LIGHT_SHADER.setVector3f(lightColor, "color");
        Shader.LIGHT_SHADER.setMatrix4f(Matrix4f.translate(lightPosition).multiply(Matrix4f.scale(new Vector3f(0.0625f))), "model");
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();

        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
        environmentMap.delete();
    }
}
