package editor;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.KeyPressedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ui.Button;
import haraldr.graphics.ui.Container;
import haraldr.graphics.ui.GridLayout;
import haraldr.graphics.ui.Pane;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.scenegraph.Scene2D;

public class EditorScene extends Scene2D
{
    private Pane propertiesPane;
    private Pane buttonPane;

    @Override
    protected void onClientActivate(Window window)
    {
        propertiesPane = new Pane(
                new Vector2f(),
                new Vector2f(300, window.getHeight()),
                new Vector4f(0.3f, 0.3f, 0.3f, 1f),
                new GridLayout(
                        1,
                        5,
                        300,
                        window.getHeight(),
                        new Vector4f(10f, 0f, 10f, 10f),
                        new Vector2f(30f, 0f)
                )
        );
        propertiesPane.addChild(new Button());
        propertiesPane.addChild(new Button());
        buttonPane = new Pane(new Vector4f(0.2f, 0.2f, 0.2f, 1f), new GridLayout(
                3, 1, 300, 300,
                new Vector4f(10f, 0f, 10f, 10f),
                new Vector2f(30f, 0f)
        ));
        buttonPane.addChild(new Button());
        buttonPane.addChild(new Button());
        buttonPane.addChild(new Button());
        propertiesPane.addChild(buttonPane);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            propertiesPane.setSize(300, windowResizedEvent.height);
        }
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
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
    }

    @Override
    protected void onClientDispose()
    {
    }
}
