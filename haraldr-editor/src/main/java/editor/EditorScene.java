package editor;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ui.Button;
import haraldr.graphics.ui.Pane;
import haraldr.graphics.ui.UIContainer;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.scenegraph.Scene2D;

public class EditorScene extends Scene2D
{
    private UIContainer editorContainer;
    private Button button = new Button(new Vector2f(), new Vector2f(0f, 100f));

    @Override
    protected void onClientActivate(Window window)
    {
        editorContainer = new UIContainer(5, 1, window.getWidth(), window.getHeight());
        Pane properties = new Pane(new Vector4f(0.4f, 0.4f, 0.4f, 1f));
        properties.addChild(new Pane(new Vector2f(), new Vector2f(0f, 100f), new Vector4f(0.4f, 0.3f, 0.3f, 1f)));
        properties.addChild(new Pane(new Vector2f(), new Vector2f(0f, 100f), new Vector4f(0.4f, 0.3f, 0.3f, 1f)));
        properties.addChild(button);
        editorContainer.addComponent(properties, 4, 0);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            editorContainer.resize(windowResizedEvent.width, windowResizedEvent.height);
        }
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            button.onClick(mousePressedEvent.xPos, mousePressedEvent.yPos);
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
        editorContainer.render();
        Renderer2D.end();
    }

    @Override
    protected void onClientDispose()
    {

    }
}
