package engine.graphics;

import java.util.ArrayList;
import java.util.List;

public class SceneLights
{
    private UniformBuffer lightSetupBuffer = new UniformBuffer(96);
    private List<Light> lights = new ArrayList<>();

    public void addLight(Light light)
    {
        lights.add(light);
    }

    public List<Light> getLights()
    {
        return lights;
    }
}
