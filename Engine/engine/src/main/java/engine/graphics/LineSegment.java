package engine.graphics;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

public class LineSegment
{
    private Vector3f start;
    private float rotation;
    private Matrix4f model;
    private Vector4f color = new Vector4f(1.0f);
    private VertexArray vertexArray;

    public LineSegment(@NotNull Vector3f start, float length, float rotation, float thickness)
    {
        this.start = start;
        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };
        VertexBufferLayout layout = new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2));
        VertexBuffer buffer = new VertexBuffer(new float[] {}, layout, false);
        vertexArray = new VertexArray(indices);
        vertexArray.setVertexBuffer(buffer);
    }

    public void update(float deltaTime)
    {
        rotation += 5.0f * deltaTime;
        model = Matrix4f.translate(start, false).multiply(Matrix4f.rotate(rotation, false));
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