package haraldr.ui.components;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;

public class UIVector3 extends UIComponent
{
    protected UIInputField<UIInputField.FloatValue> x, y, z;
    protected float elementWidth;

    protected UIVector3(UIContainer parent, int layerIndex)
    {
        super(parent, layerIndex);
    }

    public UIVector3(UIContainer parent, int layerIndex, Vector3f defaultValues, Vector3ChangeAction vector3ChangeAction)
    {
        this(parent, layerIndex, defaultValues, 0.02f, vector3ChangeAction);
    }

    public UIVector3(UIContainer parent, int layerIndex, Vector3f defaultValues, float dragSensitivity, Vector3ChangeAction vector3ChangeAction)
    {
        super(parent, layerIndex);
        x = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
                vector3ChangeAction.run(inputFieldValue.getValue(), y.getValue().getValue(), z.getValue().getValue()));

        y = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
                vector3ChangeAction.run(x.getValue().getValue(), inputFieldValue.getValue(), z.getValue().getValue()));

        z = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
                vector3ChangeAction.run(x.getValue().getValue(), y.getValue().getValue(), inputFieldValue.getValue()));
    }

    // Max and min

    public UIVector3(UIContainer parent, int layerIndex, Vector3f min, Vector3f max, Vector3f defaultValues, Vector3ChangeAction vector3ChangeAction)
    {
        this(parent, layerIndex, min, max, defaultValues, 0.02f, vector3ChangeAction);
    }

    public UIVector3(UIContainer parent, int layerIndex, Vector3f min, Vector3f max, Vector3f defaultValues, float dragSensitivity, Vector3ChangeAction vector3ChangeAction)
    {
        super(parent, layerIndex);
        x = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getX()) value = min.getX();
            if (value > max.getX()) value = max.getX();
            inputFieldValue.setValue(value);
            vector3ChangeAction.run(value, y.getValue().getValue(), z.getValue().getValue());
        });
        y = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getY()) value = min.getY();
            if (value > max.getY()) value = max.getY();
            inputFieldValue.setValue(value);
            vector3ChangeAction.run(x.getValue().getValue(), value, z.getValue().getValue());
        });
        z = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getZ()) value = min.getZ();
            if (value > max.getZ()) value = max.getZ();
            inputFieldValue.setValue(value);
            vector3ChangeAction.run(x.getValue().getValue(), y.getValue().getValue(), value);
        });
    }

    protected void updatePosition(Vector2f position)
    {
        x.setPosition(position);
        y.setPosition(Vector2f.addX(position, elementWidth));
        z.setPosition(Vector2f.addX(position, 2f * elementWidth));
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        updatePosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        elementWidth = size.getX() / 3f - 18f / 3f;
        x.setSize(new Vector2f(elementWidth, size.getY()));
        y.setSize(new Vector2f(elementWidth, size.getY()));
        z.setSize(new Vector2f(elementWidth, size.getY()));
        updatePosition(position);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        x.setEnabled(enabled);
        y.setEnabled(enabled);
        z.setEnabled(enabled);
    }

    @Override
    public void draw(Batch2D batch)
    {
        x.draw(batch);
        y.draw(batch);
        z.draw(batch);
    }

    @Override
    public float getVerticalSize()
    {
        return x.getVerticalSize();
    }

    @FunctionalInterface
    public interface Vector3ChangeAction
    {
        void run(float x, float y, float z);
    }
}