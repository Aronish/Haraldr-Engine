package haraldr.graphics.lighting;

import haraldr.graphics.UniformBuffer;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PointLight extends Light
{
    private float constant, linear, quadratic;

    public PointLight(Vector3f position, Vector3f color)
    {
        this(position, color, 1f, 0.045f, 0.0075f);
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
    protected void updateBufferData(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setSubData(new float[] {
                position.getX(), position.getY(), position.getZ(),  0f,
                color.getX(), color.getY(), color.getZ(),           0f,
                constant, linear, quadratic
        }, offset);
    }

    @Override
    protected void updateBufferDataUnsafe(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setSubDataUnsafe(new float[] {
                position.getX(), position.getY(), position.getZ(),  0f,
                color.getX(), color.getY(), color.getZ(),           0f,
                constant, linear, quadratic
        }, offset);
    }

    public float getConstant()
    {
        return constant;
    }

    public float getLinear()
    {
        return linear;
    }

    public float getQuadratic()
    {
        return quadratic;
    }
}