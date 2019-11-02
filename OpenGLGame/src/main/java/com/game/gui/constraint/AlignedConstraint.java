package com.game.gui.constraint;

import static com.game.Application.MAIN_LOGGER;

public class AlignedConstraint implements Constraint
{
    private AlignmentSide alignmentSide;

    public AlignedConstraint(AlignmentSide alignmentSide)
    {
        this.alignmentSide = alignmentSide;
    }

    @Override
    public float[] createVertexData(int width, int height, int windowWidth, int windowHeight, int padding)
    {
        switch (alignmentSide)
        {
            case LEFT:
                return new float[]
                {
                        padding,            padding,
                        width + padding,    padding,
                        width + padding,    windowHeight - padding,
                        padding,            windowHeight - padding
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