package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.graphics.CubeMap;
import engine.graphics.DefaultModels;
import engine.graphics.Renderer3D;
import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.graphics.VertexArray;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLights;
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
    private CubeMap environmentMap = CubeMap.createEnvironmentMap("default_hdris/wooden_lounge_4k.hdr");
    private CubeMap diffuseIrradianceMap = CubeMap.createDiffuseIrradianceMap(environmentMap);
    private CubeMap prefilteredMap = CubeMap.createPrefilteredEnvironmentMap(environmentMap);

    private static final int SPHERE_COUNT = 7;
    private Matrix4f[][] spherePositions = new Matrix4f[SPHERE_COUNT][SPHERE_COUNT];
    private Vector3f sphereColor = new Vector3f(1.f, 0.71f, 0.29f);

    private PointLight l1 = new PointLight(new Vector3f(1.6f, 1.6f,3f), new Vector3f(10f, 10f, 5f));
    private PointLight l2 = new PointLight(new Vector3f(1.0f, 1.6f,3f), new Vector3f(10f, 10f, 5f));
    private PointLight l3 = new PointLight(new Vector3f(1.6f, 1.0f,3f), new Vector3f(10f, 10f, 5f));
    private PointLight l4 = new PointLight(new Vector3f(1.0f, 1.0f,3f), new Vector3f(10f, 10f, 5f));

    private Texture albedo      = new Texture("default_textures/MetalDesignerWeaveSteel002_COL_4K_METALNESS.jpg", true);
    private Texture normal      = new Texture("default_textures/MetalDesignerWeaveSteel002_NRM_4K_METALNESS.jpg", false);
    private Texture metallic    = new Texture("default_textures/MetalDesignerWeaveSteel002_METALNESS_4K_METALNESS.jpg", false);
    private Texture roughness   = new Texture("default_textures/MetalDesignerWeaveSteel002_ROUGHNESS_4K_METALNESS.jpg", false);

    private VertexArray mesh = ResourceManager.getMesh("models/cylinder.obj");

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
        SceneLights sl = new SceneLights();
        sl.addLight(l1);
        sl.addLight(l2);
        sl.addLight(l3);
        sl.addLight(l4);
        Renderer3D.setSceneLights(sl);
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

    private float sin, cos, rotation;
    private int drawAmount = mesh.indexAmount;
    private Matrix4f transformation = Matrix4f.IDENTITY;

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_LEFT))
        {
            --drawAmount;
            if (drawAmount < 0) drawAmount = 0;
            System.out.println(drawAmount);
        }
        if (Input.isKeyPressed(window, Key.KEY_RIGHT))
        {
            ++drawAmount;
            if (drawAmount > mesh.indexAmount) drawAmount = mesh.indexAmount;
            System.out.println(drawAmount);
        }
        sin = (float) Math.sin(Application.time / 3f);
        cos = (float) Math.cos(Application.time / 3f);
        rotation += 10f * deltaTime;
        transformation = Matrix4f.rotate(Vector3f.UP, rotation);
    }

    @Override
    public void onRender()
    {
        Shader.PBR.bind();
        albedo.bind(0);
        normal.bind(1);
        metallic.bind(2);
        roughness.bind(3);
        diffuseIrradianceMap.bind(4);
        prefilteredMap.bind(5);
        Texture.BRDF_LUT.bind(6);
        Shader.PBR.setVector3f(Renderer3D.getPerspectiveCamera().getPosition(), "viewPosition");
        Shader.PBR.setMatrix4f(transformation, "model");
        mesh.bind();
        mesh.drawElements();
        /*for (int y = 0; y < SPHERE_COUNT; ++y)
        {
            for (int x = 0; x < SPHERE_COUNT; ++x)
            {
                Shader.PBR.setVector3f(sphereColor, "u_Albedo");
                Shader.PBR.setFloat(y / (float) SPHERE_COUNT, "u_Metallic");
                Shader.PBR.setFloat((x + 0.25f) / SPHERE_COUNT, "u_Roughness");
                Shader.PBR.setMatrix4f(spherePositions[x][y], "model");
                DefaultModels.SPHERE.bind();
                DefaultModels.SPHERE.drawElements();
            }
        }*/
        l1.render();
        l2.render();
        l3.render();
        l4.render();
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
        environmentMap.delete();
    }
}
