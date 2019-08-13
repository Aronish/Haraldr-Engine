package com.game.event;

import java.util.LinkedList;

public class EventHandler {

    private LinkedList<Event> eventQueue;
    private IDispatchCallback dispatchCallback;

    public EventHandler(){
        eventQueue = new LinkedList<>();
    }

    public void setDispatchCallback(IDispatchCallback dispatchCallback){
        this.dispatchCallback = dispatchCallback;
    }

    public void queueEvent(Event event){
        eventQueue.offer(event);
    }

    public void processEvents(){
        for (Event event : eventQueue){
            dispatchCallback.dispatchEvent(event);
            eventQueue.poll();
        }
    }
}
