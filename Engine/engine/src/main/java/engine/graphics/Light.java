package engine.graphics;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Light
{
    private static final Shader LIGHT_SHADER = new Shader("default_shaders/diffuse.vert", "default_shaders/simpleColor.frag");

    private Vector3f position;
    private Vector3f color;

    public Light()
    {
        this(new Vector3f(), new Vector3f(1f));
    }

    public Light(Vector3f position)
    {
        this(position, new Vector3f(1f));
    }

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

    public void render(@NotNull ForwardRenderer renderer)
    {
        Texture.DEFAULT_TEXTURE.bind(0);
        LIGHT_SHADER.bind();
        LIGHT_SHADER.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(0.0625f))), "model");
        LIGHT_SHADER.setVector3f(color, "color");
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
