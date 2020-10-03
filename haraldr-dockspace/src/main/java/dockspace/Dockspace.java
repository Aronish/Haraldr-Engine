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
            DockingArea dockedArea = rootArea.getDockedArea(selectedPanel);
            if (dockedArea != null)
            {
                dockedArea.undock();
                selectedPanel = null;
                return;
            }
            rootArea.checkHovered(selectedPanel.getPosition());
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
        private DockPosition dockPosition;

        private DockGizmo dockGizmo;
        private DockingArea parent;
        private Map<DockPosition, DockingArea> children = new HashMap<>();
        private boolean dockable, hovered, vertical;

        private DockablePanel dockedPanel;

        private DockingArea(Vector2f position, Vector2f size)
        {
            this(position, size, false);
        }

        private DockingArea(Vector2f position, Vector2f size, boolean vertical)
        {
            this(position, size, true, null, DockPosition.NONE, vertical, null);
        }

        private DockingArea(Vector2f position, Vector2f size, boolean dockable, DockablePanel dockedPanel, DockPosition dockPosition, boolean vertical, DockingArea parent)
        {
            this.position = new Vector2f(position);
            this.dockable = dockable;
            this.dockedPanel = dockedPanel;
            this.dockPosition = dockPosition;
            this.parent = parent;
            this.size = new Vector2f(size);
            this.vertical = vertical;
            color = new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.1f);
            dockGizmo = new DockGizmo(position, size);
        }

        private boolean dockPanel(DockablePanel panel)
        {
            DockPosition dockPosition = dockGizmo.getDockPosition(panel.getPosition());
            if (hovered && dockable)
            {
                return switch (dockPosition) //TODO: Clean up
                {
                    case CENTER -> { //TODO: Let panels have own dockspaces and make center dock inside that panel
                        panel.setPosition(position);
                        panel.setSize(size);
                        dockable = false;
                        dockedPanel = panel;
                        this.dockPosition = DockPosition.CENTER;
                        yield true;
                    }
                    case LEFT -> {
                        Vector2f leftSize = Vector2f.divide(size, new Vector2f(2f, 1f));
                        DockingArea leftDockingArea = new DockingArea(position, leftSize, false, panel, DockPosition.LEFT, false, this);
                        children.putIfAbsent(DockPosition.LEFT, leftDockingArea);
                        children.putIfAbsent(DockPosition.RIGHT, new DockingArea(
                                Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                                Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f))
                        ));
                        panel.setPosition(leftDockingArea.position);
                        panel.setSize(leftDockingArea.size);

                        dockable = false;
                        yield true;
                    }
                    case RIGHT -> {
                        Vector2f leftSize = Vector2f.divide(size, new Vector2f(2f, 1f));
                        DockingArea rightDockingArea = new DockingArea(
                                Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                                Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f)),
                                false, panel, DockPosition.RIGHT, false, this
                        );
                        children.putIfAbsent(DockPosition.LEFT, new DockingArea(position, leftSize));
                        children.putIfAbsent(DockPosition.RIGHT, rightDockingArea);
                        panel.setPosition(rightDockingArea.position);
                        panel.setSize(rightDockingArea.size);

                        dockable = false;
                        yield true;
                    }
                    case TOP -> {
                        Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                        DockingArea topDockingArea = new DockingArea(position, topSize, false, panel, DockPosition.TOP, true, this);
                        children.putIfAbsent(DockPosition.TOP, topDockingArea);
                        children.putIfAbsent(DockPosition.BOTTOM, new DockingArea(
                                Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                                Vector2f.add(size, new Vector2f(0f, -topSize.getY())),
                                true
                        ));
                        panel.setPosition(topDockingArea.position);
                        panel.setSize(topDockingArea.size);

                        dockable = false;
                        yield true;
                    }
                    case BOTTOM -> {
                        Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                        DockingArea bottomDockingArea = new DockingArea(
                                Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                                Vector2f.add(size, new Vector2f(0f, -topSize.getY())),
                                false, panel, DockPosition.BOTTOM, true, this
                        );
                        children.putIfAbsent(DockPosition.TOP, new DockingArea(position, topSize, true));
                        children.putIfAbsent(DockPosition.BOTTOM, bottomDockingArea);
                        panel.setPosition(bottomDockingArea.position);
                        panel.setSize(bottomDockingArea.size);

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

        private void undock()
        {
            switch (dockPosition) //TODO: Clean up
            {
                case LEFT -> {
                    Map<DockPosition, DockingArea> rightChildren = parent.children.get(DockPosition.RIGHT).children;
                    if (rightChildren.size() == 0 && parent.children.get(DockPosition.RIGHT).dockedPanel == null)
                    {
                        parent.dockable = true;
                        parent.children.clear();
                    } else if (parent.children.get(DockPosition.RIGHT).dockedPanel != null)
                    {
                        parent.dockedPanel = parent.children.get(DockPosition.RIGHT).dockedPanel;
                        parent.dockedPanel.setPosition(parent.position);
                        parent.dockedPanel.setSize(parent.size);
                        parent.dockPosition = DockPosition.CENTER;
                        parent.children.clear();
                    } else
                    {
                        parent.children.clear();
                        parent.children.putAll(rightChildren);
                        for (DockingArea dockingArea : parent.children.values())
                        {
                            dockingArea.parent = parent;
                            dockingArea.onUndock(size, dockPosition);
                        }
                    }
                }
                case RIGHT -> {
                    Map<DockPosition, DockingArea> leftChildren = parent.children.get(DockPosition.LEFT).children;
                    if (leftChildren.size() == 0 && parent.children.get(DockPosition.LEFT).dockedPanel == null)
                    {
                        parent.dockable = true;
                        parent.children.clear();
                    } else if (parent.children.get(DockPosition.LEFT).dockedPanel != null)
                    {
                        parent.dockedPanel = parent.children.get(DockPosition.LEFT).dockedPanel;
                        parent.dockedPanel.setPosition(parent.position);
                        parent.dockedPanel.setSize(parent.size);
                        parent.dockPosition = DockPosition.CENTER;
                        parent.children.clear();
                    } else
                    {
                        parent.children.clear();
                        parent.children.putAll(leftChildren);
                        for (DockingArea dockingArea : parent.children.values())
                        {
                            dockingArea.parent = parent;
                            dockingArea.onUndock(size, dockPosition);
                        }
                    }
                }
                case TOP -> {
                    Map<DockPosition, DockingArea> bottomChildren = parent.children.get(DockPosition.BOTTOM).children;
                    if (bottomChildren.size() == 0 && parent.children.get(DockPosition.BOTTOM).dockedPanel == null)
                    {
                        parent.dockable = true;
                        parent.children.clear();
                    } else if (parent.children.get(DockPosition.BOTTOM).dockedPanel != null)
                    {
                        parent.dockedPanel = parent.children.get(DockPosition.BOTTOM).dockedPanel;
                        parent.dockedPanel.setPosition(parent.position);
                        parent.dockedPanel.setSize(parent.size);
                        parent.dockPosition = DockPosition.CENTER;
                        parent.children.clear();
                    } else
                    {
                        parent.children.clear();
                        parent.children.putAll(bottomChildren);
                        for (DockingArea dockingArea : parent.children.values())
                        {
                            dockingArea.parent = parent;
                            dockingArea.onUndock(size, dockPosition);
                        }
                    }
                }
                case BOTTOM -> {
                    Map<DockPosition, DockingArea> bottomChildren = parent.children.get(DockPosition.TOP).children;
                    if (bottomChildren.size() == 0 && parent.children.get(DockPosition.TOP).dockedPanel == null)
                    {
                        parent.dockable = true;
                        parent.children.clear();
                    } else if (parent.children.get(DockPosition.TOP).dockedPanel != null)
                    {
                        parent.dockedPanel = parent.children.get(DockPosition.TOP).dockedPanel;
                        parent.dockedPanel.setPosition(parent.position);
                        parent.dockedPanel.setSize(parent.size);
                        parent.dockPosition = DockPosition.CENTER;
                        parent.children.clear();
                    } else
                    {
                        parent.children.clear();
                        parent.children.putAll(bottomChildren);
                        for (DockingArea dockingArea : parent.children.values())
                        {
                            dockingArea.parent = parent;
                            dockingArea.onUndock(size, dockPosition);
                        }
                    }
                }
                case CENTER -> {
                    dockable = true;
                    dockedPanel = null;
                    dockPosition = DockPosition.NONE;
                }
            }
        }

        private DockingArea getDockedArea(DockablePanel panel)
        {
            if (dockedPanel != null && dockedPanel.equals(panel))
            {
                return this;
            }
            for (DockingArea dockingArea : children.values())
            {
                DockingArea candidate = dockingArea.getDockedArea(panel);
                if (candidate != null) return candidate;
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

        private void addPosition(Vector2f position)
        {
            if (dockedPanel != null)
            {
                dockedPanel.getPosition().add(position);
                dockGizmo.addPosition(position);
            }
            for (DockingArea dockingArea : children.values())
            {
                dockingArea.addPosition(position);
            }
        }

        private void onUndock(Vector2f undockedSize, DockPosition undockedPosition)
        {
            Logger.info(vertical);
            switch (undockedPosition)
            {
                case LEFT -> {
                    position.setX((position.getX() - undockedSize.getX()) * 2f);
                    size.setX(vertical ? undockedSize.getX() * 2f : undockedSize.getX());
                    dockGizmo.setPosition(position);
                    dockGizmo.setSize(size);
                    if (dockedPanel != null)
                    {
                        dockedPanel.setPosition(position);
                        dockedPanel.setSize(size);
                    }
                }
                case RIGHT -> {
                    position.setX(position.getX() * 2f);
                    size.setX(vertical ? undockedSize.getX() * 2f : undockedSize.getX());
                    dockGizmo.setPosition(position);
                    dockGizmo.setSize(size);
                    if (dockedPanel != null)
                    {
                        dockedPanel.setPosition(position);
                        dockedPanel.setSize(size);
                    }
                }
                case TOP -> {
                    position.setY((position.getY() - undockedSize.getY()) * 2f);
                    size.setY(vertical ? undockedSize.getY() : undockedSize.getY() * 2f);
                    dockGizmo.setPosition(position);
                    dockGizmo.setSize(size);
                    if (dockedPanel != null)
                    {
                        dockedPanel.setPosition(position);
                        dockedPanel.setSize(size);
                    }
                }
                case BOTTOM -> {
                    position.setY(position.getY() * 2f);
                    size.setY(vertical ? undockedSize.getY() : undockedSize.getY() * 2f);
                    dockGizmo.setPosition(position);
                    dockGizmo.setSize(size);
                    if (dockedPanel != null)
                    {
                        dockedPanel.setPosition(position);
                        dockedPanel.setSize(size);
                    }
                }
            }
            for (DockingArea dockingArea : children.values())
            {
                dockingArea.onUndock(undockedSize, undockedPosition);
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

        @Override
        public String toString()
        {
            return dockPosition.toString();
        }
    }
}
