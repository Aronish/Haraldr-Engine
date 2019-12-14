package graphics;

import main.Camera;
import world.Grid;

import java.util.List;

public interface RenderSystem
{
    void renderGridCells(Camera camera, List<Grid.GridCell> gridCells);
}
