package com.game.event;

public abstract class Event {

    public final EventType eventType;
    private boolean isHandled;

    public Event(EventType eventType){
        this.eventType = eventType;
    }

    public void setHandled(boolean handled){
        isHandled = handled;
    }

    public boolean isHandled() {
        return isHandled;
    }
}
