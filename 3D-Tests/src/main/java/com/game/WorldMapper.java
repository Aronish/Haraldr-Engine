package com.game;

import com.game.debug.Logger;
import com.game.level.Grid;
import com.game.level.gameobject.tile.Tile;
import com.game.level.gameobject.tile.TileTree;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.game.Application.MAIN_LOGGER;
import static com.game.Main.fastFloor;

class WorldMapper {

    /**
     * Creates a simple map with one pixel representing 1x1 in game coordinates.
     * CURRENTLY BROKEN IN JAR FORMAT. (INCORRECT FILE PATHS).
     * @param width the width of the world in tiles.
     * @param height the height of the world in tiles.
     * @param grid the grid to map.
     * @param filePath the output file path.
     */
    static void generateWorldMap(int width, int height, Grid grid, String filePath){

        BufferedImage worldMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        File file = new File(filePath);

        Graphics graphics = worldMap.getGraphics();
        graphics.setColor(new Color(0.2f, 0.6f, 0.65f, 1.0f));
        graphics.fillRect(0, 0, worldMap.getWidth(), worldMap.getHeight());

        for (int x = 0; x < grid.getWidth(); ++x){
            for (int y = 0; y < grid.getHeight(); ++y){
                for (Tile tile : grid.getContent(x, y).getTiles()){
                    if (!(tile instanceof TileTree)){
                        Color color = null;
                        switch (tile.getGameObjectType()){
                            case GRASS:
                                color = new Color(0, 180, 50, 255);
                                break;
                            case GRASS_SNOW:
                                color = new Color(220, 220, 220, 255);
                                break;
                            case STONE:
                                color = new Color(100, 100, 100, 255);
                                break;
                            case DIRT:
                                color = new Color(100, 100, 10, 255);
                                break;
                        }
                        if (color != null){
                            worldMap.setRGB(fastFloor(tile.getPosition().getX()), height - fastFloor(tile.getPosition().getY()) - 1, color.getRGB());
                        }
                    }else{
                        Color color = new Color(0, 255, 80, 255);
                        for (int z = 0; z > -3; --z){
                            worldMap.setRGB(fastFloor(tile.getPosition().getX()), height - fastFloor(tile.getPosition().getY()) - 1 - z, color.getRGB());
                        }
                    }
                }
            }
        }
        try{
            ImageIO.write(worldMap, "png", file);
        }catch (IOException e){
            e.printStackTrace();
        }
        MAIN_LOGGER.info("Map Complete!");
    }
}
