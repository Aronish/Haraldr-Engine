package engine.graphics.lighting;

import engine.graphics.DefaultModels;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.math.Matrix4f;
import engine.math.Vector3f;

@SuppressWarnings("unused")
public abstract class Light
{
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
        Shader.LIGHT_SHADER.bind();
        Shader.LIGHT_SHADER.setMatrix4f("model", Matrix4f.createTranslate(position).scale(new Vector3f(0.02f)));
        Shader.LIGHT_SHADER.setVector3f("u_Color", color);
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