package haraldr.graphics.ui;

import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TextLabel
{
    private Font font;
    private String text;
    private Vector4f color;
    private Vector2f position;
    private List<Float> textMeshData = new ArrayList<>();

    public TextLabel(@NotNull String text, Vector2f position, Vector4f color, Font font)
    {
        this.text = text;
        this.color = color;
        this.font = font;
        this.position = Vector2f.add(position, new Vector2f(0f, font.getSize() - font.getBaseline()));
        setText(text);
    }

    public void setText(@NotNull String text)
    {
        this.text = text;
        textMeshData = font.createTextMesh(text, position, color);
    }

    public List<Float> getTextMeshData()
    {
        return textMeshData;
    }
}
