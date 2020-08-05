package haraldr.graphics.ui;

import haraldr.graphics.Shader;
import haraldr.graphics.ShaderDataType;
import haraldr.graphics.VertexArray;
import haraldr.graphics.VertexBuffer;
import haraldr.graphics.VertexBufferElement;
import haraldr.graphics.VertexBufferLayout;
import haraldr.main.ArrayUtils;
import haraldr.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class TextManager
{
    private static final int MAX_CHARACTERS = 1000, VERTEX_SIZE;
    private static final Shader TEXT_PASS = Shader.create("default_shaders/textpass.glsl");

    private static VertexArray texts = new VertexArray();
    private static VertexBuffer textMeshes;
    private static int indexCount;

    public static Font FONT = new Font("default_fonts/Roboto-Regular.ttf", 30);

    private static List<TextLabel> textLabels = new ArrayList<>();

    static
    {
        texts.setIndexBufferData(VertexBuffer.createQuadIndices(MAX_CHARACTERS * 6));
        VertexBufferLayout layout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT4)
        );
        VERTEX_SIZE = layout.getVertexSize();
        textMeshes = new VertexBuffer(MAX_CHARACTERS * 4 * VERTEX_SIZE * 4, layout, VertexBuffer.Usage.DYNAMIC_DRAW);
        texts.setVertexBuffers(textMeshes);
    }

    public static void addTextLabel(TextLabel newLabel)
    {
        textLabels.add(newLabel);
    }

    public static void refreshTextMeshData()
    {
        List<Float> textMeshData = new ArrayList<>();
        for (TextLabel label : textLabels)
        {
            textMeshData.addAll(label.getTextMeshData());
        }
        textMeshes.setData(ArrayUtils.toPrimitiveArrayF(textMeshData));
        indexCount = (textMeshData.size() / (VERTEX_SIZE * 4)) * 6;
    }

    public static void render()
    {
        TEXT_PASS.bind();
        TEXT_PASS.setMatrix4f("projection", Matrix4f.pixelOrthographic);
        FONT.bind(0);
        texts.bind();
        texts.drawElements(indexCount);
    }

    public static void dispose()
    {
        texts.delete();
    }
}
