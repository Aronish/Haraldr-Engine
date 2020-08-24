package editor;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.KeyPressedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ui.Button;
import haraldr.graphics.ui.Checkbox;
import haraldr.graphics.ui.Pane;
import haraldr.graphics.ui.Slider;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.scenegraph.Scene2D;

public class EditorScene extends Scene2D
{
    private Pane propertiesPane;
    private float red, green, blue;
    private boolean visible;

    @Override
    protected void onClientActivate(Window window)
    {
        propertiesPane = new Pane(
                new Vector2f(),
                new Vector2f(400, window.getHeight()),
                "Properties"
        );
        Slider red = new Slider("Red", propertiesPane);
        red.setSliderChangeAction((value) -> this.red = value);
        Slider green = new Slider("Green", propertiesPane);
        green.setSliderChangeAction((value) -> this.green = value);
        Slider blue = new Slider("Blue", propertiesPane);
        blue.setSliderChangeAction((value) -> this.blue = value);
        Button reset = new Button("Reset", propertiesPane);
        reset.setPressAction(() -> {
            red.setValue(0f);
            green.setValue(0f);
            blue.setValue(0f);
        });
        propertiesPane.addChild(red);
        propertiesPane.addChild(green);
        propertiesPane.addChild(blue);
        propertiesPane.addChild(reset);
        Checkbox checkbox = new Checkbox("Visible", propertiesPane);
        checkbox.setStateChangeAction((state) -> {
            visible = state;
        });
        propertiesPane.addChild(checkbox);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
        propertiesPane.onEvent(event);
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            propertiesPane.setSize(400, windowResizedEvent.height);
        }
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
        }
        if (event.eventType == EventType.KEY_PRESSED)
        {
            var keyPressedEvent = (KeyPressedEvent) event;
        }
    }

    @Override
    protected void onClientUpdate(float deltaTime, Window window)
    {
    }

    @Override
    protected void onClientRender()
    {
        Renderer2D.begin();
        propertiesPane.render();
        if (visible) Renderer2D.drawQuad(new Vector2f(0f, 300f), new Vector2f(200f), new Vector4f(red, green, blue, 1f));
        Renderer2D.end();
        propertiesPane.renderText();
    }

    @Override
    protected void onClientDispose()
    {
    }
}
