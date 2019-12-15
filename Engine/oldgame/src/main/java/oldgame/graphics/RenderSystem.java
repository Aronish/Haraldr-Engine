package oldgame.graphics;

import engine.main.Camera;
import oldgame.world.Grid;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RenderSystem
{
    void renderGridCells(@NotNull Camera camera, List<Grid.GridCell> gridCells);
}
