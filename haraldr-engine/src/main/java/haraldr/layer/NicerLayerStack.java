package haraldr.layer;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;

public class NicerLayerStack implements Iterable<Layer>
{
    private LinkedList<Layer> layerStack = new LinkedList<>();
    private int overlayInsertPointer;

    public void pushLayer(Layer layer)
    {
        layerStack.add(overlayInsertPointer, layer);
        ++overlayInsertPointer;
    }

    public void pushOverlay(Layer layer)
    {
        layerStack.add(overlayInsertPointer, layer);
    }

    public int size()
    {
        return layerStack.size();
    }

    public int getOverlayInsertPointer()
    {
        return overlayInsertPointer;
    }

    public Iterator<Layer> reverse()
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
