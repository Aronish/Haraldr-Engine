package engine.graphics;

import java.util.ArrayList;
import java.util.List;

public class SceneLights
{
    private List<Light> lights = new ArrayList<>();

    public void addLight(Light light)
    {
        lights.add(light);
    }

    public void removeLight(Light light)
    {
        lights.remove(light);
    }

    public List<Light> getLights()
    {
        return lights;
    }
}
