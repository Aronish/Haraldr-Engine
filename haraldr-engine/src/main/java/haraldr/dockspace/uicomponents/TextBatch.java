package haraldr.dockspace.uicomponents;

import haraldr.debug.Logger;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ResourceManager;
import haraldr.graphics.Shader;
import haraldr.graphics.ShaderDataType;
import haraldr.graphics.VertexArray;
import haraldr.graphics.VertexBuffer;
import haraldr.graphics.VertexBufferElement;
import haraldr.graphics.VertexBufferLayout;
import haraldr.main.ArrayUtils;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class TextBatch
{
    private static final int MAX_CHARACTERS = 1000, VERTEX_SIZE = 8;
    private static final Shader TEXT_PASS = Shader.create("internal_shaders/textpass.glsl");

    private List<TextLabel> textLabels = new ArrayList<>();
    private Font font;

    private VertexArray texts = new VertexArray();
    private VertexBuffer textMeshData;
    private int indexCount;

    public TextBatch(Font font)
    {
        this.font = font;
        texts.setIndexBufferData(VertexBuffer.createQuadIndices(MAX_CHARACTERS * 6));
        VertexBufferLayout layout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT4)
        );
        textMeshData = new VertexBuffer(MAX_CHARACTERS * 4 * VERTEX_SIZE * 4, layout, VertexBuffer.Usage.DYNAMIC_DRAW);
        texts.setVertexBuffers(textMeshData);

        ResourceManager.addTextBatch(this);
    }

    public TextLabel createTextLabel(String text, Vector2f position, Vector4f color)
    {
        TextLabel label = new TextLabel(text, position, color, font);
        textLabels.add(label);
        refreshTextMeshData();
        return label;
    }

    public void addTextLabel(TextLabel label)
    {
        textLabels.add(label);
        refreshTextMeshData();
    }

    public void refreshTextMeshData()
    {
        List<Float> textMeshData = new ArrayList<>();
        for (TextLabel label : textLabels)
        {
            label.refresh();
            textMeshData.addAll(label.getTextMeshData());
        }
        this.textMeshData.setData(ArrayUtils.toPrimitiveArrayF(textMeshData));
        indexCount = (textMeshData.size() / (VERTEX_SIZE * 4)) * 6;
    }

    public void render()
    {
        TEXT_PASS.bind();
        TEXT_PASS.setMatrix4f("projection", Renderer2D.pixelOrthographic);
        font.bind(0);
        texts.bind();
        texts.drawElements(indexCount);
    }

    public void clear()
    {
        textMeshData.setData(new float[] {});
        textLabels.clear();
        indexCount = 0;
    }

    public Font getFont()
    {
        return font;
    }

    public void dispose()
    {
        texts.delete();
    }
}
