package engine.graphics;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

public class LineSegment
{
    private Vector3f start;
    private float rotation;
    private float thickness;
    private float length;
    private Matrix4f model;
    private Vector4f color = new Vector4f(1.0f);
    private VertexArray vertexArray;
    private VertexBuffer buffer;

    public LineSegment(@NotNull Vector3f start, float length, float rotation, float thickness)
    {
        this.start = start;
        this.thickness = thickness;
        this.length = length;
        model = Matrix4f.translate(start, false);
        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };
        VertexBufferLayout layout = new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2));
        buffer = new VertexBuffer(32, layout, false);
        vertexArray = new VertexArray(indices);
        vertexArray.setVertexBuffer(buffer);
        rotate();
    }

    private void rotate()
    {
        Matrix4f rotationMatrix = Matrix4f.rotate(rotation, false);
        Vector3f one    = rotationMatrix.multiply(new Vector3f(0f, thickness));
        Vector3f two    = rotationMatrix.multiply(new Vector3f(0f, -thickness));
        Vector3f three  = rotationMatrix.multiply(new Vector3f(length, -thickness));
        Vector3f q      = Vector3f.add(three, new Vector3f(0f, (2 * thickness) / (float) Math.cos(Math.toRadians(rotation))));
        float[] data = {
                one.getX(), one.getY(),
                two.getX(), two.getY(),
                three.getX(), three.getY(),
                q.getX(), q.getY()
        };
        buffer.setData(data);
    }

    public void update(float deltaTime)
    {
        rotation += 15.0f * deltaTime;
        rotate();
    }

    public void draw()
    {
        SceneData.defaultTexture.bind();
        Shader.DEFAULT.setMatrix4f(model, "model");
        Shader.DEFAULT.setMatrix4f(Renderer2D.sceneData.getViewMatrix(), "view");
        Shader.DEFAULT.setMatrix4f(Matrix4f.orthographic, "projection");
        Shader.DEFAULT.setVector4f(color, "color");

        vertexArray.bind();
        vertexArray.draw();
    }
}