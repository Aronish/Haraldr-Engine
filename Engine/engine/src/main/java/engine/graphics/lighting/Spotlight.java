package engine.graphics.lighting;

import engine.graphics.UniformBuffer;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class Spotlight extends DirectionalLight implements SceneLightCompat
{
    private float innerCutOff, outerCutOff;

    public Spotlight(Vector3f position, Vector3f direction, Vector3f color, float innerCutOff, float outerCutOff)
    {
        super(position, direction, color);
        this.innerCutOff = innerCutOff;
        this.outerCutOff = outerCutOff;
    }

    @Override
    public void updateBufferData(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setData(new float[] {
                position.getX(), position.getY(), position.getZ(),      0f,
                direction.getX(), direction.getY(), direction.getZ(),   0f,
                color.getX(), color.getY(), color.getZ(),               0f,
                (float) Math.cos(Math.toRadians(innerCutOff)),
                (float) Math.cos(Math.toRadians(outerCutOff))
        }, offset);
    }

    @Override
    public void updateBufferDataUnsafe(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setDataUnsafe(new float[] {
                position.getX(), position.getY(), position.getZ(),      0f,
                direction.getX(), direction.getY(), direction.getZ(),   0f,
                color.getX(), color.getY(), color.getZ(),               0f,
                (float) Math.cos(Math.toRadians(innerCutOff)),
                (float) Math.cos(Math.toRadians(outerCutOff))
        }, offset);
    }
}