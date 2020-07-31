package haraldr.graphics;

import haraldr.main.ArrayUtils;
import haraldr.math.Matrix4f;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Renderer2D
{
    private static final int MAX_QUADS = 1000, MAX_INDICES = MAX_QUADS * 6, VERTEX_SIZE = 9, MAX_TEXTURES = 32;
    private static final Shader SHADER = Shader.create("default_shaders/renderer2d.glsl");

    private static VertexArray quadVertexArray = new VertexArray();
    private static VertexBuffer quadVertexBuffer;
    private static List<Float> vertexData = new ArrayList<>(MAX_QUADS * VERTEX_SIZE * 4); //TODO: Primitive array?

    private static List<Texture> textures = new ArrayList<>(MAX_TEXTURES);
    private static int textureIndex = 1;

    private static int indexCount;

    static
    {
        quadVertexArray.setIndexBufferData(VertexBuffer.createQuadIndices(MAX_INDICES));
        VertexBufferLayout layout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT4),
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT)
        );
        quadVertexBuffer = new VertexBuffer(MAX_QUADS * VERTEX_SIZE * 16/*vertices * sizeof(float)*/, layout, VertexBuffer.Usage.DYNAMIC_DRAW);
        quadVertexArray.setVertexBuffers(quadVertexBuffer);

        textures.add(Texture.DEFAULT_WHITE);
    }

    public static void begin()
    {
        indexCount = 0;
        textureIndex = 1;
        vertexData.clear();
    }

    public static void end()
    {
        if (indexCount == 0) return;
        SHADER.bind();
        SHADER.setMatrix4f("projection", Matrix4f.pixelOrthographic);
        for (int i = 0; i < textureIndex; ++i)
        {
            textures.get(i).bind(i);
        }
        quadVertexBuffer.setData(ArrayUtils.toPrimitiveArrayF(vertexData));
        quadVertexArray.bind();
        quadVertexArray.drawElementsInstanced(indexCount);
    }

    public static void drawQuad(Vector2f position, Vector2f size, Vector4f color)
    {
        if (indexCount >= MAX_INDICES)
        {
            end();
            begin();
        }
        vertexData.add(position.getX());
        vertexData.add(position.getY());
        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());
        vertexData.add(0f);
        vertexData.add(0f);
        vertexData.add(0f);

        vertexData.add(position.getX() + size.getX());
        vertexData.add(position.getY());
        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());
        vertexData.add(1f);
        vertexData.add(0f);
        vertexData.add(0f);

        vertexData.add(position.getX() + size.getX());
        vertexData.add(position.getY() + size.getY());
        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());
        vertexData.add(1f);
        vertexData.add(1f);
        vertexData.add(0f);

        vertexData.add(position.getX());
        vertexData.add(position.getY() + size.getY());
        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());
        vertexData.add(0f);
        vertexData.add(1f);
        vertexData.add(0f);

        indexCount += 6;
    }

    public static void drawQuad(Vector2f position, Vector2f size, Texture texture)
    {
        if (indexCount >= MAX_INDICES)
        {
            end();
            begin();
        }
        // Find texture or add it.
        float index = textures.indexOf(texture);
        if (index == -1)
        {
            if (textureIndex >= MAX_TEXTURES)
            {
                end();
                begin();
            }
            index = textureIndex;
            textures.add((int) index, texture);
            ++textureIndex;
        }

        Vector4f color = new Vector4f(1f);

        vertexData.add(position.getX());
        vertexData.add(position.getY());
        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());
        vertexData.add(0f);
        vertexData.add(0f);
        vertexData.add(index);

        vertexData.add(position.getX() + size.getX());
        vertexData.add(position.getY());
        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());
        vertexData.add(1f);
        vertexData.add(0f);
        vertexData.add(index);

        vertexData.add(position.getX() + size.getX());
        vertexData.add(position.getY() + size.getY());
        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());
        vertexData.add(1f);
        vertexData.add(1f);
        vertexData.add(index);

        vertexData.add(position.getX());
        vertexData.add(position.getY() + size.getY());
        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());
        vertexData.add(0f);
        vertexData.add(1f);
        vertexData.add(index);

        indexCount += 6;
    }

    public static void dispose()
    {
        quadVertexArray.delete();
    }
}