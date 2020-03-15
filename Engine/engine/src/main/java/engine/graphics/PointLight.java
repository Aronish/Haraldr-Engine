package engine.graphics;

import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PointLight extends Light implements SceneLightCompat
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
    public void updateBufferData(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setData(new float[] {
                position.getX(), position.getY(), position.getZ(),  0f,
                color.getX(), color.getY(), color.getZ(),
                constant, linear, quadratic
        }, offset);
    }

    @Override
    public void updateBufferDataUnsafe(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setDataUnsafe(new float[] {
                position.getX(), position.getY(), position.getZ(),  0f,
                color.getX(), color.getY(), color.getZ(),
                constant, linear, quadratic
        }, offset);
    }
}