package engine.debug;

import engine.event.Event;
import engine.event.EventCallback;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.graphics.Shader;
import engine.input.Key;

public class DebugEventHandler implements EventCallback
{
    @Override
    public void onEvent(Event event)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (((KeyPressedEvent) event).keyCode == Key.KEY_Q.keyCode)
            {
                Shader.DEFAULT.recompile();
            }
        }
    }
}
