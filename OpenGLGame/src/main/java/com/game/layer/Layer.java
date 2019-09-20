package com.game.layer;

import com.game.Window;
import com.game.debug.Logger;
import com.game.event.Event;

public abstract class Layer
{
    private final String name;
    protected final Logger LOGGER;

    public Layer(String name)
    {
        this.name = name;
        LOGGER = new Logger(name);
    }

    public abstract void onEvent(Window window, Event event);

    public abstract void onUpdate(Window window, float deltaTime);

    public abstract void onRender();

    public String getName()
    {
        return name;
    }
}
