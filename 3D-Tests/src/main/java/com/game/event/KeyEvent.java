package com.game.event;

public abstract class KeyEvent extends Event {

    public final int keyCode;

    public KeyEvent(int keyCode, EventType eventType, EventCategory... eventCategories){
        super(eventType, eventCategories);
        this.keyCode = keyCode;
    }

    @Override
    public String toString() {
        return String.format("%s: Key Code: %d", super.toString(), keyCode);
    }
}
