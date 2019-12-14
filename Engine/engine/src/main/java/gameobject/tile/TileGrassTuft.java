package gameobject.tile;

import gameobject.GameObject;
import gameobject.IBackground;
import math.Vector3f;

public class TileGrassTuft extends Tile implements IBackground {
    public TileGrassTuft(Vector3f position) {
        super(position, GameObject.GRASS_TUFT);
    }
}
