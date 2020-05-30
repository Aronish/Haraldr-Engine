package sandbox;

import engine.event.Event;
import engine.graphics.CubeMap;
import engine.graphics.Model;
import engine.graphics.Renderer3D;
import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.material.Material;
import engine.graphics.material.PBRMaterial;
import engine.graphics.material.RefractiveMaterial;
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
    private CubeMap environmentMap = CubeMap.createEnvironmentMap("default_hdris/TexturesCom_NorwayForest_4K_hdri_sphere.hdr");

    private PointLight l1 = new PointLight(new Vector3f(0f, 1f, 0f), new Vector3f(2f));
    private DirectionalLight l2 = new DirectionalLight(new Vector3f(0f, 2f, 0f), new Vector3f(0f, -1f, 0f), new Vector3f(1f));

    private Material pbr = new PBRMaterial(
            "default_textures/Tiles_Glass_1K_albedo.png",
            "default_textures/Tiles_Glass_1K_normal.png",
            "default_textures/Tiles_Glass_1K_metallic.png",
            "default_textures/Tiles_Glass_1K_roughness.png",
            environmentMap
    );

    private Material pbr2 = new PBRMaterial(
            "default_textures/Cerberus_A.png",
            "default_textures/Cerberus_N.png",
            "default_textures/Cerberus_M.png",
            "default_textures/Cerberus_R.png",
            environmentMap
    );

    private Material material = new RefractiveMaterial(
            "default_textures/MetalSpottyDiscoloration001_COL_4K_SPECULAR.jpg",
            "default_textures/MetalSpottyDiscoloration001_REFL_4K_SPECULAR.jpg",
            environmentMap
    );

    private Model model = new Model(
            "models/suzanne_smooth.obj",
            material,
            Matrix4f.createRotate(Vector3f.UP, 180f).scale(new Vector3f(0.8f))
    );

    public PBRLayer(String name)
    {
        super(name);
        SceneLights sl = new SceneLights();
        sl.addLight(l1);
        sl.addLight(l2);
        Renderer3D.setSceneLights(sl);
    }

    @Override
    public void onEvent(@NotNull Window window, @NotNull Event event)
    {
    }

    private float sin, cos, rotation;

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
        sin = (float) Math.sin(Application.time / 3f);
        cos = (float) Math.cos(Application.time / 3f);
        //rotation += 10f * deltaTime;
        l1.setPosition(new Vector3f(sin, 0.3f, cos));
    }

    @Override
    public void onRender()
    {
        model.render();
        l1.render();
        l2.render();
        l2.renderDirectionVector();
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
    }
}
