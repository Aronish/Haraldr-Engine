package engine.graphics.lighting;

import engine.graphics.UniformBuffer;

import java.util.ArrayList;
import java.util.List;

public class SceneLights
{
    private static final int MAX_DIRECTIONAL_LIGHTS = 1,    DIRECTIONAL_LIGHT_SIZE = 32;
    private static final int MAX_POINT_LIGHTS = 15,         POINT_LIGHT_SIZE = 48;
    private static final int MAX_SPOTLIGHTS = 5,            SPOTLIGHT_SIZE = 64;
    private final int TOTAL_LIGHTS_SIZE = MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS * SPOTLIGHT_SIZE + MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_SIZE;

    private float ambientStrength;
    private final UniformBuffer lightSetupBuffer = new UniformBuffer(TOTAL_LIGHTS_SIZE + 16);
    private final List<DirectionalLight> directionalLights = new ArrayList<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private final List<Spotlight> spotlights = new ArrayList<>();

    {
        setAmbientStrength(0.2f);
    }

    public void setAmbientStrength(float ambientStrength)
    {
        this.ambientStrength = ambientStrength;
        lightSetupBuffer.setData(new float[] { ambientStrength }, TOTAL_LIGHTS_SIZE + 12);
    }

    public void addLight(Light light)
    {
        //Really isn't possible to do easily in any other generic way. Not beautiful, but works.
        if (light instanceof PointLight)
            addPointLight((PointLight) light);
        else if (light instanceof Spotlight)
            addSpotLight((Spotlight) light);
        else if (light instanceof DirectionalLight)
            addDirectionalLight((DirectionalLight) light);
    }

    private void addPointLight(PointLight pointLight)
    {
        if (pointLights.size() >= MAX_POINT_LIGHTS) return;
        pointLights.add(pointLight);
        lightSetupBuffer.setData(new float[] { pointLights.size() }, TOTAL_LIGHTS_SIZE);
    }

    private void addSpotLight(Spotlight spotlight)
    {
        if (spotlights.size() >= MAX_SPOTLIGHTS) return;
        spotlights.add(spotlight);
        lightSetupBuffer.setData(new float[] { spotlights.size() }, TOTAL_LIGHTS_SIZE + 4);
    }

    private void addDirectionalLight(DirectionalLight directionalLight)
    {
        if (directionalLights.size() >= MAX_DIRECTIONAL_LIGHTS) return;
        directionalLights.add(directionalLight);
        lightSetupBuffer.setData(new float[] { directionalLights.size() }, TOTAL_LIGHTS_SIZE + 8);
    }
//TODO: Only update changed lights
    public void bind()
    {
        lightSetupBuffer.bind(1);
        for (int i = 0; i < pointLights.size(); ++i)
        {
            pointLights.get(i).updateBufferDataUnsafe(lightSetupBuffer, i * POINT_LIGHT_SIZE);
        }
        for (int i = 0; i < spotlights.size(); ++i)
        {
            spotlights.get(i).updateBufferDataUnsafe(lightSetupBuffer, MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + i * SPOTLIGHT_SIZE);
        }
        for (int i = 0; i < directionalLights.size(); ++i)
        {
            directionalLights.get(i).updateBufferDataUnsafe(lightSetupBuffer, MAX_POINT_LIGHTS * POINT_LIGHT_SIZE + MAX_SPOTLIGHTS * SPOTLIGHT_SIZE + i * DIRECTIONAL_LIGHT_SIZE);
        }
    }

    public void dispose()
    {
        lightSetupBuffer.delete();
    }
}
