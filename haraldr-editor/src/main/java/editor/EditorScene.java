package editor;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ui.InputField;
import haraldr.graphics.ui.Pane;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.scenegraph.Scene2D;

public class EditorScene extends Scene2D
{
    private Pane propertiesPane;

    @Override
    protected void onClientActivate(Window window)
    {
        propertiesPane = new Pane(
                new Vector2f(),
                new Vector2f(400, window.getHeight()),
                "Properties"
        );
        InputField field = new InputField("Name", propertiesPane);
        propertiesPane.addChild(field);
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
