package com.game.event;

public class MousePressedEvent extends MouseButtonEvent {

    public MousePressedEvent(int button) {
        super(button, EventType.MOUSE_PRESSED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_MOUSE);
    }
}
