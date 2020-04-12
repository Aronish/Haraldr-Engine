package engine.graphics;

import java.util.ArrayList;
import java.util.List;

public class SceneLights
{
    private static final int MAX_DIRECTIONAL_LIGHTS = 1,    DIRECTIONAL_LIGHT_SIZE = 32;
    private static final int MAX_POINT_LIGHTS = 20,          POINT_LIGHT_SIZE = 48;
    private static final int MAX_SPOTLIGHTS = 1,            SPOTLIGHT_SIZE = 64;

    private final ShaderStorageBuffer lightSetupBuffer = new ShaderStorageBuffer(MAX_POINT_LIGHTS * POINT_LIGHT_SIZE);
    private final ShaderStorageBuffer interfaceBlock = new ShaderStorageBuffer(MAX_POINT_LIGHTS * 4);
    private final List<DirectionalLight> directionalLights = new ArrayList<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private final List<Spotlight> spotlights = new ArrayList<>();

    public void addPointLight(PointLight pointLight)
    {
        if (pointLights.size() >= MAX_POINT_LIGHTS) return;
        pointLights.add(pointLight);
    }

    public void addDirectionalLight(DirectionalLight directionalLight)
    {
        if (directionalLights.size() >= MAX_DIRECTIONAL_LIGHTS) return;
        directionalLights.add(directionalLight);
    }

    public void addSpotLight(Spotlight spotlight)
    {
        if (spotlights.size() >= MAX_SPOTLIGHTS) return;
        spotlights.add(spotlight);
    }

    public void bind()
    {
        lightSetupBuffer.bind(1);
        for (int i = 0; i < pointLights.size(); ++i)
        {
            pointLights.get(i).updateBufferDataUnsafe2(lightSetupBuffer, i * POINT_LIGHT_SIZE);
        }
        interfaceBlock.bind(2);
        /*
        for (int i = 0; i < spotlights.size(); ++i)
        {
            spotlights.get(i).updateBufferDataUnsafe(lightSetupBuffer, MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + i * SPOTLIGHT_SIZE);
        }
        for (int i = 0; i < directionalLights.size(); ++i)
        {
            directionalLights.get(i).updateBufferDataUnsafe(lightSetupBuffer, MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS + SPOTLIGHT_SIZE + i * DIRECTIONAL_LIGHT_SIZE);
        }*/
    }

    public void dispose()
    {
        lightSetupBuffer.delete();
    }
}
