package com.game.event;

public class MouseReleasedEvent extends MouseButtonEvent {

    public MouseReleasedEvent(int button) {
        super(EventType.MOUSE_RELEASED, button);
    }
}
