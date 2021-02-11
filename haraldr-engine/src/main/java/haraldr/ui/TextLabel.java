package haraldr.ui;

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
    private boolean enabled;
    private Vector2f position;
    private List<Float> textMeshData = new ArrayList<>();

    public TextLabel(String text, Vector2f position, Vector4f color, Font font)
    {
        this(text, position, color, font, true);
    }

    public TextLabel(String text, Vector2f position, Vector4f color, Font font, boolean enabled)
    {
        this.text = text;
        this.font = font;
        this.color = color;
        this.enabled = enabled;
        this.position = Vector2f.addY(position, font.getSize() - font.getBaseline());
        setText(text);
    }

    public void refresh()
    {
        textMeshData = font.createTextMesh(text, position, color);
    }

    public void setText(@NotNull String text)
    {
        this.text = text;
        textMeshData = font.createTextMesh(text, position, color);
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(Vector2f.addY(position, font.getSize() - font.getBaseline()));
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void addPosition(Vector2f position)
    {
        this.position.add(position);
    }

    public void setColor(Vector4f color)
    {
        this.color = color;
    }

    public Font getFont()
    {
        return font;
    }

    public String getText()
    {
        return text;
    }

    public int getPixelWidth()
    {
        return Math.round(font.getPixelWidth(text));
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public List<Float> getTextMeshData()
    {
        return enabled ? textMeshData : new ArrayList<>();
    }
}
