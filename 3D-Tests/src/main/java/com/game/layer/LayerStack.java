package com.game.layer;

import java.util.ArrayDeque;

public class LayerStack {

    public final ArrayDeque<Layer> layerStack;

    public LayerStack(){
        layerStack = new ArrayDeque<>();
    }

    public void pushLayer(Layer layer){
        layerStack.push(layer);
    }

}
