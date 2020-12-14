package haraldr.dockspace.uicomponents;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class UnlabeledInfoLabel extends UnlabeledComponent
{
    private String value;
    private TextLabel valueLabel;
    private TextBatch parentTextBatch;

    public UnlabeledInfoLabel(String value, TextBatch parentTextBatch)
    {
        this.value = value;
        this.parentTextBatch = parentTextBatch;
        valueLabel = parentTextBatch.createTextLabel(value, position, new Vector4f(1f));
    }

    public void setValue(String value)
    {
        this.value = value;
        valueLabel.setText(value);
        parentTextBatch.refreshTextMeshData();
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        valueLabel.setPosition(position);
        parentTextBatch.refreshTextMeshData();
    }

    @Override
    public void setWidth(float width)
    {
    }

    @Override
    public float getVerticalSize()
    {
        return valueLabel.getFont().getSize();
    }

    @Override
    public boolean onEvent(Event event)
    {
        return false;
    }

    @Override
    public void render(Batch2D batch)
    {
    }
}
