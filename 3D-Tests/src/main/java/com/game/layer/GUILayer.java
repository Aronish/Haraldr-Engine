package com.game.layer;

import com.game.Window;
import com.game.event.Event;
import com.game.graphics.FontRenderer;

public class GUILayer extends Layer {

    private FontRenderer fontRenderer;

    public GUILayer(String name) {
        super(name);
    }

    public void init(Window window){
        fontRenderer = new FontRenderer(window);
        fontRenderer.setup();
    }

    public void render(){
        fontRenderer.render();
    }

    @Override
    public void onEvent(Window window, Event event) {
        //LOGGER.info(event.toString());
    }
}
