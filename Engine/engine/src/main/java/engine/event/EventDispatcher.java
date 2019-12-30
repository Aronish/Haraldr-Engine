package engine.event;

import java.util.ArrayList;
import java.util.List;

//TODO: Create support for subscribing to certain events.
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

    public static void dispatch(Event event)
    {
        for (EventCallback callback : callbacks)
        {
            callback.onEvent(event);
        }
    }
}
