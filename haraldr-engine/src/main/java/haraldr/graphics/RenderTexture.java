package haraldr.graphics;

import haraldr.math.Vector2f;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RenderTexture
{
    private VertexArray quad = new VertexArray();
    private VertexBuffer quadData;
    private Framebuffer framebuffer = new Framebuffer();

    private Vector2f position, size;

    public RenderTexture(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;
        quadData = new VertexBuffer(
                createQuadData(position, size),
                new VertexBufferLayout(
                        new VertexBufferElement(ShaderDataType.FLOAT2),
                        new VertexBufferElement(ShaderDataType.FLOAT2)
                ),
                VertexBuffer.Usage.DYNAMIC_DRAW
        );
        quad.setVertexBuffers(quadData);
        quad.setIndexBufferData(new int[] { 0, 1, 2, 0, 2, 3 });

        framebuffer.setColorAttachment(new Framebuffer.ColorAttachment((int)size.getX(), (int)size.getY(), Framebuffer.ColorAttachment.Format.RGBA16F));
        framebuffer.setDepthBuffer(new Framebuffer.RenderBuffer((int)size.getX(), (int)size.getY(), Framebuffer.RenderBuffer.Format.DEPTH_24_STENCIL_8));
    }

    public void setSize(float width, float height)
    {
        size.set(width, height);
        quadData.setSubData(createQuadData(position, size), 0);
        framebuffer.setSize((int)width, (int)height);
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
        quadData.setSubData(createQuadData(position, size), 0);
    }

    public void delete()
    {
        framebuffer.delete();
        quad.delete();
    }

    public Framebuffer getFramebuffer()
    {
        return framebuffer;
    }

    public VertexArray getQuad()
    {
        return quad;
    }

    public Vector2f getSize()
    {
        return size;
    }

    @Contract("_, _ -> new")
    private static @NotNull float[] createQuadData(@NotNull Vector2f position, @NotNull Vector2f size)
    {
        return new float[] {
                position.getX(),                position.getY() + size.getY(),  0f, 0f,
                position.getX() + size.getX(),  position.getY() + size.getY(),  1f, 0f,
                position.getX() + size.getX(),  position.getY(),                1f, 1f,
                position.getX(),                position.getY(),                0f, 1f
        };
    }
}