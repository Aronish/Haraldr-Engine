package haraldr.event;

import haraldr.math.MathUtils;

public enum EventCategory
{
    CATEGORY_INPUT      (MathUtils.bit(0)),
    CATEGORY_KEYBOARD   (MathUtils.bit(1)),
    CATEGORY_MOUSE      (MathUtils.bit(2)),
    CATEGORY_WINDOW     (MathUtils.bit(3)),
    CATEGORY_APPLICATION(MathUtils.bit(4));

    public final int bitFlag;

    EventCategory(int bitFlag)
    {
        this.bitFlag = bitFlag;
    }
}
