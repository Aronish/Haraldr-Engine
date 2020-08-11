package haraldr.graphics;

import haraldr.math.Matrix4f;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Renderer2D
{
    private static final int MAX_QUADS = 1000, MAX_INDICES = MAX_QUADS * 6, VERTEX_SIZE = 6;
    private static final Shader SHADER = Shader.create("default_shaders/renderer2d.glsl");

    private static VertexArray quadVertexArray = new VertexArray();
    private static VertexBuffer quadVertexBuffer;
    private static float[] vertexData = new float[MAX_QUADS * VERTEX_SIZE * 4];
    private static int insertIndex;

    private static int indexCount;

    static
    {
        quadVertexArray.setIndexBufferData(VertexBuffer.createQuadIndices(MAX_INDICES));
        VertexBufferLayout layout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT4)
        );
        quadVertexBuffer = new VertexBuffer(MAX_QUADS * VERTEX_SIZE * 16/*vertices * sizeof(float)*/, layout, VertexBuffer.Usage.DYNAMIC_DRAW);
        quadVertexArray.setVertexBuffers(quadVertexBuffer);
    }

    public static void begin()
    {
        indexCount = 0;
        insertIndex = 0;
        SHADER.bind();
        SHADER.setMatrix4f("projection", Matrix4f.pixelOrthographic);
        quadVertexBuffer.bind();
    }

    public static void end()
    {
        if (indexCount == 0) return;
        quadVertexBuffer.setSubDataUnsafe(vertexData);
        quadVertexArray.bind();
        quadVertexArray.drawElements(indexCount);
    }

    public static void reset()
    {
        indexCount = 0;
        insertIndex = 0;
    }

    public static void drawQuad(Vector2f position, Vector2f size, Vector4f color)
    {
        if (indexCount >= MAX_INDICES)
        {
            end();
            reset();
        }

        float xPos = position.getX(), yPos = position.getY();
        float r = color.getX();
        float g = color.getY();
        float b = color.getZ();
        float a = color.getW();
        
        vertexData[insertIndex++] = xPos;
        vertexData[insertIndex++] = yPos;
        vertexData[insertIndex++] = r;
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

    public static void dispose()
    {
        quadVertexArray.delete();
    }
}