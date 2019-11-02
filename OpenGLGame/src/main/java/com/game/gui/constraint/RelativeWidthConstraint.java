package com.game.gui.constraint;

import static com.game.Application.MAIN_LOGGER;

public class RelativeWidthConstraint implements Constraint
{
    private float relativeWidth;
    private AlignmentSide alignmentSide; //Top/Bottom ATM.
    private float offsetX, offsetY;
    private float oppositeOffsetX, oppositeOffsetY;

    public RelativeWidthConstraint(float relativeWidth, AlignmentSide alignmentSide, float offsetX, float offsetY)
    {
        this.relativeWidth = relativeWidth;
        this.alignmentSide = alignmentSide;

        if (offsetX > 100) this.offsetX = 100;
        else if (offsetX < 0) this.offsetX = 0;
        else this.offsetX = offsetX;
        this.oppositeOffsetX = 100 - this.offsetX;

        if (offsetY > 100) this.offsetY = 100;
        else if (offsetY < 0) this.offsetY = 0;
        else this.offsetY = offsetY;
        this.oppositeOffsetY = 100 - this.offsetY;
    }

    @Override
    public float[] createVertexData(int height, int windowWidth, int windowHeight)
    {
        switch (alignmentSide)
        {
            case LEFT:
                return new float[]
                {
                        windowWidth * offsetX,                                  windowHeight * offsetY,
                        windowWidth * offsetX + relativeWidth * windowWidth,    windowHeight * offsetY,
                        windowWidth * offsetX + relativeWidth * windowWidth,    windowHeight * offsetY + height,
                        windowWidth * offsetX,                                  windowHeight * offsetY + height
                };
            default:
                MAIN_LOGGER.error("Unknown alignment constraint direction!");
                return null;
        }
    }
}
