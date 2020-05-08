package engine.layer;

import engine.main.Window;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Last pushed Layer will receive events first and be rendered last. Usually what you want for GUI and such.
 */
public class LayerStack implements Iterable<Layer>
{
    private final Deque<Layer> layerStack = new ArrayDeque<>();

    public void pushLayer(Layer layer, Window window)
    {
        layerStack.push(layer);
        layer.onAttach(window);
    }

    public Iterator<Layer> reverseIterator()
    {
        return layerStack.descendingIterator();
    }

    @NotNull
    @Override
    public Iterator<Layer> iterator()
    {
        return layerStack.iterator();
    }
}
