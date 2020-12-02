package haraldr.dockspace.uicomponents;

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
    private boolean enabled = true;
    private List<Float> textMeshData = new ArrayList<>();

    public TextLabel(@NotNull String text, Vector2f position, Vector4f color, Font font)
    {
        this.text = text;
        this.color = color;
        this.font = font;
        this.position = Vector2f.add(position, new Vector2f(0f, font.getSize() - font.getBaseline()));
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
        this.position.set(Vector2f.add(position, new Vector2f(0f, font.getSize() - font.getBaseline())));
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
