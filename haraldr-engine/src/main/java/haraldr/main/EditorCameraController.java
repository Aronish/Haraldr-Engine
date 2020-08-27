package haraldr.main;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.input.Button;
import haraldr.input.Input;

public class EditorCameraController implements PerspectiveCameraController
{
    private PerspectiveCamera reference;

    private boolean mouseHeld;

    @Override
    public void onUpdate(float deltaTime, Window window)
    {

    }

    @Override
    public void onEvent(Event event, Window window)
    {
        if (Input.wasMousePressed(event, Button.MOUSE_BUTTON_1)) mouseHeld = true;
        if (Input.wasMouseReleased(event, Button.MOUSE_BUTTON_1)) mouseHeld = false;
        if (mouseHeld)
        {
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                reference.setYaw((float)((MouseMovedEvent) event).xPos / window.getHeight() * 180f - 90f);
                reference.setPitch((float)((window.getHeight() - ((MouseMovedEvent) event).yPos) / window.getHeight() * 180f - 90f));
            }
        }
    }

    @Override
    public void setReference(PerspectiveCamera reference)
    {
        this.reference = reference;
    }
}
