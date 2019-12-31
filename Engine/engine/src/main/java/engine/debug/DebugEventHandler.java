package engine.debug;

import engine.event.Event;
import engine.event.EventCallback;
import engine.graphics.Shader;
import engine.input.Input;
import engine.input.Key;
import engine.main.Window;

public class DebugEventHandler implements EventCallback
{
    @Override
    public void onEvent(Event event, Window window)
    {
        if (Input.isKeyPressed(window.getWindowHandle(), Key.KEY_Q))
        {
            Shader.DEFAULT.recompile();
        }
    }
}
