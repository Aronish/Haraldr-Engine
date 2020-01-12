package engine.graphics;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Line
{
    private VertexArray vertexArray;
    private float length, slopeAngle;
    private Vector4f color = new Vector4f(1.0f);
    private Matrix4f rotation;

    public Line(Vector3f start, Vector3f end, float thickness)
    {
        length = Vector3f.subtract(start, end).length();
        slopeAngle = (float) Math.tan(Vector3f.slope(start, end));
        rotation = Matrix4f.rotate(slopeAngle, false);
        float[] vertices = {
                start.getX(),           start.getY() + thickness,
                start.getX() + length,  start.getY() + thickness,
                start.getX() + length,  start.getY() - thickness,
                start.getX(),           start.getY() - thickness,
        };
        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };
        VertexBufferLayout layout = new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2));
        VertexBuffer buffer = new VertexBuffer(vertices, layout, false);
        vertexArray = new VertexArray(indices);
        vertexArray.setVertexBuffer(buffer);
    }

    public Line(@NotNull Vector3f position, float slopeAngle, float length, float thickness)
    {
        this.length = length;
        this.slopeAngle = slopeAngle;
        rotation = Matrix4f.rotate(slopeAngle, false);
        float[] vertices = {
                position.getX(),           position.getY() + thickness,
                position.getX() + length,  position.getY() + thickness,
                position.getX() + length,  position.getY() - thickness,
                position.getX(),           position.getY() - thickness,
        };
        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };
        VertexBufferLayout layout = new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2));
        VertexBuffer buffer = new VertexBuffer(vertices, layout, false);
        vertexArray = new VertexArray(indices);
        vertexArray.setVertexBuffer(buffer);
    }

    public void setRotation(float rotation)
    {
        slopeAngle = rotation;
        this.rotation = Matrix4f.rotate(rotation, false);
    }

    public void update(float deltaTime)
    {
    }

    public void draw()
    {
        SceneData.defaultTexture.bind();
        Shader.DEFAULT.setMatrix4f(rotation, "model");
        Shader.DEFAULT.setMatrix4f(Renderer2D.sceneData.getViewMatrix(), "view");
        Shader.DEFAULT.setMatrix4f(Matrix4f.orthographic, "projection");
        Shader.DEFAULT.setVector4f(color, "color");

        vertexArray.bind();
        vertexArray.draw();
    }
}
