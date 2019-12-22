package oldgame.graphics;

import engine.main.OrthograhpicCamera;
import oldgame.world.Grid;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RenderSystem
{
    void renderGridCells(@NotNull OrthograhpicCamera camera, List<Grid.GridCell> gridCells);
}
