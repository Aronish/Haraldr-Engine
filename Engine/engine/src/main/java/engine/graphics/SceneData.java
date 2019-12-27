package engine.graphics;

import engine.math.Matrix4f;

public class SceneData
{
    /////RENDERER////////////////////////
    public static final VertexArray QUAD;
    private static final float[] quadVertices = {
            0.0f,    0.0f,      0.0f, 0.0f,
            1.0f,    0.0f,      1.0f, 0.0f,
            1.0f,   -1.0f,      1.0f, 1.0f,
            0.0f,   -1.0f,      0.0f, 1.0f
    };
    //Winding order: Clockwise starting at top-left.
    private static final int[] quadIndices = {
            0, 1, 2,
            0, 2, 3
    };

    static
    {
        QUAD = new VertexArray(quadIndices);
        VertexBufferLayout quadLayout = new VertexBufferLayout
        (
            new VertexBufferElement(ShaderDataType.FLOAT2),     // Vertex Positions
            new VertexBufferElement(ShaderDataType.FLOAT2)                // Texture Coordinates
        );
        VertexBuffer quadBuffer = new VertexBuffer(quadVertices, quadLayout, false);
        QUAD.setVertexBuffer(quadBuffer);
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
