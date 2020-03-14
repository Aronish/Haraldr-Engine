package engine.graphics;

import engine.math.Vector3f;

public class PointLight extends Light
{
    private float constant, linear, quadratic;

    //TEMPORARY
    public PointLight(Vector3f position, Vector3f color)
    {
        super(position, color);
    }

    public PointLight(Vector3f position, Vector3f color, float linear, float quadratic)
    {
        this(position, color, 1f, linear, quadratic);
    }

    public PointLight(Vector3f position, Vector3f color, float constant, float linear, float quadratic)
    {
        super(position, color);
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
    }

    @Override
    public void addToBuffer()
    {
        //TODO: Add sensible implementation.
    }
}