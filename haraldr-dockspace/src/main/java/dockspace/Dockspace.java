package dockspace;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.MouseReleasedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Dockspace
{
    private static final Vector4f BACKGROUND_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);

    private Vector2f position, size;
    private boolean splitting, removing;

    private List<DockingArea> dockingAreas = new ArrayList<>(); // 0 contains 1, 1 contains 2
    private DockingArea selectedDockingArea;
    private float startingSize;

    private LinkedList<DockablePanel> panels = new LinkedList<>();
    private DockablePanel activePanel;
    private boolean activePanelMoving, activePanelDocked;

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
            if (activePanelMoving = panel.onEvent(event, window)) break;
        }

        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            activePanel = null;
            for (DockablePanel panel : panels)
            {
                if (panel.select(mousePressedEvent))
                {
                    activePanel = panel;
                    panels.remove(activePanel);
                    panels.addFirst(activePanel);
                    break;
                }
            }
        }

        if (event.eventType == EventType.MOUSE_MOVED)
        {
            if (activePanelMoving && activePanelDocked)
            {
                activePanelDocked = false;
                for (Iterator<DockingArea> it = dockingAreas.iterator(); it.hasNext();)
                {
                    DockingArea dockingArea = it.next();
                    if (dockingArea.dockedPanel.equals(activePanel))
                    {
                        //Merge the one it was docked in with the next in the list.
                        Logger.info(dockingArea.positionAtDock + " " + dockingAreas.indexOf(dockingArea));
                        break;
                    }
                }
            }
        }

        if (activePanel != null && event.eventType == EventType.MOUSE_RELEASED)
        {
            var mouseReleasedEvent = (MouseReleasedEvent) event;
            for (ListIterator<DockingArea> it = dockingAreas.listIterator(); it.hasNext();)
            {
                DockingArea dockingArea = it.next();
                if (Physics2D.pointInsideAABB(new Vector2f(mouseReleasedEvent.xPos, mouseReleasedEvent.yPos), dockingArea.position, dockingArea.size))
                {
                    DockingData dockingData = dockingArea.dockPanel(activePanel, window);
                    if (dockingData.docked)
                    {
                        activePanelDocked = true;
                        DockingArea newDockingArea = splitDockingArea(dockingArea, dockingData.dockPosition);
                        if (newDockingArea != null)
                        {
                            it.add(newDockingArea);
                        }
                    }
                }
            }
        }
/*
        // Rearranging docking areas, temporary
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

 */
    }

    private DockingArea splitDockingArea(DockingArea dockingArea, DockPosition dockPosition)
    {
        switch (dockPosition)
        {
            case TOP, BOTTOM -> {
                Vector2f oldSize = new Vector2f(dockingArea.size);
                dockingArea.setHeight(activePanel.getSize().getY() - dockingArea.position.getY());
                return new DockingArea(
                        new Vector2f(dockingArea.position.getX(), dockingArea.position.getY() + dockingArea.size.getY()),
                        new Vector2f(dockingArea.size.getX(), (dockingArea.position.getY() + oldSize.getY()) - activePanel.getSize().getY())
                );
            }
            case LEFT, RIGHT -> {
                Vector2f oldSize = new Vector2f(dockingArea.size);
                dockingArea.setWidth(activePanel.getSize().getX() - dockingArea.position.getX());
                return new DockingArea(
                        new Vector2f(dockingArea.position.getX() + dockingArea.size.getX(), dockingArea.position.getY()),
                        new Vector2f((dockingArea.position.getX() + oldSize.getX()) - activePanel.getSize().getX(), dockingArea.size.getY())
                );
            }
        }
        return null;
    }

    public void render()
    {
        for (DockingArea dockingArea : dockingAreas)
        {
            Renderer2D.drawQuad(dockingArea.position, dockingArea.size, dockingArea.color);
            // Render gizmo on area hovered by active panel
            if (activePanel != null && Physics2D.pointInsideAABB(activePanel.getPosition(), dockingArea.position, dockingArea.size))
            {
                dockingArea.dockGizmo.render();
            }
        }
        for (DockablePanel panel : panels) panel.render();
    }

    private static class DockingArea
    {
        private Vector2f position, size;
        private Vector4f color;

        private DockablePanel dockedPanel;
        private DockPosition positionAtDock;

        private DockGizmo dockGizmo;

        private DockingArea(Vector2f position, Vector2f size)
        {
            this.position = position;
            this.size = size;
            dockGizmo = new DockGizmo(position, size);

            color = new Vector4f((float)Math.random(), (float)Math.random(), (float)Math.random(), 1f);
        }

        private DockingData dockPanel(DockablePanel panel, Window window)
        {
            DockingData dockingData = switch (dockGizmo.getDockPosition(panel.getPosition()))
            {
                case TOP -> {
                    panel.setSize(Vector2f.divide(size, new Vector2f(1f, 2f)));
                    panel.setPosition(position);
                    yield new DockingData(true, DockPosition.TOP);
                }
                case BOTTOM -> {
                    panel.setSize(Vector2f.divide(size, new Vector2f(1f, 2f)));
                    panel.setPosition(Vector2f.add(position, new Vector2f(0f, window.getHeight() -panel.getSize().getY())));
                    yield new DockingData(true, DockPosition.BOTTOM);
                }
                case LEFT -> {
                    panel.setSize(Vector2f.divide(size, new Vector2f(2f, 1f)));
                    panel.setPosition(position);
                    yield new DockingData(true, DockPosition.LEFT);
                }
                case RIGHT -> {
                    panel.setSize(Vector2f.divide(size, new Vector2f(2f, 1f)));
                    panel.setPosition(Vector2f.add(position, new Vector2f(window.getWidth() -panel.getSize().getX(), 0f)));
                    yield new DockingData(true, DockPosition.RIGHT);
                }
                case CENTER -> {
                    panel.setPosition(position);
                    panel.setSize(size);
                    yield new DockingData(true, DockPosition.CENTER);
                }
                case NONE -> new DockingData(false, DockPosition.NONE);
            };
            if (dockingData.dockPosition != DockPosition.NONE) dockedPanel = panel;
            positionAtDock = dockingData.dockPosition;
            return dockingData;
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

    private static class DockingData
    {
        private boolean docked;
        private DockPosition dockPosition;

        @Contract(pure = true)
        public DockingData(boolean docked, DockPosition dockPosition)
        {
            this.docked = docked;
            this.dockPosition = dockPosition;
        }
    }
}
