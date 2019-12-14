package event;

@FunctionalInterface
public interface IEventCallback
{
    void onEvent(Event event);
}
