package haraldr.graphics;

import haraldr.debug.Logger;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Batch2D
{
    private static final int MAX_QUADS = 250, MAX_INDICES = MAX_QUADS * 6, VERTEX_SIZE = 6;
    private static final Shader BATCH_SHADER_2D = Shader.create("internal_shaders/batch_shader_2d.glsl");

    private VertexArray vertexArray;
    private VertexBuffer vertexBuffer;

    private float[] vertexData = new float[MAX_QUADS * VERTEX_SIZE * 4];
    private int insertIndex, indexCount;

    public Batch2D()
    {
        VertexBufferLayout layout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT4)
        );
        vertexBuffer = new VertexBuffer(MAX_QUADS * VERTEX_SIZE * 16/*vertices * sizeof(float)*/, layout, VertexBuffer.Usage.DYNAMIC_DRAW);

        vertexArray = new VertexArray();
        vertexArray.setIndexBufferData(VertexBuffer.createQuadIndices(MAX_INDICES));
        vertexArray.setVertexBuffers(vertexBuffer);

        ResourceManager.addBatch2D(this);
    }

    public void begin()
    {
        indexCount = 0;
        insertIndex = 0;
        vertexData = new float[MAX_QUADS * VERTEX_SIZE * 4];
    }

    public void drawQuad(Vector2f position, Vector2f size, Vector4f color)
    {
        if (indexCount >= MAX_INDICES) Logger.error("Batch2D overflow!");

        float xPos = position.getX(), yPos = position.getY();
        float r = color.getX();
        float g = color.getY();
        float b = color.getZ();
        float a = color.getW();

        vertexData[insertIndex++] = xPos;   // Position
        vertexData[insertIndex++] = yPos;
        vertexData[insertIndex++] = r;      // Color
        vertexData[insertIndex++] = g;
        vertexData[insertIndex++] = b;
        vertexData[insertIndex++] = a;

        vertexData[insertIndex++] = xPos + size.getX();
        vertexData[insertIndex++] = yPos;
        vertexData[insertIndex++] = r;
        vertexData[insertIndex++] = g;
        vertexData[insertIndex++] = b;
        vertexData[insertIndex++] = a;

        vertexData[insertIndex++] = xPos + size.getX();
        vertexData[insertIndex++] = yPos + size.getY();
        vertexData[insertIndex++] = r;
        vertexData[insertIndex++] = g;
        vertexData[insertIndex++] = b;
        vertexData[insertIndex++] = a;

        vertexData[insertIndex++] = xPos;
        vertexData[insertIndex++] = yPos + size.getY();
        vertexData[insertIndex++] = r;
        vertexData[insertIndex++] = g;
        vertexData[insertIndex++] = b;
        vertexData[insertIndex++] = a;

        indexCount += 6;
    }

    public void end()
    {
        vertexBuffer.setSubData(vertexData, 0);
        vertexData = null;
    }

    public void render()
    {
        if (indexCount == 0) return;
        BATCH_SHADER_2D.bind();
        BATCH_SHADER_2D.setMatrix4f("projection", Renderer2D.pixelOrthographic);
        vertexArray.bind();
        vertexArray.drawElements(indexCount);
    }

    public void dispose()
    {
        vertexArray.delete();
    }
}
