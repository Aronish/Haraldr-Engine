package com.game.event;

public abstract class MouseButtonEvent extends Event {

    public final int button;

    public MouseButtonEvent(EventType eventType, int button) {
        super(eventType);
        this.button = button;
    }
}
