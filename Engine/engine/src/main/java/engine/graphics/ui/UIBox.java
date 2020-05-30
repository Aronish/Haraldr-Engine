package engine.graphics.ui;

import engine.event.MouseMovedEvent;
import engine.graphics.Shader;
import engine.graphics.ShaderDataType;
import engine.graphics.VertexArray;
import engine.graphics.VertexBuffer;
import engine.graphics.VertexBufferElement;
import engine.graphics.VertexBufferLayout;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class UIBox
{
    private VertexArray boxMesh;
    private Vector2f position;
    private Vector2f size;
    private Vector4f color;

    public UIBox(@NotNull Vector2f position, @NotNull Vector2f size, Vector4f color)
    {
        this.position = position;
        this.size = size;
        this.color = color;
        boxMesh = new VertexArray();
        float[] quad = {
                position.getX(),                position.getY(),
                position.getX() + size.getX(),  position.getY(),
                position.getX() + size.getX(),  position.getY() + size.getY(),
                position.getX(),                position.getY() + size.getY()
        };
        VertexBuffer quadVertices = new VertexBuffer(
                quad,
                new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2)),
                VertexBuffer.Usage.STATIC_DRAW
        );
        boxMesh.setVertexBuffers(quadVertices);
        boxMesh.setIndexBufferData(new int[] { 0, 3, 2, 0, 2, 1 });
    }

    public void onMouseMove(@NotNull MouseMovedEvent event)
    {
        if (event.xPos >= position.getX() && event.xPos <= position.getX() + size.getX() && event.yPos >= position.getY() && event.yPos <= position.getY() + size.getY())
        {
            this.color.set(0.3f, 0.8f, 0.2f, 1f);
        }
        else
        {
            this.color.set(0.8f, 0.2f, 0.3f, 1f);
        }
    }

    public void render()
    {
        glDisable(GL_DEPTH_TEST);
        Shader.UI.bind();
        Shader.UI.setMatrix4f("projection", Matrix4f.pixelOrthographic);
        Shader.UI.setVector4f("u_Color", color);
        boxMesh.bind();
        boxMesh.drawElements();
        glEnable(GL_DEPTH_TEST);
    }

    public void delete()
    {
        boxMesh.delete();
    }
}
