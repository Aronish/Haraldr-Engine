package com.game.event;

public class DebugScreenUpdatedEvent extends Event {

    public final int fps;

    public DebugScreenUpdatedEvent(int fps) {
        super(EventType.DEBUG_SCREEN_UPDATED, EventCategory.CATEGORY_APPLICATION);
        this.fps = fps;
    }
}
