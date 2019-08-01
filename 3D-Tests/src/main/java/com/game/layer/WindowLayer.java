package com.game.layer;

import com.game.event.Event;
import com.game.event.EventCategory;

public class WindowLayer extends Layer {

    public WindowLayer(String name) {
        super(name);
    }

    @Override
    public void onEvent(Event e) {
        if (e.isInCategory(EventCategory.CATEGORY_WINDOW)){
            LOGGER.info(e.toString());
            e.setHandled(true);
        }
    }
}
