package engine.graphics.ui;

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

@SuppressWarnings("unused")
public class TextLabel
{
    private Font font;
    private String text;
    private Vector4f color;
    private Vector2f position;
    private VertexArray textMesh;
    private VertexBuffer textMeshBuffer;

    public TextLabel(Vector2f position, @NotNull Font font, Vector4f color, @NotNull String text)
    {
        this.font = font;
        this.text = text;
        this.color = color;
        this.position = position;
        VertexBufferLayout layout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT2)
        );
        textMeshBuffer = new VertexBuffer(text.length() * 64, layout, VertexBuffer.Usage.DYNAMIC_DRAW);
        textMesh = new VertexArray();
        textMesh.setVertexBuffers(textMeshBuffer);
        setText(text);
    }

    public void setText(@NotNull String text)
    {
        this.text = text;
        textMeshBuffer.setData(font.createTextMesh(text));
        textMesh.setIndexBufferData(VertexBuffer.createQuadIndices(text.length()));
    }

    public void render()
    {
        glDisable(GL_DEPTH_TEST);
        Shader.TEXT.bind();
        Shader.TEXT.setMatrix4f("model", Matrix4f.createTranslate(position));
        Shader.TEXT.setMatrix4f("projection", Matrix4f.pixelOrthographic);
        Shader.TEXT.setVector4f("u_Color", color);
        font.bind(0);
        textMesh.bind();
        textMesh.drawElements();
        glEnable(GL_DEPTH_TEST);
    }

    public void delete()
    {
        textMesh.delete();
    }
}
