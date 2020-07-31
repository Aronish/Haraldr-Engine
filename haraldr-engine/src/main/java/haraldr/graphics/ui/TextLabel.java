package haraldr.graphics.ui;

import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TextLabel
{
    private String text;
    private Vector4f color;
    private Vector2f position;
    private List<Float> textMeshData = new ArrayList<>();

    public TextLabel(Vector2f position, Vector4f color, @NotNull String text)
    {
        this.text = text;
        this.color = color;
        this.position = position;
        TextManager.addTextLabel(this);
        setText(text);
    }

    public void setText(@NotNull String text)
    {
        this.text = text;
        textMeshData = TextManager.FONT.createTextMesh(text, position, color);
        TextManager.refreshTextMeshData(); // Observer Pattern?
    }

    public List<Float> getTextMeshData()
    {
        return textMeshData;
    }
}
