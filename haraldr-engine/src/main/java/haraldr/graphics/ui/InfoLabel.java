package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class InfoLabel extends LabeledComponent
{
    private Vector2f valueLabelPosition = new Vector2f();
    private String value = "6789";
    private TextLabel valueLabel;

    public InfoLabel(String name, Pane parent)
    {
        super(name, parent);
        valueLabel = parent.textBatch.createTextLabel(value, valueLabelPosition, new Vector4f(41f));
    }

    @Override
    protected void setComponentPosition(Vector2f position)
    {
        valueLabel.setPosition(position);
        parent.textBatch.refreshTextMeshData();
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
    public void onUpdate(float deltaTime)
    {
    }

    @Override
    public void render()
    {
    }
}
