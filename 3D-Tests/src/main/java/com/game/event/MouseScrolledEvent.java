package com.game.event;

public class MouseScrolledEvent extends Event {

    public final int xOffset, yOffset;

    public MouseScrolledEvent(int xOffset, int yOffset) {
        super(EventType.MOUSE_SCROLLED);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
}
