package haraldr.graphics.lighting;

import haraldr.graphics.UniformBuffer;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class Spotlight extends DirectionalLight
{
    private float innerCutOff, outerCutOff;

    public Spotlight(Vector3f position, Vector3f direction, Vector3f color, float innerCutOff, float outerCutOff)
    {
        super(position, direction, color);
        this.innerCutOff = innerCutOff;
        this.outerCutOff = outerCutOff;
    }

    @Override
    protected void updateBufferData(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setSubData(new float[] {
                position.getX(), position.getY(), position.getZ(),      0f,
                direction.getX(), direction.getY(), direction.getZ(),   0f,
                color.getX(), color.getY(), color.getZ(),               0f,
                (float) Math.cos(Math.toRadians(innerCutOff)),
                (float) Math.cos(Math.toRadians(outerCutOff))
        }, offset);
    }

    @Override
    protected void updateBufferDataUnsafe(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setSubDataUnsafe(new float[] {
                position.getX(), position.getY(), position.getZ(),      0f,
                direction.getX(), direction.getY(), direction.getZ(),   0f,
                color.getX(), color.getY(), color.getZ(),               0f,
                (float) Math.cos(Math.toRadians(innerCutOff)),
                (float) Math.cos(Math.toRadians(outerCutOff))
        }, offset);
    }

    public float getInnerCutOff()
    {
        return innerCutOff;
    }

    public float getOuterCutOff()
    {
        return outerCutOff;
    }
}