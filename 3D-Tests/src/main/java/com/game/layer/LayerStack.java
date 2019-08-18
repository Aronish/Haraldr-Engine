package com.game.layer;

import java.util.ArrayDeque;
import java.util.Iterator;

public class LayerStack implements Iterable<Layer> {

    private final ArrayDeque<Layer> layerStack = new ArrayDeque<>();

    public void pushLayer(Layer layer){
        layerStack.push(layer);
    }

    @Override
    public Iterator<Layer> iterator() {
        return layerStack.iterator();
    }
}
