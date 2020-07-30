package oldgame.gameobject.tile;

import oldgame.gameobject.GameObject;
import oldgame.gameobject.IBackground;
import engine.math.Vector3f;

public class TileGrassTuft extends Tile implements IBackground {
    public TileGrassTuft(Vector3f position) {
        super(position, GameObject.GRASS_TUFT);
    }
}
