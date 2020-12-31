package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;

public class UIVector3 extends UIComponent
{
    protected UIInputField<UIInputField.FloatValue> x, y, z;
    protected float elementWidth;

    protected UIVector3()
    {
    }

    public UIVector3(TextBatch parentTextBatch, Vector3f defaultValues, Vector3ChangeAction vector3ChangeAction)
    {
        this(parentTextBatch, defaultValues, 0.02f, vector3ChangeAction);
    }

    public UIVector3(TextBatch parentTextBatch, Vector3f defaultValues, float dragSensitivity, Vector3ChangeAction vector3ChangeAction)
    {
        x = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
                vector3ChangeAction.run(inputFieldValue.getValue(), y.getValue().getValue(), z.getValue().getValue()));

        y = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
                vector3ChangeAction.run(x.getValue().getValue(), inputFieldValue.getValue(), z.getValue().getValue()));

        z = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
                vector3ChangeAction.run(x.getValue().getValue(), y.getValue().getValue(), inputFieldValue.getValue()));
    }

    // Max and min

    public UIVector3(TextBatch parentTextBatch, Vector3f min, Vector3f max, Vector3f defaultValues, Vector3ChangeAction vector3ChangeAction)
    {
        this(parentTextBatch, min, max, defaultValues, 0.02f, vector3ChangeAction);
    }

    public UIVector3(TextBatch parentTextBatch, Vector3f min, Vector3f max, Vector3f defaultValues, float dragSensitivity, Vector3ChangeAction vector3ChangeAction)
    {
        x = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getX(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getX()) value = min.getX();
            if (value > max.getX()) value = max.getX();
            inputFieldValue.setValue(value);
            vector3ChangeAction.run(value, y.getValue().getValue(), z.getValue().getValue());
        });
        y = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getY(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getY()) value = min.getY();
            if (value > max.getY()) value = max.getY();
            inputFieldValue.setValue(value);
            vector3ChangeAction.run(x.getValue().getValue(), value, z.getValue().getValue());
        });
        z = new UIInputField<>(parentTextBatch, new UIInputField.FloatValue(defaultValues.getZ(), dragSensitivity), inputFieldValue ->
        {
            float value = inputFieldValue.getValue();
            if (value < min.getZ()) value = min.getZ();
            if (value > max.getZ()) value = max.getZ();
            inputFieldValue.setValue(value);
            vector3ChangeAction.run(x.getValue().getValue(), y.getValue().getValue(), value);
        });
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        updatePosition(position);
    }

    protected void updatePosition(Vector2f position)
    {
        x.setPosition(position);
        y.setPosition(Vector2f.add(position, new Vector2f(elementWidth, 0f)));
        z.setPosition(Vector2f.add(position, new Vector2f(2f * elementWidth, 0f)));
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
    public boolean onEvent(Event event, Window window)
    {
        return x.onEvent(event, window) | y.onEvent(event, window) | z.onEvent(event, window);
    }

    @Override
    public void draw(Batch2D batch)
    {
        x.draw(batch);
        y.draw(batch);
        z.draw(batch);
    }

    @Override
    public void onDispose()
    {
    }

    @FunctionalInterface
    public interface Vector3ChangeAction
    {
        void run(float x, float y, float z);
    }
}