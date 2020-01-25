package engine.graphics;

import engine.math.Matrix4f;

public class SceneData3D
{
    public static final VertexArray CUBE;
    public static final VertexArray TRI;

    static
    {
        float[] cubeVertices = {
                -1, -1, -1,     1f, 0f, 0f, 1f,
                 1, -1, -1,     0f, 1f, 0f, 1f,
                 1,  1, -1,     0f, 0f, 1f, 1f,
                -1,  1, -1,     0f, 1f, 0f, 1f,
                -1, -1,  1,     1f, 0f, 0f, 1f,
                 1, -1,  1,     0f, 1f, 0f, 1f,
                 1,  1,  1,     0f, 0f, 1f, 1f,
                -1,  1,  1,     0f, 1f, 0f, 1f
        };
        int[] cubeIndices = {
                0, 1, 3, 3, 1, 2,
                1, 5, 2, 2, 5, 6,
                5, 4, 6, 6, 4, 7,
                4, 0, 7, 7, 0, 3,
                3, 2, 7, 7, 2, 6,
                4, 5, 0, 0, 5, 1
        };
        CUBE = new VertexArray(cubeIndices);
        VertexBufferLayout cubeLayout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT3),
                new VertexBufferElement(ShaderDataType.FLOAT4)
        );
        VertexBuffer cubeBuffer = new VertexBuffer(cubeVertices, cubeLayout, false);
        CUBE.setVertexBuffer(cubeBuffer);

        float[] triVertices = {
                -1f, -1f, 0f,
                1f, -1f, 0f,
                0f, 1f, 0f
        };
        int[] triIndices = {
                0, 1, 2
        };
        TRI = new VertexArray(triIndices);
        VertexBufferLayout layout = new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT3));
        VertexBuffer triBuffer = new VertexBuffer(triVertices, layout, false);
        TRI.setVertexBuffer(triBuffer);
    }

    ///// SCENE //////////
    private Matrix4f viewMatrix = new Matrix4f();

    public void setViewMatrix(Matrix4f viewMatrix)
    {
        this.viewMatrix = viewMatrix;
    }

    public Matrix4f getViewMatrix()
    {
        return viewMatrix;
    }
}
