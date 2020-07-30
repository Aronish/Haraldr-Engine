package haraldr.graphics.lighting;

import haraldr.graphics.DefaultModels;
import haraldr.graphics.ResourceManager;
import haraldr.graphics.Shader;
import haraldr.graphics.Texture;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;

@SuppressWarnings("unused")
public abstract class Light
{
    protected static final Shader LIGHT_SHADER = Shader.create("default_shaders/unlit.glsl");

    protected Vector3f position;
    protected Vector3f color;

    public Light(Vector3f position, Vector3f color)
    {
        this.position = position;
        this.color = color;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public void addPosition(Vector3f position)
    {
        this.position.add(position);
    }

    public void setColor(Vector3f color)
    {
        this.color = color;
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