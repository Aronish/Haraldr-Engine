package com.game.graphics;

import com.game.physics.AABB;

public interface IModel
{
    IVertexArray getVertexArray();
    AABB getAABB();
    void dispose();
    void printType();
}
