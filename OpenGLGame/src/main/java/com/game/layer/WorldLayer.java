package com.game.layer;

import com.game.Camera;
import com.game.EventHandler;
import com.game.Window;
import com.game.event.Event;
import com.game.event.EventType;
import com.game.event.GUIToggledEvent;
import com.game.event.KeyEvent;
import com.game.event.MouseScrolledEvent;
import com.game.graphics.Renderer;
import com.game.graphics.Shader;
import com.game.world.Grid;
import com.game.gameobject.IBackground;
import com.game.gameobject.Player;
import com.game.world.World;
import com.game.gameobject.tile.Tile;
import com.game.math.Vector2f;
import com.game.math.Vector3f;
import com.game.physics.CollisionDetector;

import java.util.ArrayList;
import java.util.List;

public class WorldLayer extends Layer
{
    private boolean guiVisible;

    private World world = new World();
    private Player player = new Player(new Vector3f(0.0f, 255.0f));
    private Camera camera = new Camera();
    private EventHandler eventHandler = new EventHandler();

    private List<Grid.GridCell> visibleGridCells = new ArrayList<>();
    private List<Tile> frustumCulledObjects = new ArrayList<>();

    public WorldLayer(String name)
    {
        super(name);
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        eventHandler.processInput(camera, window.getWindowHandle(), player, world);
        player.update(camera, deltaTime);
        checkVisibleGridCells();
        doCollisions();
        player.updateMatrix();
    }

    @Override
    public void onRender()
    {
        //Renderer.multiRenderIndirectGridCells(camera, Shader.MULTI_DRAW_SHADER, visibleGridCells);
        Renderer.renderGridCells(camera, Shader.INSTANCED_SHADER, visibleGridCells);
        Renderer.render(camera, Shader.SHADER, player);
    }

    @Override
    public void onEvent(Window window, Event event)
    {
        //LOGGER.info(event.toString());
        if (event.eventType == EventType.GUI_TOGGLED)
        {
            guiVisible = ((GUIToggledEvent) event).visible;
        }
        if (event.eventType == EventType.KEY_PRESSED)
        {
            eventHandler.processKeyEvent((KeyEvent) event, window);
            event.setHandled(true);
        }
        if (event.eventType == EventType.MOUSE_SCROLLED)
        {
            eventHandler.processScrollEvent((MouseScrolledEvent) event, camera);
        }
    }

    /**
     * Main optimization algorithm. Checks chunks in the grid system in a zoom dependent area around the Player and runs them through orthographic frustum culling.
     * Frustum culling alone does wonders, but does not allow for infinite worlds. With the grid system, the world is already loaded in a data structure.
     * Array lookup is far better than looping through every object, since the time required is basically constant.
     */
    private void checkVisibleGridCells(){
        Vector2f playerGridPosition = player.getGridPosition();
        Grid grid = world.getGrid();
        visibleGridCells.clear();
        frustumCulledObjects.clear();
        for (int x = -camera.getChunkXRange(); x <= camera.getChunkXRange(); ++x){
            if (playerGridPosition.getX() + x < 0 || playerGridPosition.getX() + x > grid.getWidthI()) continue;
            for (int y = -camera.getChunkYRange(); y <= camera.getChunkYRange(); ++y){
                if (playerGridPosition.getY() + y < 0 || playerGridPosition.getY() + y > grid.getHeightI()) continue;
                visibleGridCells.add(grid.getGridCell((int) playerGridPosition.getX() + x, (int) playerGridPosition.getY() + y)); //TODO: Int cast not good for negative values.
            }
        }
        frustumCull();
    }

    /**
     * Performs orthographic frustum culling on the visible GridCells. For now, used to determine the objects to check collisions against.
     */
    private void frustumCull()
    {
        for (Grid.GridCell gridCell : visibleGridCells)
        {
            for (Tile tile : gridCell.getTiles())
            {
                camera.isInView(frustumCulledObjects, tile);
            }
        }
    }

    private void doCollisions()
    {
        for (Tile tile : frustumCulledObjects)
        {
            if (!(tile instanceof IBackground))
            {
                CollisionDetector.doCollisions(camera, tile, player);
            }
        }
    }
}
