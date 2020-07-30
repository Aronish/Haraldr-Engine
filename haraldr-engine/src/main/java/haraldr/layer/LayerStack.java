package haraldr.layer;

import java.util.ArrayDeque;
import java.util.Deque;

public class LayerStack
{
    private final Deque<Layer> layerStack = new ArrayDeque<>();
    private final Deque<Layer> overlayStack = new ArrayDeque<>();

    public void pushLayer(Layer layer)
    {
        layerStack.push(layer);
    }

    public void pushOverlay(Layer layer)
    {
        overlayStack.push(layer);
    }

    public Deque<Layer> getLayerStack()
    {
        return layerStack;
    }

    public Deque<Layer> getOverlayStack()
    {
        return overlayStack;
    }
}
