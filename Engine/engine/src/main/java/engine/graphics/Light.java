package engine.graphics;

import engine.math.Vector3f;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Light
{
    private Vector3f position;
    private Vector3f color;

    public Light()
    {
        this(new Vector3f(), new Vector3f(1f));
    }

    public Light(Vector3f position)
    {
        this(position, new Vector3f(1f));
    }

    public Light(Vector3f position, Vector3f color)
    {
        this.position = position;
        this.color = color;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public void setColor(Vector3f color)
    {
        this.color = color;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public Vector3f getColor()
    {
        return color;
    }
}
