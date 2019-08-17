package com.game.gameobject.tile;

import com.game.gameobject.GameObject;
import com.game.gameobject.IBackground;
import com.game.math.Vector3f;

public class TileGrassTuft extends Tile implements IBackground {
    public TileGrassTuft(Vector3f position) {
        super(position, GameObject.GRASS_TUFT);
    }
}
