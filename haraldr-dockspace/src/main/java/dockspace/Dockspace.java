package dockspace;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.graphics.Renderer2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dockspace
{
    private static final Vector4f BACKGROUND_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);

    private Vector2f position;
    private Vector2f size;

    private List<DockablePanel> panels = new ArrayList<>();
    private DockablePanel selectedPanel;

    private DockingArea rootArea;

    public Dockspace(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;

        rootArea = new DockingArea(position, size);
    }

    public void addPanel(DockablePanel panel)
    {
        panels.add(panel);
    }

    public void onEvent(Event event, Window window)
    {
        for (DockablePanel panel : panels)
        {
            panel.onEvent(event, window);

            if (panel.isHeld())
            {
                selectedPanel = panel;
                break;
            }
        }

        if (selectedPanel != null && Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1))
        {
            rootArea.dockPanel(selectedPanel);
            selectedPanel = null;
        }

        if (selectedPanel != null && event.eventType == EventType.MOUSE_MOVED)
        {
            rootArea.checkHovered(selectedPanel.getPosition());
            Logger.info(rootArea.getDockedArea(selectedPanel));
        }
    }

    public void render()
    {
        Renderer2D.drawQuad(position, size, BACKGROUND_COLOR);
        panels.forEach(DockablePanel::render);
        if (selectedPanel != null) rootArea.render();
    }

    private static class DockingArea
    {
        private Vector2f position, size;
        private Vector4f color;

        private DockGizmo dockGizmo;
        private Map<DockPosition, DockingArea> children = new HashMap<>();
        private boolean dockable = true, hovered;

        private DockablePanel dockedPanel;

        private DockingArea(Vector2f position, Vector2f size)
        {
            this.position = position;
            this.size = size;
            color = new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.1f);
            dockGizmo = new DockGizmo(position, size);
        }

        private boolean dockPanel(DockablePanel panel)
        {
            DockPosition dockPosition = dockGizmo.getDockPosition(panel.getPosition());
            if (dockPosition == DockPosition.NONE)
            {
                dockedPanel = null;
            }
            if (hovered && dockable)
            {
                return switch (dockPosition)
                {
                    case CENTER -> {
                        panel.setPosition(position);
                        panel.setSize(size);
                        dockable = false;
                        yield true;
                    }
                    case LEFT -> {
                        Vector2f leftSize = Vector2f.divide(size, new Vector2f(2f, 1f));
                        DockingArea leftDockingArea = new DockingArea(position, leftSize);
                        children.putIfAbsent(DockPosition.LEFT, leftDockingArea);
                        children.putIfAbsent(DockPosition.RIGHT, new DockingArea(
                                Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                                Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f))
                        ));
                        panel.setPosition(leftDockingArea.position);
                        panel.setSize(leftDockingArea.size);
                        leftDockingArea.dockable = false;
                        leftDockingArea.dockedPanel = panel;

                        dockable = false;
                        yield true;
                    }
                    case RIGHT -> {
                        Vector2f leftSize = Vector2f.divide(size, new Vector2f(2f, 1f));
                        DockingArea rightDockingArea = new DockingArea(
                                Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                                Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f))
                        );
                        children.putIfAbsent(DockPosition.LEFT, new DockingArea(position, leftSize));
                        children.putIfAbsent(DockPosition.RIGHT, rightDockingArea);
                        panel.setPosition(rightDockingArea.position);
                        panel.setSize(rightDockingArea.size);
                        rightDockingArea.dockable = false;
                        rightDockingArea.dockedPanel = panel;

                        dockable = false;
                        yield true;
                    }
                    case TOP -> {
                        Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                        DockingArea topDockingArea = new DockingArea(position, topSize);
                        children.putIfAbsent(DockPosition.TOP, topDockingArea);
                        children.putIfAbsent(DockPosition.BOTTOM, new DockingArea(
                                Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                                Vector2f.add(size, new Vector2f(0f, -topSize.getY()))
                        ));
                        panel.setPosition(topDockingArea.position);
                        panel.setSize(topDockingArea.size);
                        topDockingArea.dockable = false;
                        topDockingArea.dockedPanel = panel;

                        dockable = false;
                        yield true;
                    }
                    case BOTTOM -> {
                        Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                        DockingArea bottomDockingArea = new DockingArea(
                                Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                                Vector2f.add(size, new Vector2f(0f, -topSize.getY()))
                        );
                        children.putIfAbsent(DockPosition.TOP, new DockingArea(position, topSize));
                        children.putIfAbsent(DockPosition.BOTTOM, bottomDockingArea);
                        panel.setPosition(bottomDockingArea.position);
                        panel.setSize(bottomDockingArea.size);
                        bottomDockingArea.dockable = false;
                        bottomDockingArea.dockedPanel = panel;

                        dockable = false;
                        yield true;
                    }
                    case NONE -> {
                        yield false;
                    }
                };
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.dockPanel(panel)) break;
                }
            }
            return false;
        }

        //TODO: Something is not working
        private DockingArea getDockedArea(DockablePanel panel)
        {
            if (dockedPanel != null && dockedPanel.equals(panel)) return this;
            for (DockingArea dockingArea : children.values())
            {
                dockingArea.getDockedArea(panel);
            }
            return null;
        }

        private void checkHovered(Vector2f panelPosition)
        {
            if (dockable)
            {
                hovered = Physics2D.pointInsideAABB(panelPosition, position, size);
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    dockingArea.checkHovered(panelPosition);
                }
            }
        }

        private void render()
        {
            if (hovered)
            {
                if (dockable)
                {
                    Renderer2D.drawQuad(position, size, color);
                    dockGizmo.render();
                } else
                {
                    for (DockingArea dockingArea : children.values())
                    {
                        dockingArea.render();
                    }
                }
            }
        }
    }
}
