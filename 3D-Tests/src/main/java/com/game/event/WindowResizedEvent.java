package com.game.event;

public class WindowResizedEvent extends Event {

    public final int width, height;

    public WindowResizedEvent(int width, int height) {
        super(EventType.WINDOW_RESIZED);
        this.width = width;
        this.height = height;
    }
}
