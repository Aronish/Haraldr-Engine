package com.game.event;

public class MouseReleasedEvent extends MouseButtonEvent
{
    public MouseReleasedEvent(int button)
    {
        super(button, EventType.MOUSE_RELEASED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_MOUSE);
    }
}
