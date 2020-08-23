package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Button;
import haraldr.input.Input;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Checkbox extends UIComponent
{
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f), ON_COLOR = new Vector4f(0.3f, 0.8f, 0.2f, 1f);

    private Pane parent;

    private boolean state;

    public Checkbox(String name, Vector2f position, Pane parent)
    {
        super(position, new Vector2f(parent.getTextBatch().getFont().getSize()), name);
        this.parent = parent;
   }

    @Override
    protected void setupLabel(String name)
    {
    }

    @Override
    public void setPosition(Vector2f position)
    {
        name.setPosition(position);
        parent.getTextBatch().refreshTextMeshData();
        this.position.set(position).add(name.getPixelWidth(), 0f);
    }

    @Override
    public void onEvent(Event event)
    {
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                if (mousePressedEvent.xPos > position.getX() &&
                    mousePressedEvent.xPos < position.getX() + size.getX() &&
                    mousePressedEvent.yPos > position.getY() &&
                    mousePressedEvent.yPos < position.getY() + size.getY())
                {
                    state = !state;
                }
            }
        }
    }

    @Override
    public void render(Vector2f parentPosition)
    {
        Renderer2D.drawQuad(position, size, state ? ON_COLOR : OFF_COLOR);
    }
}
