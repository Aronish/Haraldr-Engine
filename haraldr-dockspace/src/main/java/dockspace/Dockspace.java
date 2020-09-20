package dockspace;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Dockspace
{
    private static final Vector4f BACKGROUND_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);

    private Vector2f position, size;
    private boolean splitting;

    private List<DockingArea> dockingAreas = new ArrayList<>(); // 0 contains 1, 1 contains 2
    private DockingArea selectedDockingArea;
    private float startingSize;
    private boolean removing;

    private LinkedList<DockablePanel> panels = new LinkedList<>();
    private DockablePanel activePanel;
    private boolean activePanelMoving;

    public Dockspace(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;

        dockingAreas.add(new DockingArea(position, size));
    }

    public void addPanel(DockablePanel panel)
    {
        panels.add(panel);
    }

    public void onEvent(Event event, Window window)
    {
        for (DockablePanel panel : panels)
        {
            if (activePanelMoving = panel.onEvent(event, window))
            {
                if (event.eventType == EventType.MOUSE_PRESSED)
                {
                    activePanel = null;
                    var mousePressedEvent = (MousePressedEvent) event;
                    if (panel.select(mousePressedEvent))
                    {
                        activePanel = panel;
                        panels.remove(activePanel);
                        panels.addFirst(activePanel);
                        break;
                    }
                }
                return;
            } else activePanel = null;
        }

        if (Input.wasKeyPressed(event, KeyboardKey.KEY_LEFT_SHIFT)) splitting = true;
        if (Input.wasKeyReleased(event, KeyboardKey.KEY_LEFT_SHIFT)) splitting = false;
        if (Input.wasKeyPressed(event, KeyboardKey.KEY_R)) removing = true;
        if (Input.wasKeyReleased(event, KeyboardKey.KEY_R)) removing = false;

        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f clickPosition = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);

            if (splitting) // Split old area into old and new at click point
            {
                for (ListIterator<DockingArea> it = dockingAreas.listIterator(); it.hasNext(); )
                {
                    DockingArea dockingArea = it.next();

                    boolean intersected = Physics2D.pointInsideAABB(clickPosition, dockingArea.position, dockingArea.size);

                    if (intersected)
                    {
                        if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
                        {
                            Vector2f oldSize = new Vector2f(dockingArea.size);
                            dockingArea.setWidth(clickPosition.getX() - dockingArea.position.getX());
                            DockingArea newDockingArea = new DockingArea(
                                    new Vector2f(dockingArea.position.getX() + dockingArea.size.getX(), dockingArea.position.getY()),
                                    new Vector2f((dockingArea.position.getX() + oldSize.getX()) - clickPosition.getX(), dockingArea.size.getY())
                            );
                            it.add(newDockingArea);
                            break;
                        } else if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_2))
                        {
                            Vector2f oldSize = new Vector2f(dockingArea.size);
                            dockingArea.setHeight(clickPosition.getY() - dockingArea.position.getY());
                            DockingArea newDockingArea = new DockingArea(
                                    new Vector2f(dockingArea.position.getX(), dockingArea.position.getY() + dockingArea.size.getY()),
                                    new Vector2f(dockingArea.size.getX(), (dockingArea.position.getY() + oldSize.getY()) - clickPosition.getY())
                            );
                            it.add(newDockingArea);
                            break;
                        }
                    }
                }

                for (DockingArea dockingArea : dockingAreas)
                {
                    Logger.info(dockingAreas.indexOf(dockingArea));
                    dockingArea.position.print();
                    dockingArea.size.print();
                }

            } else if (removing) // Removed hovered area on click
            {
                boolean removed = false;
                DockingArea removedDockingArea = null;
                for (Iterator<DockingArea> it = dockingAreas.iterator(); it.hasNext();)
                {
                    DockingArea dockingArea = it.next();
                    if (Physics2D.pointInsideAABB(clickPosition, dockingArea.position, dockingArea.size))
                    {
                        removedDockingArea = dockingArea;
                        it.remove();
                        removed = true;
                    }

                    if (removed)
                    {
                        dockingArea.setPosition(Vector2f.add(dockingArea.position, new Vector2f(-removedDockingArea.size.getX(), 0f)));
                    }
                }

            } else if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1)) // Select an area for resizing
            {
                selectedDockingArea = null;
                for (DockingArea dockingArea : dockingAreas)
                {
                    if (Physics2D.pointInsideAABB(clickPosition, dockingArea.position, dockingArea.size))
                    {
                        Logger.info(dockingAreas.indexOf(dockingArea));
                        selectedDockingArea = dockingArea;
                        startingSize = selectedDockingArea.size.getX();
                        break;
                    }
                }
            }
        }

        // Resize all subsequent areas when selected is resized
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1))
        {
            if (selectedDockingArea != null)
            {
                float sizeDifference = selectedDockingArea.size.getX() - startingSize;
                Logger.info(sizeDifference);
                for (int index = dockingAreas.indexOf(selectedDockingArea) + 1; index < dockingAreas.size(); ++index)
                {
                    DockingArea current = dockingAreas.get(index);
                    current.setPosition(Vector2f.add(current.position, new Vector2f(sizeDifference, 0f)));
                }
                selectedDockingArea = null;
            }
        }

        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            // Resize width of selected
            if (selectedDockingArea != null)
            {
                selectedDockingArea.setWidth((float)mouseMovedEvent.xPos - selectedDockingArea.position.getX());
            }
        }
    }

    public void render()
    {
        for (DockingArea dockingArea : dockingAreas)
        {
            Renderer2D.drawQuad(dockingArea.position, dockingArea.size, dockingArea.color);
            // Render gizmo on area hovered by active panel
            Logger.info(activePanelMoving + " " + (activePanel == null));
            if (activePanel != null && Physics2D.pointInsideAABB(activePanel.getPosition(), dockingArea.position, dockingArea.size))
            {
                Logger.info(dockingAreas.indexOf(dockingArea));
                dockingArea.dockGizmo.render();
            }
        }
        for (DockablePanel panel : panels) panel.render();
    }

    private static class DockingArea
    {
        private Vector2f position, size;
        private Vector4f color;

        private DockGizmo dockGizmo;
        private boolean shouldDrawDockGizmo;

        private DockingArea(Vector2f position, Vector2f size)
        {
            this.position = position;
            this.size = size;
            dockGizmo = new DockGizmo(position, size);

            color = new Vector4f((float)Math.random(), (float)Math.random(), (float)Math.random(), 1f);
        }

        private void setPosition(Vector2f position)
        {
            this.position.set(position);
            dockGizmo.setPosition(this.position);
        }

        private void setWidth(float width)
        {
            size.setX(width);
            dockGizmo.setWidth(width);
        }

        private void setHeight(float height)
        {
            size.setY(height);
            dockGizmo.setHeight(height);
        }
    }
}
