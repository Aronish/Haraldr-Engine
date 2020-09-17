package haraldr.event;

import haraldr.math.MathUtils;
import org.jetbrains.annotations.Contract;

public enum EventCategory
{
    CATEGORY_INPUT          (MathUtils.bit(0)),
    CATEGORY_KEYBOARD       (MathUtils.bit(1)),
    CATEGORY_MOUSE          (MathUtils.bit(2)),
        CATEGORY_MOUSE_BUTTON   (MathUtils.bit(21)),
    CATEGORY_WINDOW         (MathUtils.bit(3)),
    CATEGORY_APPLICATION    (MathUtils.bit(4));

    public final int bitFlag;

    @Contract(pure = true)
    EventCategory(int bitFlag)
    {
        this.bitFlag = bitFlag;
    }
}
