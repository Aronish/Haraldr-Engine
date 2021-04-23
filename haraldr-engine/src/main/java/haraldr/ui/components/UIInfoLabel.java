package haraldr.ui.components;

import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.TextLabel;

public class UIInfoLabel extends UIComponent
{
    private String value;
    private TextLabel valueLabel;

    public UIInfoLabel(UIContainer parent, int layerIndex, String value)
    {
        super(parent, layerIndex);
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
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        valueLabel.setEnabled(enabled);
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
