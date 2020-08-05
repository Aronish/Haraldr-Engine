package haraldr.graphics.lighting;

import haraldr.graphics.DefaultModels;
import haraldr.graphics.Shader;
import haraldr.graphics.Texture;
import haraldr.graphics.UniformBuffer;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class Light
{
    protected static final Shader LIGHT_SHADER = Shader.create("default_shaders/unlit.glsl");

    protected Vector3f position;
    protected Vector3f color;

    protected UniformBuffer sceneLightsBuffer;
    protected int bufferOffset;

    public Light(Vector3f position, Vector3f color)
    {
        this.position = position;
        this.color = color;
    }

    public void setSceneLightsBuffer(UniformBuffer sceneLightsBuffer)
    {
        this.sceneLightsBuffer = sceneLightsBuffer;
        updateBufferData(sceneLightsBuffer, bufferOffset);
    }

    public void setBufferOffset(int bufferOffset)
    {
        this.bufferOffset = bufferOffset;
    }

    protected abstract void updateBufferData(@NotNull UniformBuffer lightSetup, int offset);
    protected abstract void updateBufferDataUnsafe(@NotNull UniformBuffer lightSetup, int offset);

    public void setPosition(Vector3f position)
    {
        this.position = position;
        updateBufferData(sceneLightsBuffer, bufferOffset);
    }

    public void addPosition(Vector3f position)
    {
        this.position.add(position);
        updateBufferData(sceneLightsBuffer, bufferOffset);
    }

    public void setColor(Vector3f color)
    {
        this.color = color;
        updateBufferData(sceneLightsBuffer, bufferOffset);
    }

    public void render()
    {
        Texture.DEFAULT_WHITE.bind(0);
        LIGHT_SHADER.bind();
        LIGHT_SHADER.setMatrix4f("model", Matrix4f.identity().translate(position).scale(new Vector3f(0.02f)));
        LIGHT_SHADER.setVector3f("u_Color", color);
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
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