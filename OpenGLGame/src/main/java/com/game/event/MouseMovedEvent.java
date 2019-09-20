package com.game.event;

public class MouseMovedEvent extends Event {

    public final double xPos, yPos;

    public MouseMovedEvent(double xPos, double yPos) {
        super(EventType.MOUSE_MOVED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_MOUSE);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    @Override
    public String toString() {
        return String.format("%s: XPos: %f YPos: %f", super.toString(), xPos, yPos);
    }
}
