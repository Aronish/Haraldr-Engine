package com.game.layer;

import com.game.event.Event;
import com.game.event.EventCategory;

public class WorldLayer extends Layer {

    public WorldLayer(String name) {
        super(name);
    }

    @Override
    public void onEvent(Event e) {
        if (e.isInCategory(EventCategory.CATEGORY_KEYBOARD.bitFlag | EventCategory.CATEGORY_MOUSE.bitFlag)){
            LOGGER.info(e.toString());
            e.setHandled(true);
        }
    }
}
