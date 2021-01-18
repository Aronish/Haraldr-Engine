package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.ParentCollapsedEvent;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class UIInfoLabel extends UIComponent
{
    private String value;
    private TextLabel valueLabel;

    public UIInfoLabel(String value)
    {
        this(null, value);
    }

    public UIInfoLabel(UIContainer parent, String value)
    {
        super(parent);
        this.value = value;
        valueLabel = textBatch.createTextLabel(value, position, new Vector4f(1f));
    }

    public void setValue(String value)
    {
        this.value = value;
        valueLabel.setText(value);
        textBatch.refreshTextMeshData();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        valueLabel.setPosition(position);
        textBatch.refreshTextMeshData();
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        if (event.eventType == EventType.PARENT_COLLAPSED)
        {
            valueLabel.setEnabled(!((ParentCollapsedEvent) event).collapsed);
            textBatch.refreshTextMeshData();
        }
        return false;
    }

    @Override
    public float getVerticalSize()
    {
        return valueLabel.getFont().getSize();
    }

    public String getValue()
    {
        return value;
    }
}
