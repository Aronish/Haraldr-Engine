package editor;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ui.Button;
import haraldr.graphics.ui.InputField;
import haraldr.graphics.ui.Pane;
import haraldr.graphics.ui.Slider;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.scenegraph.Scene2D;

public class EditorOverlay extends Scene2D
{
    private Pane propertiesPane;

    @Override
    protected void onClientActivate(Window window)
    {
        propertiesPane = new Pane(
                new Vector2f(),
                window.getWidth(), window.getHeight(),
                0.25f,
                0.3f,
                "Properties"
        );

        InputField field = new InputField("Name", propertiesPane);
        propertiesPane.addChild(field);
        InputField description = new InputField("Description", propertiesPane);
        propertiesPane.addChild(description);

        Slider interpolation = new Slider("Interpolation", propertiesPane);
        propertiesPane.addChild(interpolation);

        Button button = new Button("Reset", propertiesPane);
        propertiesPane.addChild(button);

        Slider test = new Slider("Test", propertiesPane);
        propertiesPane.addChild(test);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
        propertiesPane.onEvent(event, window);
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            propertiesPane.onWindowResized(windowResizedEvent.width, windowResizedEvent.height);
        }
    }

    @Override
    protected void onClientUpdate(float deltaTime, Window window)
    {
        propertiesPane.onUpdate(deltaTime);
    }

    @Override
    protected void onClientRender()
    {
        Renderer2D.begin();
        propertiesPane.render();
        Renderer2D.end();
        propertiesPane.renderText();
    }

    @Override
    protected void onClientDispose()
    {
    }
}
