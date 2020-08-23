package editor;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.KeyPressedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ui.Font;
import haraldr.graphics.ui.GridLayout;
import haraldr.graphics.ui.Pane;
import haraldr.graphics.ui.TextBatch;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.scenegraph.Scene2D;

public class EditorScene extends Scene2D
{
    private Pane propertiesPane;

    @Override
    protected void onClientActivate(Window window)
    {
        propertiesPane = new Pane(
                new Vector2f(),
                new Vector2f(300, window.getHeight()),
                new Vector4f(0.3f, 0.3f, 0.3f, 1f),
                "Properties",
                new GridLayout(
                        1,
                        5,
                        300,
                        window.getHeight(),
                        new Vector4f(10f, 0f, 10f, 10f),
                        new Vector2f(0f, 30f)
                )
        );
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
        propertiesPane.onEvent(event);
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            propertiesPane.setSize(300, windowResizedEvent.height);
            propertiesPane.refresh(propertiesPane.getPosition());
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
        propertiesPane.render(new Vector2f());
        Renderer2D.end();
        propertiesPane.renderText();
    }

    @Override
    protected void onClientDispose()
    {
    }
}
