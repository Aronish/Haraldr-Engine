package dockspace;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Dockspace
{
    private static final Vector4f BACKGROUND_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);

    private Vector2f position, size;

    private List<DockablePanel> panels = new ArrayList<>();
    private DockablePanel selectedPanel;
    private boolean drawDockGizmo;

    public Dockspace(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;
    }

    public void addChild(DockablePanel panel)
    {
        panels.add(panel);
    }

    public void onEvent(Event event, Window window)
    {
        panels.forEach(panel -> panel.onEvent(event, window));
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            selectedPanel = null;
            for (DockablePanel panel : panels)
            {
                if (panel.select(mousePressedEvent))
                {
                    selectedPanel = panel;
                    break;
                }
            }
        }
        if (event.eventType == EventType.MOUSE_RELEASED) selectedPanel = null;
        drawDockGizmo = selectedPanel != null;
    }

    public void render()
    {
        Renderer2D.drawQuad(position, size, BACKGROUND_COLOR);
        panels.forEach(DockablePanel::render);
        if (drawDockGizmo) drawDockGizmo();
    }

    private void drawDockGizmo()
    {
        Renderer2D.drawQuad(Vector2f.divide(size, new Vector2f(2f, 1f)), new Vector2f(80f), new Vector4f(1f));
    }
}
