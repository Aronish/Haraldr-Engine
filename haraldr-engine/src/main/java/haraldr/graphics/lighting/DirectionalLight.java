package haraldr.graphics.lighting;

import haraldr.graphics.DefaultModels;
import haraldr.graphics.Shader;
import haraldr.graphics.Texture;
import haraldr.graphics.UniformBuffer;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class DirectionalLight extends Light
{
    protected Vector3f direction;

    public DirectionalLight(Vector3f position, Vector3f direction, Vector3f color)
    {
        super(position, color);
        this.direction = Vector3f.normalize(direction);
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
        updateBufferData(sceneLightsBuffer, bufferOffset);
    }

    public void renderDirectionVector()
    {
        Texture.DEFAULT_WHITE.bind(0);
        LIGHT_SHADER.bind();
        LIGHT_SHADER.setMatrix4f("model", Matrix4f.identity().translate(Vector3f.add(position, Vector3f.multiply(direction, 0.25f))).scale(new Vector3f(0.015f)));
        LIGHT_SHADER.setVector3f("u_Color", color);
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }

    @Override
    protected void updateBufferData(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setData(new float[] {
                direction.getX(), direction.getY(), direction.getZ(),   0f,
                color.getX(), color.getY(), color.getZ(),               0f,
        }, offset);
    }

    @Override
    protected void updateBufferDataUnsafe(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setDataUnsafe(new float[] {
                direction.getX(), direction.getY(), direction.getZ(),   0f,
                color.getX(), color.getY(), color.getZ(),               0f,
        }, offset);
    }
}