package haraldr.main;

import java.util.ArrayList;
import java.util.List;

public class LayerManager
{
    private List<Layer> layers = new ArrayList<>();

    public void addLayer(Layer layer)
    {
        layers.add(layer);
    }

    public void render()
    {
        for (Layer layer : layers)
        {
            layer.render();
        }
    }
}