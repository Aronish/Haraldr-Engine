package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.CubeMap;
import engine.graphics.ForwardRenderer;
import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.lighting.Spotlight;
import engine.graphics.material.ReflectiveMaterial;
import engine.input.Button;
import engine.input.Input;
import engine.layer.Layer;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class BatchTestLayer extends Layer
{
    private ForwardRenderer renderer = new ForwardRenderer();
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
    private final Spotlight flashLight = new Spotlight(perspectiveCamera.getPosition(), perspectiveCamera.getDirection(), new Vector3f(1f), 10f, 25f);
    private final DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0f), new Vector3f(-1f, -2f, -3f), new Vector3f(1f, 1f, 0.8f));
    private final SceneLights sceneLights = new SceneLights();

    private CubeMap environmentMap = new CubeMap("default_hdris/cape_hill_4k.hdr");

    private ReflectiveMaterial reflectiveMaterial = new ReflectiveMaterial(
            environmentMap,
            "default_textures/MetalSpottyDiscoloration001_COL_4K_SPECULAR.jpg",
            "default_textures/MetalSpottyDiscoloration001_REFL_4K_SPECULAR.jpg"
    );

    public BatchTestLayer(String name)
    {
        super(name);
        sceneLights.addLight(flashLight);
        sceneLights.addLight(directionalLight);
        renderer.setSceneLights(sceneLights);
    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            if (window.isFocused())
            {
                perspectiveCamera.getController().handleRotation(perspectiveCamera, (MouseMovedEvent) event);
            }
        }
        if (event.eventType == EventType.KEY_PRESSED)
        {
            EventHandler.onKeyPress((KeyPressedEvent) event, window);
        }
        if (event.eventType == EventType.MOUSE_SCROLLED)
        {
            perspectiveCamera.getController().handleScroll((MouseScrolledEvent) event);
        }
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_1))
            {
                flashLight.setColor(new Vector3f(0f));
            }
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_2))
            {
                flashLight.setColor(new Vector3f(1f));
            }
        }
    }

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
        flashLight.setDirection(perspectiveCamera.getDirection());
        flashLight.setPosition(perspectiveCamera.getPosition());
    }

    @Override
    public void onRender()
    {
        renderer.begin(perspectiveCamera);
        for (int y = 0; y < 100; ++y)
        {
            for (int x = 0; x < 100; ++x)
            {
                renderer.drawCube(new Vector3f(x * 2.1f, y * 2.1f, 0f));
            }
        }
        renderer.end();
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
        environmentMap.delete();
    }
}
