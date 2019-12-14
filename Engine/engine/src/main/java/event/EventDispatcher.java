package event;

import java.util.ArrayList;
import java.util.List;

public class EventDispatcher
{
    private static List<IEventCallback> callbacks = new ArrayList<>();

    public static void addCallback(IEventCallback callback)
    {
        callbacks.add(callback);
    }

    public static void removeCallback(IEventCallback callback)
    {
        callbacks.remove(callback);
    }

    public static void dispatch(Event event)
    {
        for (IEventCallback callback : callbacks)
        {
            callback.onEvent(event);
        }
    }
}
