package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;

public class UIVector3Linkable extends UIVector3
{
    private UICheckbox linked;

    public UIVector3Linkable(TextBatch parentTextBatch, Vector3f defaultValues, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        this(parentTextBatch, defaultValues, 0.02f, initiallyLinked, vector3ChangeAction);
    }

    public UIVector3Linkable(TextBatch parentTextBatch, Vector3f defaultValues, float dragSensitivity, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        linked = new UICheckbox(initiallyLinked);
        linked.setWidth(20f);
        x = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
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
        y = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
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
        z = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
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

    public UIVector3Linkable(TextBatch parentTextBatch, Vector3f min, Vector3f max, Vector3f defaultValues, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        this(parentTextBatch, min, max, defaultValues, 0.02f, initiallyLinked, vector3ChangeAction);
    }

    public UIVector3Linkable(TextBatch parentTextBatch, Vector3f min, Vector3f max, Vector3f defaultValues, float dragSensitivity, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        linked = new UICheckbox(initiallyLinked);
        linked.setWidth(20f);
        x = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getX()) value = min.getX();
            if (value > max.getX()) value = max.getX();
            inputFieldValue.setValue(value);
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
        y = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getY()) value = min.getY();
            if (value > max.getY()) value = max.getY();
            inputFieldValue.setValue(value);
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
        z = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getZ()) value = min.getZ();
            if (value > max.getZ()) value = max.getZ();
            inputFieldValue.setValue(value);
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

    @Override
    public void updatePosition(Vector2f position)
    {
        super.updatePosition(position);
        linked.setPosition(Vector2f.add(position, new Vector2f(3f * elementWidth, 0f)));
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        return super.onEvent(event, window) | linked.onEvent(event, window);
    }

    @Override
    public void draw(Batch2D batch)
    {
        super.draw(batch);
        linked.draw(batch);
    }
}
