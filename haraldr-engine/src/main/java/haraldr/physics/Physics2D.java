package haraldr.physics;

import haraldr.debug.Logger;
import haraldr.math.Vector2f;

public class Physics2D
{
    public static boolean pointInsideAABB(Vector2f point, Vector2f boxPosition, Vector2f boxSize)
    {
        return  point.getX() >= boxPosition.getX() && point.getX() <= boxPosition.getX() + boxSize.getX() &&
                point.getY() >= boxPosition.getY() && point.getY() <= boxPosition.getY() + boxSize.getY();
        //boolean first = point.getX() >= boxPosition.getX();
        //boolean second = point.getX() <= boxPosition.getX() + boxSize.getX();
        //boolean third = point.getY() >= boxPosition.getY();
        //boolean fourth = point.getY() <= boxPosition.getY() + boxSize.getY();
        //Logger.info(first, second, third, fourth);
        //return first && second && third && fourth;
    }
}
