package com.game.event;

@FunctionalInterface
public interface IEventCallback
{
    void onEvent(Event event);
}
