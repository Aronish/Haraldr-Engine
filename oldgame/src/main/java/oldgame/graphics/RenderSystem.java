package oldgame.graphics;

import haraldr.scene.OrthographicCamera;
import oldgame.world.Grid;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RenderSystem
{
    void renderGridCells(@NotNull OrthographicCamera camera, List<Grid.GridCell> gridCells);
}
