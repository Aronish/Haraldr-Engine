package haraldr.graphics.ui;

import haraldr.event.CharTypedEvent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class InputField extends LabeledComponent
{
    private static final Vector4f SELECTED_COLOR = new Vector4f(1f), UNSELECTED_COLOR = new Vector4f();

    private Vector2f borderPosition = new Vector2f(), borderSize;
    private Vector2f fieldPosition = new Vector2f(), fieldSize;
    private Vector2f cursorSize;
    private float borderWidth = 2f, blinkAccumulator;
    private boolean selected, cursorVisible;

    private InputType inputType;
    private String text = "";
    private TextLabel textLabel;

    private InputFieldChangeAction inputFieldChangeAction;

    public InputField(String name, Pane parent)
    {
        this(name, parent, InputType.ANY, ((addedChar, fullText) -> {}));
    }

    public InputField(String name, Pane parent, InputFieldChangeAction inputFieldChangeAction)
    {
        this(name, parent, InputType.ANY, inputFieldChangeAction);
    }

    public InputField(String name, Pane parent, InputType inputType, InputFieldChangeAction inputFieldChangeAction)
    {
        super(name, parent);
        fieldSize = new Vector2f(parent.size.getX() - parent.getDivider() - 2f * borderWidth, label.getFont().getSize() - 2f * borderWidth);
        borderSize = new Vector2f(parent.size.getX() - parent.getDivider(), label.getFont().getSize());
        cursorSize = new Vector2f(3f, fieldSize.getY());
        textLabel = parent.textBatch.createTextLabel(text, fieldPosition, new Vector4f(0f, 0f, 0f, 1f));
        this.inputType = inputType;
        this.inputFieldChangeAction = inputFieldChangeAction;
    }

    public void setInputFieldChangeAction(InputFieldChangeAction inputFieldChangeAction)
    {
        this.inputFieldChangeAction = inputFieldChangeAction;
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        fieldPosition = Vector2f.add(position, new Vector2f(parent.getDivider() + borderWidth, borderWidth));
        borderPosition = Vector2f.add(position, new Vector2f(parent.getDivider(), 0f));
        textLabel.setPosition(fieldPosition);
    }

    @Override
    public float getVerticalSize()
    {
        return fieldSize.getY();
    }

    @Override
    public void onEvent(Event event)
    {
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            selected =
                    mousePressedEvent.xPos >= fieldPosition.getX() &&
                    mousePressedEvent.xPos <= fieldPosition.getX() + fieldSize.getX() &&
                    mousePressedEvent.yPos >= fieldPosition.getY() &&
                    mousePressedEvent.yPos <= fieldPosition.getY() + fieldSize.getY();
            if (!selected) cursorVisible = false;
        }
        if (selected)
        {
            if (event.eventType == EventType.KEY_PRESSED)
            {
                if (Input.wasKey(event, Key.KEY_BACKSPACE) && text.length() > 0)
                {
                    text = text.substring(0, text.length() - 1);
                }
            }
            if (event.eventType == EventType.CHAR_TYPED)
            {
                var charTypedEvent = (CharTypedEvent) event;
                if (inputType == InputType.ANY || inputType.allowedCharacters.contains(charTypedEvent.character))
                text += charTypedEvent.character;
            }
            textLabel.setText(text);
            parent.textBatch.refreshTextMeshData();
        }
    }

    @Override
    public void onUpdate(float deltaTime)
    {
        if (selected)
        {
            blinkAccumulator += deltaTime;
            if (blinkAccumulator > 0.5f)
            {
                cursorVisible = !cursorVisible;
                blinkAccumulator = 0f;
            }
        }
    }

    @Override
    public void render()
    {
        Renderer2D.drawQuad(borderPosition, borderSize, selected ? SELECTED_COLOR : UNSELECTED_COLOR);
        Renderer2D.drawQuad(fieldPosition, fieldSize, new Vector4f(0.8f, 0.8f, 0.8f, 1f));
        if (cursorVisible) Renderer2D.drawQuad(fieldPosition, cursorSize, new Vector4f(1f));
    }

    public enum InputType
    {
        ANY(new ArrayList<>()),
        NUMBERS(List.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.'));

        private final List<Character> allowedCharacters;

        InputType(List<Character> allowedCharacters)
        {
            this.allowedCharacters = allowedCharacters;
        }
    }

    public interface InputFieldChangeAction
    {
        void run(char addedChar, String fullText);
    }
}
