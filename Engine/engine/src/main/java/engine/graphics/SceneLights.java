package engine.graphics;

import java.util.ArrayList;
import java.util.List;

public class SceneLights
{
    private static final int MAX_DIRECTIONAL_LIGHTS = 1, DIRECTIONAL_LIGHT_SIZE = 32;
    private static final int MAX_POINT_LIGHTS = 3, POINT_LIGHT_SIZE = 48;
    private static final int MAX_SPOTLIGHTS = 3, SPOTLIGHT_SIZE = 64;

    private UniformBuffer lightSetupBuffer = new UniformBuffer(MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_SIZE + MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS * SPOTLIGHT_SIZE + 12);
    private List<DirectionalLight> directionalLights = new ArrayList<>();
    private List<PointLight> pointLights = new ArrayList<>();
    private List<Spotlight> spotlights = new ArrayList<>();

    public void addDirectionalLight(DirectionalLight directionalLight)
    {
        if (directionalLights.size() >= MAX_DIRECTIONAL_LIGHTS) return;
        directionalLights.add(directionalLight);
        SceneLightCompat.lightSetupMask.addX(1f);
        lightSetupBuffer.setData(new float[] { SceneLightCompat.lightSetupMask.getX(), SceneLightCompat.lightSetupMask.getY(), SceneLightCompat.lightSetupMask.getZ() }, MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_SIZE + MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS * SPOTLIGHT_SIZE);
    }

    public void addPointLight(PointLight pointLight)
    {
        if (pointLights.size() >= MAX_POINT_LIGHTS) return;
        pointLights.add(pointLight);
        SceneLightCompat.lightSetupMask.addY(1f);
        lightSetupBuffer.setData(new float[] { SceneLightCompat.lightSetupMask.getX(), SceneLightCompat.lightSetupMask.getY(), SceneLightCompat.lightSetupMask.getZ() }, MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_SIZE + MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS * SPOTLIGHT_SIZE);
    }

    public void addSpotLight(Spotlight spotlight)
    {
        if (spotlights.size() >= MAX_SPOTLIGHTS) return;
        spotlights.add(spotlight);
        SceneLightCompat.lightSetupMask.addZ(1f);
        lightSetupBuffer.setData(new float[] { SceneLightCompat.lightSetupMask.getX(), SceneLightCompat.lightSetupMask.getY(), SceneLightCompat.lightSetupMask.getZ() }, MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_SIZE + MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS * SPOTLIGHT_SIZE);
    }

    public void bind()
    {
        lightSetupBuffer.bindBuffer();
        for (int i = 0; i < directionalLights.size(); ++i)
        {
            directionalLights.get(i).updateBufferDataUnsafe(lightSetupBuffer, i * DIRECTIONAL_LIGHT_SIZE);
        }
        for (int i = 0; i < pointLights.size(); ++i)
        {
            pointLights.get(i).updateBufferDataUnsafe(lightSetupBuffer, MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_SIZE + i * POINT_LIGHT_SIZE);
        }
        for (int i = 0; i < spotlights.size(); ++i)
        {
            spotlights.get(i).updateBufferDataUnsafe(lightSetupBuffer, MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_SIZE + MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + i * SPOTLIGHT_SIZE);
        }
        lightSetupBuffer.unbindBuffer();
        lightSetupBuffer.bind(1);
    }
}
