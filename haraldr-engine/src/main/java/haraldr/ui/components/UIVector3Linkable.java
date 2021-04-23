package haraldr.ui.components;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;

@SuppressWarnings({"WeakerAccess", "unused"})
public class UIVector3Linkable extends UIVector3
{
    private UICheckbox linked;

    public UIVector3Linkable(UIContainer parent, int layerIndex, Vector3f defaultValues, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        this(parent, layerIndex, defaultValues, 0.02f, initiallyLinked, vector3ChangeAction);
    }

    public UIVector3Linkable(UIContainer parent, int layerIndex, Vector3f defaultValues, float dragSensitivity, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        super(parent, layerIndex);
        linked = new UICheckbox(parent, layerIndex, initiallyLinked);
        linked.setSize(new Vector2f(18f));
        x = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (linked.isChecked())
            {
                vector3ChangeAction.run(value, value, value);
                y.getValue().setValue(value);
                y.updateTextLabel();
                z.getValue().setValue(value);
                z.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(value, y.getValue().getValue(), z.getValue().getValue());
            }
        });
        y = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (linked.isChecked())
            {
                vector3ChangeAction.run(value, value, value);
                x.getValue().setValue(value);
                x.updateTextLabel();
                z.getValue().setValue(value);
                z.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(x.getValue().getValue(), value, z.getValue().getValue());
            }
        });
        z = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (linked.isChecked())
            {
                vector3ChangeAction.run(value, value, value);
                x.getValue().setValue(value);
                x.updateTextLabel();
                y.getValue().setValue(value);
                y.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(x.getValue().getValue(), y.getValue().getValue(), value);
            }
        });
    }

    // Max and min

    public UIVector3Linkable(UIContainer parent, int layerIndex, Vector3f min, Vector3f max, Vector3f defaultValues, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        this(parent, layerIndex, min, max, defaultValues, 0.02f, initiallyLinked, vector3ChangeAction);
    }

    public UIVector3Linkable(UIContainer parent, int layerIndex, Vector3f min, Vector3f max, Vector3f defaultValues, float dragSensitivity, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        super(parent, layerIndex);
        linked = new UICheckbox(parent, layerIndex, initiallyLinked);
        linked.setSize(new Vector2f(18f));
        x = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            String value1 = inputFieldValue.toString();
            if (value < min.getX()) value1 = Float.toString(min.getX());
            if (value > max.getX()) value1 = Float.toString(max.getX());
            inputFieldValue.setStringValue(value1);
            if (linked.isChecked())
            {
                vector3ChangeAction.run(value, value, value);
                y.getValue().setStringValue(value1);
                y.updateTextLabel();
                z.getValue().setStringValue(value1);
                z.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(value, y.getValue().getValue(), z.getValue().getValue());
            }
        });
        y = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getY()) value = min.getY();
            if (value > max.getY()) value = max.getY();
            inputFieldValue.setStringValue(inputFieldValue.toString());
            if (linked.isChecked())
            {
                vector3ChangeAction.run(value, value, value);
                x.getValue().setStringValue(inputFieldValue.toString());
                x.updateTextLabel();
                z.getValue().setStringValue(inputFieldValue.toString());
                z.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(x.getValue().getValue(), value, z.getValue().getValue());
            }
        });
        z = new UIInputField<>(parent, layerIndex, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getZ()) value = min.getZ();
            if (value > max.getZ()) value = max.getZ();
            inputFieldValue.setStringValue(inputFieldValue.toString());
            if (linked.isChecked())
            {
                vector3ChangeAction.run(value, value, value);
                x.getValue().setStringValue(inputFieldValue.toString());
                x.updateTextLabel();
                y.getValue().setStringValue(inputFieldValue.toString());
                y.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(x.getValue().getValue(), y.getValue().getValue(), value);
            }
        });
    }

    @Override
    public void updatePosition(Vector2f position)
    {
        super.updatePosition(position);
        linked.setPosition(Vector2f.addX(position, 3f * elementWidth));
    }

    @Override
    public void setSize(Vector2f size)
    {
        super.setSize(size);
        linked.setSize(new Vector2f(18f));
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        linked.setEnabled(enabled);
    }

    @Override
    public void draw(Batch2D batch)
    {
        super.draw(batch);
        linked.draw(batch);
    }
}