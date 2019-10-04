package com.game.layer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Last pushed Layer will receive events first and be rendered last. Usually what you want for GUI and such.
 */
public class LayerStack implements Iterable<Layer>
{
    private final Deque<Layer> layerStack = new ArrayDeque<>();

    public void pushLayer(Layer layer)
    {
        layerStack.push(layer);
    }

    public void pushLayers(Layer... layers)
    {
        for (Layer layer : layers)
        {
            layerStack.push(layer);
        }
    }

    public Iterator<Layer> reverseIterator()
    {
        return layerStack.descendingIterator();
    }

    @Override
    public Iterator<Layer> iterator()
    {
        return layerStack.iterator();
    }
}
