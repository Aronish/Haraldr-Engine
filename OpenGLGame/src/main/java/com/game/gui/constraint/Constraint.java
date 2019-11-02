package com.game.gui.constraint;

import static com.game.Application.MAIN_LOGGER;

//Defines how width, height, position, etc. is represented as vertex data.
//UUGGH
public interface Constraint
{
    default float[] createVertexData(int height, int windowWidth, int windowHeight)
    {
        MAIN_LOGGER.error("Called unimplemented method!");
        return null;
    }

    default float[] createVertexData(int width, int height, int windowWidth, int windowHeight, int padding)
    {
        MAIN_LOGGER.error("Called unimplented method!");
        return null;
    }
}
