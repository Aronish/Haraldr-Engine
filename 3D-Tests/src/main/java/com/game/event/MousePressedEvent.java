package com.game.event;

public class MousePressedEvent extends MouseButtonEvent {

    public MousePressedEvent(int button) {
        super(EventType.MOUSE_PRESSED, button);
    }
}
