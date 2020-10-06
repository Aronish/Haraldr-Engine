package haraldr.dockspace.uicomponents;

import haraldr.dockspace.ControlPanel;
import haraldr.event.Event;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class InfoLabel extends LabeledComponent
{
    private Vector2f valueLabelPosition = new Vector2f();
    private String value = "";
    private TextLabel valueLabel;

    public InfoLabel(String name, ControlPanel parent)
    {
        super(name, parent);
        valueLabel = parent.getTextBatch().createTextLabel(value, valueLabelPosition, new Vector4f(1f));
    }

    public void setText(String text)
    {
        value = text;
        valueLabel.setText(value);
        parent.getTextBatch().refreshTextMeshData();
    }

    public String getValue()
    {
        return value;
    }

    @Override
    protected void setComponentPosition(Vector2f position)
    {
        valueLabel.setPosition(position);
        parent.getTextBatch().refreshTextMeshData();
    }

    @Override
    public void setWidth(float width)
    {
    }

    @Override
    public float getVerticalSize()
    {
        return parent.getTextBatch().getFont().getSize();
    }

    @Override
    public void onEvent(Event event)
    {
    }

    @Override
    public void render()
    {
    }
}
