package sandbox;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.graphics.CubeMap;
import haraldr.graphics.JsonModel;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.lighting.PointLight;
import haraldr.graphics.lighting.SceneLights;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.main.Scene;
import haraldr.main.Window;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;

public class TestScene implements Scene
{
    private PointLight l1 = new PointLight(new Vector3f(0f, 0.2f, 0f), new Vector3f(4.5f, 2.5f, 3f));
    private PointLight l2 = new PointLight(new Vector3f(), new Vector3f(2.5f, 2.5f, 7.5f));
    private float interpolation, interpolation2;

    private CubeMap environmentMap = CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr");
    private JsonModel model = new JsonModel("default_models/test.json", Matrix4f.identity().rotate(Vector3f.UP, 180f));
    private boolean renderLights = true;

    @Override
    public void onActivate()
    {
        SceneLights sl = new SceneLights();
        sl.addLight(l1);
        sl.addLight(l2);
        Renderer3D.setSceneLights(sl);
    }

    @Override
    public void onEvent(Event event)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (Input.wasKey(event, Key.KEY_R))
            {
                model.refresh();
            }
            if (Input.wasKey(event, Key.KEY_L))
            {
                renderLights = !renderLights;
            }
        }
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_KP_7))   interpolation += 1f * deltaTime;
        if (Input.isKeyPressed(window, Key.KEY_KP_9))   interpolation -= 1f * deltaTime;
        if (Input.isKeyPressed(window, Key.KEY_KP_1))   interpolation2 += 1f * deltaTime;
        if (Input.isKeyPressed(window, Key.KEY_KP_3))   interpolation2 -= 1f * deltaTime;
        l1.setPosition(new Vector3f(Math.sin(interpolation), 0.15f, Math.cos(interpolation)));
        l2.setPosition(new Vector3f(Math.sin(interpolation2) * 0.6f, 0.3f, Math.cos(interpolation2)));
    }

    @Override
    public void onRender()
    {
        model.render();
        if (renderLights)
        {
            l1.render();
            l2.render();
        }
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
    }
}
