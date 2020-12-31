package haraldr.graphics.lighting;

import haraldr.graphics.UniformBuffer;

import java.util.ArrayList;
import java.util.List;

public class SceneLights
{
    private static final int MAX_DIRECTIONAL_LIGHTS = 1,    DIRECTIONAL_LIGHT_SIZE = 32;
    private static final int MAX_POINT_LIGHTS = 15,         POINT_LIGHT_SIZE = 48;
    private static final int MAX_SPOTLIGHTS = 5,            SPOTLIGHT_SIZE = 64;
    private static final int TOTAL_LIGHTS_SIZE = MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS * SPOTLIGHT_SIZE + MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_SIZE;

    private final UniformBuffer lightSetupBuffer = new UniformBuffer(TOTAL_LIGHTS_SIZE + 12);
    private final List<DirectionalLight> directionalLights = new ArrayList<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private final List<Spotlight> spotlights = new ArrayList<>();

    public void addLight(Light light)
    {
        //Really isn't possible to do easily in any other generic way. Not beautiful, but works.
        if (light instanceof PointLight)
        {
            light.setBufferOffset(pointLights.size() * POINT_LIGHT_SIZE);
            addPointLight((PointLight) light);
        } else if (light instanceof Spotlight)
        {
            light.setBufferOffset(MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + spotlights.size() * SPOTLIGHT_SIZE);
            addSpotLight((Spotlight) light);
        } else if (light instanceof DirectionalLight)
        {
            light.setBufferOffset(MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS * SPOTLIGHT_SIZE + directionalLights.size() * DIRECTIONAL_LIGHT_SIZE);
            addDirectionalLight((DirectionalLight) light);
        }
        light.setSceneLightsBuffer(lightSetupBuffer);
    }

    private void addPointLight(PointLight pointLight)
    {
        if (pointLights.size() >= MAX_POINT_LIGHTS) return;
        pointLights.add(pointLight);
        lightSetupBuffer.setSubData(new float[] { pointLights.size() }, TOTAL_LIGHTS_SIZE);
    }

    private void addSpotLight(Spotlight spotlight)
    {
        if (spotlights.size() >= MAX_SPOTLIGHTS) return;
        spotlights.add(spotlight);
        lightSetupBuffer.setSubData(new float[] { spotlights.size() }, TOTAL_LIGHTS_SIZE + 4);
    }

    private void addDirectionalLight(DirectionalLight directionalLight)
    {
        if (directionalLights.size() >= MAX_DIRECTIONAL_LIGHTS) return;
        directionalLights.add(directionalLight);
        lightSetupBuffer.setSubData(new float[] { directionalLights.size() }, TOTAL_LIGHTS_SIZE + 8);
    }

    public void bind()
    {
        lightSetupBuffer.bind(1);
    }

    public void renderLights()
    {
        pointLights.forEach(Light::render);
    }

    public void dispose()
    {
        lightSetupBuffer.delete();
    }
}