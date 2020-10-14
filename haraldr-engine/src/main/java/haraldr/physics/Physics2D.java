package haraldr.physics;

import haraldr.math.Vector2f;

public class Physics2D
{
    public static boolean pointInsideAABB(Vector2f point, Vector2f boxPosition, Vector2f boxSize)
    {
        return  point.getX() >= boxPosition.getX() && point.getX() <= boxPosition.getX() + boxSize.getX() &&
                point.getY() >= boxPosition.getY() && point.getY() <= boxPosition.getY() + boxSize.getY();
    }
}
