package engine.graphics;

import engine.math.Matrix4f;

public class SceneData2D
{
    /////RENDERER////////////////////////
    public static final VertexArray QUAD;

    static
    {
        float[] quadVertices = {
                0.0f,    0.0f,      0.0f, 0.0f,
                1.0f,    0.0f,      1.0f, 0.0f,
                1.0f,   -1.0f,      1.0f, 1.0f,
                0.0f,   -1.0f,      0.0f, 1.0f
        };
        //Winding order: Clockwise starting at top-left.
        int[] quadIndices = {
                0, 1, 2,
                0, 2, 3
        };
        VertexBufferLayout quadLayout = new VertexBufferLayout
        (
            new VertexBufferElement(ShaderDataType.FLOAT2),     // Vertex Positions
            new VertexBufferElement(ShaderDataType.FLOAT2)      // Texture Coordinates
        );
        VertexBuffer quadBuffer = new VertexBuffer(quadVertices, quadLayout, false);
        QUAD = new VertexArray();
        QUAD.setVertexBuffers(quadBuffer);
        QUAD.setIndexBuffer(quadIndices);
    }

    public static final Texture defaultTexture = new Texture(1, 1, new int[] { -1 } ); // -1 means all channels at max.

    /////SCENE///////////////////////////////////
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