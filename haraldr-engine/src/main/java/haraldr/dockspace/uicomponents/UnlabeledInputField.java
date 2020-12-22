package haraldr.dockspace.uicomponents;

import haraldr.event.CharTypedEvent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.event.ParentCollapsedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;
import org.jetbrains.annotations.Contract;

import java.util.List;

@SuppressWarnings("rawtypes")
public class UnlabeledInputField<T extends UnlabeledInputField.InputFieldValue> extends UnlabeledComponent
{
    private static final Vector4f SELECTED_COLOR = new Vector4f(1f), UNSELECTED_COLOR = new Vector4f(0f, 0f, 0f, 1f);
    private static final float BORDER_WIDTH = 2f;

    private Vector2f borderSize;
    private Vector2f fieldPosition = new Vector2f(), fieldSize;
    private boolean selected;

    private T value;
    private TextLabel textLabel;
    private TextBatch parentTextBatch;
    private InputFieldChangeAction<T> inputFieldChangeAction;

    public UnlabeledInputField(TextBatch parentTextBatch, T defaultValue, InputFieldChangeAction<T> inputFieldChangeAction)
    {
        fieldSize = new Vector2f(0f, parentTextBatch.getFont().getSize() - 2f * BORDER_WIDTH);
        borderSize = new Vector2f(0f, parentTextBatch.getFont().getSize());
        value = defaultValue;
        textLabel = parentTextBatch.createTextLabel(value.toString(), fieldPosition, new Vector4f(0f, 0f, 0f, 1f));
        this.parentTextBatch = parentTextBatch;
        this.inputFieldChangeAction = inputFieldChangeAction;
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        fieldPosition.set(Vector2f.add(position, BORDER_WIDTH));
        textLabel.setPosition(position);
    }

    @Override
    public void setWidth(float width)
    {
        fieldSize.setX(width - 2f * BORDER_WIDTH);
        borderSize.setX(width);
    }

    @Override
    public float getVerticalSize()
    {
        return borderSize.getY();
    }

    @Override
    public boolean onEvent(Event event)
    {
        boolean requireRedraw = false;
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                boolean lastSelectedState = selected;
                selected = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), fieldPosition, fieldSize);

                if (value.toString().length() == 0) value.setDefaultValue();
                updateTextLabel();

                requireRedraw = lastSelectedState != selected;
            }
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_2) && selected)
            {
                value.clear();
                updateTextLabel();
            }
        }
        if (selected)
        {
            if (event.eventType == EventType.KEY_PRESSED)
            {
                if (Input.wasKeyPressed(event, KeyboardKey.KEY_BACKSPACE) && value.toString().length() > 0)
                {
                    value.onCharacterTyped('\b');
                    updateTextLabel();
                }
            }
            if (event.eventType == EventType.CHAR_TYPED)
            {
                var charTypedEvent = (CharTypedEvent) event;
                value.onCharacterTyped(charTypedEvent.character);
                updateTextLabel();
            }
        }
        if (event.eventType == EventType.PARENT_COLLAPSED)
        {
            textLabel.setEnabled(((ParentCollapsedEvent) event).collapsed);
        }
        return requireRedraw;
    }

    private void updateTextLabel()
    {
        textLabel.setText(value.toString());
        parentTextBatch.refreshTextMeshData();
        inputFieldChangeAction.run(value);
    }

    @Override
    public void render(Batch2D batch)
    {
        batch.drawQuad(position, borderSize, selected ? SELECTED_COLOR : UNSELECTED_COLOR);
        batch.drawQuad(fieldPosition, fieldSize, new Vector4f(0.8f, 0.8f, 0.8f, 1f));
    }

    public interface InputFieldChangeAction<UnderlyingType extends InputFieldValue>
    {
        void run(UnderlyingType inputFieldValue);
    }

    public interface InputFieldValue<UnderlyingType>
    {
        void onCharacterTyped(char enteredCharacter);
        void setDefaultValue();
        void clear();
        String toString();
        UnderlyingType getValue();
    }

    public static class FloatValue implements InputFieldValue<Float>
    {
        private String floatValue;

        @Contract(pure = true)
        public FloatValue(float floatValue)
        {
            this.floatValue = Float.toString(floatValue);
        }

        @Override
        public void onCharacterTyped(char enteredCharacter)
        {
            if (enteredCharacter != '\b')
            {
                if (List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.').contains(enteredCharacter))
                {
                    if (enteredCharacter != '.' || !floatValue.contains("."))
                    {
                        floatValue += enteredCharacter;
                    }
                }
            } else if (floatValue.length() > 0)
            {
                floatValue = floatValue.substring(0, floatValue.length() - 1);
            }
        }

        @Override
        public void setDefaultValue()
        {
            floatValue = "0.0";
        }

        @Override
        public void clear()
        {
            floatValue = "";
        }

        @Override
        public Float getValue()
        {
            return floatValue.isEmpty() ? 0f : Float.parseFloat(floatValue);
        }

        @Override
        public String toString()
        {
            return floatValue;
        }
    }

    public static class StringValue implements InputFieldValue<String>
    {
        private String stringValue;

        @Contract(pure = true)
        public StringValue(String stringValue)
        {
            this.stringValue = stringValue;
        }

        @Override
        public void onCharacterTyped(char enteredCharacter)
        {
            if (enteredCharacter != '\b')
            {
                stringValue += enteredCharacter;
            } else
            {
                stringValue = stringValue.substring(0, stringValue.length() - 1);
            }
        }

        @Override
        public void setDefaultValue()
        {
            stringValue = "";
        }

        @Override
        public void clear()
        {
            stringValue = "";
        }

        @Override
        public String getValue()
        {
            return stringValue;
        }

        @Override
        public String toString()
        {
            return stringValue;
        }
    }
}