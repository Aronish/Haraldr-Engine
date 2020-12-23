package haraldr.dockspace.uicomponents;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;

public class UIVector3 extends UIComponent
{
    private UIInputField<UIInputField.FloatValue> x, y, z;
    private UICheckbox linked;
    private float elementWidth;

    public UIVector3(TextBatch parentTextBatch, Vector3f defaultValues, Vector3ChangeAction vector3ChangeAction)
    {
        this(parentTextBatch, defaultValues, 0.02f, false, vector3ChangeAction);
    }

    public UIVector3(TextBatch parentTextBatch, Vector3f defaultValues, float dragSensitivity, Vector3ChangeAction vector3ChangeAction)
    {
        this(parentTextBatch, defaultValues, dragSensitivity, false, vector3ChangeAction);
    }

    public UIVector3(TextBatch parentTextBatch, Vector3f defaultValues, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        this(parentTextBatch, defaultValues, 0.02f, initiallyLinked, vector3ChangeAction);
    }

    public UIVector3(TextBatch parentTextBatch, Vector3f defaultValues, float dragSensitivity, boolean initiallyLinked, Vector3ChangeAction vector3ChangeAction)
    {
        x = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
        {
            if (linked.isChecked())
            {
                vector3ChangeAction.run(inputFieldValue.getValue(), inputFieldValue.getValue(), inputFieldValue.getValue());
                y.getValue().setValue(inputFieldValue.getValue());
                y.updateTextLabel();
                z.getValue().setValue(inputFieldValue.getValue());
                z.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(inputFieldValue.getValue(), y.getValue().getValue(), z.getValue().getValue());
            }
        });
        y = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
        {
            if (linked.isChecked())
            {
                vector3ChangeAction.run(inputFieldValue.getValue(), inputFieldValue.getValue(), inputFieldValue.getValue());
                x.getValue().setValue(inputFieldValue.getValue());
                x.updateTextLabel();
                z.getValue().setValue(inputFieldValue.getValue());
                z.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(x.getValue().getValue(), inputFieldValue.getValue(), z.getValue().getValue());
            }
        });
        z = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
        {
            if (linked.isChecked())
            {
                vector3ChangeAction.run(inputFieldValue.getValue(), inputFieldValue.getValue(), inputFieldValue.getValue());
                x.getValue().setValue(inputFieldValue.getValue());
                x.updateTextLabel();
                y.getValue().setValue(inputFieldValue.getValue());
                y.updateTextLabel();
            } else
            {
                vector3ChangeAction.run(x.getValue().getValue(), y.getValue().getValue(), inputFieldValue.getValue());
            }
        });
        linked = new UICheckbox(initiallyLinked);
        linked.setWidth(20f);
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        updatePosition(position);
    }

    private void updatePosition(Vector2f position)
    {
        x.setPosition(position);
        y.setPosition(Vector2f.add(position, new Vector2f(elementWidth, 0f)));
        z.setPosition(Vector2f.add(position, new Vector2f(2f * elementWidth, 0f)));
        linked.setPosition(Vector2f.add(position, new Vector2f(3f * elementWidth, 0f)));
    }

    @Override
    public void setWidth(float width)
    {
        elementWidth = width / 3f - 20f / 3f;
        x.setWidth(elementWidth);
        y.setWidth(elementWidth);
        z.setWidth(elementWidth);
        updatePosition(position);
    }

    @Override
    public float getVerticalSize()
    {
        return x.getVerticalSize();
    }

    @Override
    public boolean onEvent(Event event)
    {
        return x.onEvent(event) | y.onEvent(event) | z.onEvent(event) | linked.onEvent(event);
    }

    @Override
    public void render(Batch2D batch)
    {
        x.render(batch);
        y.render(batch);
        z.render(batch);
        linked.render(batch);
    }

    public interface Vector3ChangeAction
    {
        void run(float x, float y, float z);
    }
}