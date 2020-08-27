package haraldr.main;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.event.WindowFocusEvent;
import haraldr.input.Input;
import haraldr.math.Vector3f;

import static haraldr.input.Key.KEY_A;
import static haraldr.input.Key.KEY_C;
import static haraldr.input.Key.KEY_D;
import static haraldr.input.Key.KEY_S;
import static haraldr.input.Key.KEY_SPACE;
import static haraldr.input.Key.KEY_W;

public class FPSCameraController implements PerspectiveCameraController
{
    private static final float CAMERA_SPEED = 0.75f;
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float ZOOM_SENSITIVITY = 2f;

    private PerspectiveCamera reference;
    private float lastX, lastY;
    private boolean focused = true;

    @Override
    public void setReference(PerspectiveCamera reference)
    {
        this.reference = reference;
    }

    @Override
    public void onEvent(Event event, Window window)
    {
        if (event.eventType == EventType.WINDOW_FOCUS) focused = ((WindowFocusEvent) event).focused;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            if (focused) reference.rotate(((float) mouseMovedEvent.xPos - lastX) * MOUSE_SENSITIVITY, (lastY - (float) mouseMovedEvent.yPos) * MOUSE_SENSITIVITY);
            lastX = (float) mouseMovedEvent.xPos;
            lastY = (float) mouseMovedEvent.yPos;
        }
        if (event.eventType == EventType.MOUSE_SCROLLED) reference.addFov((float) -((MouseScrolledEvent) event).yOffset * ZOOM_SENSITIVITY);
    }

    @Override
    public void onUpdate(float deltaTime, Window window)
    {
        if (Input.isKeyPressed(window, KEY_W))
        {
            reference.addPosition(Vector3f.multiply(reference.getDirection(), CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_S))
        {
            reference.addPosition(Vector3f.multiply(reference.getDirection(), -CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_D))
        {
            reference.addPosition(Vector3f.multiply(reference.getRight(), CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_A))
        {
            reference.addPosition(Vector3f.multiply(reference.getRight(), -CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_SPACE))
        {
            reference.addPosition(Vector3f.multiply(reference.getUp(), CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_C))
        {
            reference.addPosition(Vector3f.multiply(reference.getUp(), -CAMERA_SPEED * deltaTime));
        }
    }
}