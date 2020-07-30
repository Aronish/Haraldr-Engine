package engine.event;

import engine.main.Window;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class EventDispatcher
{
    private static List<EventCallback> callbacks = new ArrayList<>();

    public static void addCallback(EventCallback callback)
    {
        callbacks.add(callback);
    }

    public static void removeCallback(EventCallback callback)
    {
        callbacks.remove(callback);
    }

    public static void dispatch(Event event, Window window)
    {
        for (EventCallback callback : callbacks)
        {
            callback.onEvent(event, window);
        }
    }
}
