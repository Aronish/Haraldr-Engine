package haraldr.dockspace;

import org.jetbrains.annotations.Contract;

public enum DockPosition
{
    NONE, LEFT, RIGHT, TOP, BOTTOM, CENTER;

    private DockPosition opposite;

    static
    {
        NONE.opposite = NONE;
        LEFT.opposite = RIGHT;
        RIGHT.opposite = LEFT;
        TOP.opposite = BOTTOM;
        BOTTOM.opposite = TOP;
        CENTER.opposite = CENTER;
    }

    @Contract(pure = true)
    public DockPosition getOpposite()
    {
        return opposite;
    }
}
