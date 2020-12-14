package haraldr.dockspace.uicomponents;

import haraldr.event.CharTypedEvent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class UnlabeledInputField extends UnlabeledComponent
{
    private static final Vector4f SELECTED_COLOR = new Vector4f(1f), UNSELECTED_COLOR = new Vector4f(0f, 0f, 0f, 1f);

    private Vector2f borderSize;
    private Vector2f fieldPosition = new Vector2f(), fieldSize;
    private float borderWidth = 2f;
    private boolean selected;

    private InputField.InputType inputType;
    private String value = "";
    private TextLabel textLabel;
    private TextBatch parentTextBatch;

    private InputField.InputFieldChangeAction inputFieldChangeAction;

    public UnlabeledInputField(TextBatch parentTextBatch)
    {
        this(parentTextBatch, "", InputField.InputType.ANY, (((addedChar, fullText) -> {})));
    }

    public UnlabeledInputField(TextBatch parentTextBatch, String defaultValue)
    {
        this(parentTextBatch, defaultValue, InputField.InputType.ANY, ((addedChar, fullText) -> {}));
    }

    public UnlabeledInputField(TextBatch parentTextBatch, String defaultValue, InputField.InputFieldChangeAction inputFieldChangeAction)
    {
        this(parentTextBatch, defaultValue, InputField.InputType.ANY, inputFieldChangeAction);
    }

    public UnlabeledInputField(TextBatch parentTextBatch, String defaultValue, InputField.InputType inputType)
    {
        this(parentTextBatch, defaultValue, inputType, ((addedChar, fullText) -> {}));
    }

    public UnlabeledInputField(TextBatch parentTextBatch, String defaultValue, InputField.InputType inputType, InputField.InputFieldChangeAction inputFieldChangeAction)
    {
        fieldSize = new Vector2f(0f, parentTextBatch.getFont().getSize() - 2f * borderWidth);
        borderSize = new Vector2f(0f, parentTextBatch.getFont().getSize());
        textLabel = parentTextBatch.createTextLabel(defaultValue, fieldPosition, new Vector4f(0f, 0f, 0f, 1f));
        value = defaultValue;
        this.inputType = inputType;
        this.inputFieldChangeAction = inputFieldChangeAction;
        this.parentTextBatch = parentTextBatch;
    }

    public void setInputFieldChangeAction(InputField.InputFieldChangeAction inputFieldChangeAction)
    {
        this.inputFieldChangeAction = inputFieldChangeAction;
    }

    public void setValue(String value)
    {
        this.value = value;
        textLabel.setText(value);
        parentTextBatch.refreshTextMeshData();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        fieldPosition.set(Vector2f.add(position, borderWidth));
        textLabel.setPosition(position);
    }

    @Override
    public void setWidth(float width)
    {
        fieldSize.setX(width - 2f * borderWidth);
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
                requireRedraw = lastSelectedState != selected;
            }
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_2) && selected)
            {
                value = "";
                textLabel.setText(value);
                parentTextBatch.refreshTextMeshData();
                inputFieldChangeAction.run('\b', value);
            }
        }
        if (selected)
        {
            if (event.eventType == EventType.KEY_PRESSED)
            {
                if (Input.wasKeyPressed(event, KeyboardKey.KEY_BACKSPACE) && value.length() > 0)
                {
                    value = value.substring(0, value.length() - 1);
                    textLabel.setText(value);
                    parentTextBatch.refreshTextMeshData();
                    inputFieldChangeAction.run('\b', value);
                }
            }
            if (event.eventType == EventType.CHAR_TYPED)
            {
                var charTypedEvent = (CharTypedEvent) event;
                if (inputType == InputField.InputType.ANY || inputType.allowedCharacters.contains(charTypedEvent.character))
                {
                    value += charTypedEvent.character;
                    textLabel.setText(value);
                    parentTextBatch.refreshTextMeshData();
                    inputFieldChangeAction.run(charTypedEvent.character, value);
                }
            }
        }
        return requireRedraw;
    }

    @Override
    public void render(Batch2D batch)
    {
        batch.drawQuad(position, borderSize, selected ? SELECTED_COLOR : UNSELECTED_COLOR);
        batch.drawQuad(fieldPosition, fieldSize, new Vector4f(0.8f, 0.8f, 0.8f, 1f));
    }
}
