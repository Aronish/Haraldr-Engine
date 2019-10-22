package com.game.gui.constraint;

import static com.game.Application.MAIN_LOGGER;

public class AlignedConstraint extends Constraint
{
    private AlignmentSide alignmentSide;
    private int padding;

    public AlignedConstraint(AlignmentSide alignmentSide, int padding)
    {
        this.alignmentSide = alignmentSide;
        this.padding = padding;
    }

    @Override
    public float[] createVertexData(int width, int height, int windowWidth, int windowHeight)
    {
        switch (alignmentSide)
        {
            case LEFT:
                return new float[]
                {
                        padding,    padding,
                        width,      padding,
                        width,      windowHeight - padding,
                        padding,    windowHeight - padding
                };
            case RIGHT:
                return new float[]
                {
                        windowWidth,            0,
                        windowWidth - width,    0,
                        windowWidth - width,    windowHeight,
                        windowWidth,            windowHeight
                };
            case TOP:
                return new float[]
                {
                        0,              0,
                        windowWidth,    0,
                        windowWidth,    height,
                        0,              height
                };
            case BOTTOM:
                return new float[]
                {
                        0,              windowHeight,
                        windowWidth,    windowHeight,
                        windowWidth,    windowHeight - height,
                        0,              windowHeight - height
                };
            default:
                MAIN_LOGGER.error("Unknown alignment constraint direction!");
                return null;
        }
    }
}