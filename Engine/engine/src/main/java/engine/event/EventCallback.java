package engine.event;

@FunctionalInterface
public interface EventCallback
{
    void onEvent(Event event);
}
