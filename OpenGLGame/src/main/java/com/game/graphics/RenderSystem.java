package com.game.graphics;

import com.game.Camera;
import com.game.world.Grid;

import java.util.List;

public interface RenderSystem
{
    void renderGridCells(Camera camera, List<Grid.GridCell> gridCells);
}
