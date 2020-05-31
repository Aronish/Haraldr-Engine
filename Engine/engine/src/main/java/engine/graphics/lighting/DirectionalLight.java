package engine.graphics.lighting;

import engine.graphics.DefaultModels;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.graphics.UniformBuffer;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class DirectionalLight extends Light implements SceneLightCompat
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
    }

    public void renderDirectionVector()
    {
        Texture.DEFAULT_WHITE.bind(0);
        Shader.LIGHT_SHADER.bind();
        Shader.LIGHT_SHADER.setMatrix4f("model", Matrix4f.createTranslate(Vector3f.add(position, direction.multiply(0.25f))).multiply(Matrix4f.createScale(new Vector3f(0.015f))));
        Shader.LIGHT_SHADER.setVector3f("u_Color", color);
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }

    @Override
    public void updateBufferData(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setData(new float[] {
                direction.getX(), direction.getY(), direction.getZ(),   0f,
                color.getX(), color.getY(), color.getZ(),               0f,
        }, offset);
    }

    @Override
    public void updateBufferDataUnsafe(@NotNull UniformBuffer lightSetup, int offset)
    {
        lightSetup.setDataUnsafe(new float[] {
                direction.getX(), direction.getY(), direction.getZ(),   0f,
                color.getX(), color.getY(), color.getZ(),               0f,
        }, offset);
    }
}