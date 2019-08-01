package com.game.event;

public enum EventCategory {

    CATEGORY_INPUT(bit(0)),
        CATEGORY_KEYBOARD(bit(1)), CATEGORY_MOUSE(bit(2)),
    CATEGORY_WINDOW(bit(3));

    public final int bitFlag;

    EventCategory(int bitFlag){
        this.bitFlag = bitFlag;
    }

    public static int bit(int x){
        return 1 << x;
    }

}
