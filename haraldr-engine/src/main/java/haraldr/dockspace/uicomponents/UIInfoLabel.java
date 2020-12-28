package haraldr.dockspace.uicomponents;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.ParentCollapsedEvent;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class UIInfoLabel extends UIComponent
{
    private String value;
    private TextLabel valueLabel;
    private TextBatch parentTextBatch;

    public UIInfoLabel(TextBatch parentTextBatch, String value)
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
        if (event.eventType == EventType.PARENT_COLLAPSED)
        {
            valueLabel.setEnabled(((ParentCollapsedEvent) event).collapsed);
            parentTextBatch.refreshTextMeshData();
        }
        return false;
    }

    @Override
    public void render(Batch2D batch)
    {
    }
}
