package haraldr.dockspace;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Dockspace
{
    private static final Vector4f BACKGROUND_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);

    private Vector2f position;
    private Vector2f size;

    private LinkedList<DockablePanel> panels = new LinkedList<>();
    private DockablePanel selectedPanel;
    private DockingArea rootArea;

    private Batch2D renderBatch = new Batch2D(); //TODO: Could do more dirty flagging, but not really necessary yet.

    public Dockspace(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;

        rootArea = new DockingArea(position, size);
        renderToBatch();
    }

    public void addPanel(DockablePanel panel)
    {
        panels.add(panel);
        renderToBatch();
    }

    public void onEvent(Event event, Window window)
    {
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            setSize(new Vector2f(windowResizedEvent.width, windowResizedEvent.height));
        }

        if (Input.wasKeyPressed(event, KeyboardKey.KEY_TAB))
        {
            panels.addFirst(panels.removeLast());
        }

        rootArea.onEvent(event, window);

        for (DockablePanel panel : panels)
        {
            panel.onEvent(event, window);

            if (panel.isHeld())
            {
                selectedPanel = panel;
                panels.remove(selectedPanel);
                panels.addFirst(selectedPanel);
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
            // Undock the selected panel if it is docked and is moved.
            DockingArea dockedArea = rootArea.getDockedArea(selectedPanel);
            if (dockedArea != null)
            {
                dockedArea.undock();
                selectedPanel.setSize(new Vector2f(300f));
                selectedPanel = null;
                return;
            }
            rootArea.checkHovered(selectedPanel.getPosition());
        }
        renderToBatch();
    }

    private void setSize(Vector2f size)
    {
        this.size.set(size);
        rootArea.setSize(this.size);
    }

    public void renderToBatch()
    {
        renderBatch.begin();
        renderBatch.drawQuad(position, size, BACKGROUND_COLOR);
        for (DockablePanel panel : panels)
        {
            panel.render(renderBatch);
        }
        if (selectedPanel != null) rootArea.render(renderBatch);
        renderBatch.end();
    }

    public void render()
    {
        renderBatch.render();
    }

    public void dispose()
    {
        renderBatch.dispose();
    }

    public Vector2f getSize()
    {
        return size;
    }

    /**
     * A node in a tree hierarchy of dockable areas. Can contain DockablePanels.
     */
    private static class DockingArea
    {
        private Vector2f position, size;
        private Vector4f color;
        private DockPosition dockPosition;

        private DockGizmo dockGizmo;
        private DockingArea parent;
        private Map<DockPosition, DockingArea> children = new HashMap<>();
        private boolean dockable, hovered, vertical, resizing; // vertical is used to add extra size in the perpendicular axis in certain undocking configurations.

        private DockablePanel dockedPanel;

        private DockingArea(Vector2f position, Vector2f size)
        {
            this(position, size, DockPosition.NONE, false, null);
        }

        private DockingArea(Vector2f position, Vector2f size, DockPosition dockPosition, DockingArea parent)
        {
            this(position, size, true, null, dockPosition, false, parent);
        }

        private DockingArea(Vector2f position, Vector2f size, DockPosition dockPosition, boolean vertical, DockingArea parent)
        {
            this(position, size, true, null, dockPosition, vertical, parent);
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

        private void onEvent(Event event, Window window)
        {
            // Resize at split point if it exists
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
                if (children.size() > 0)
                {
                    if (vertical)
                    {
                        DockingArea top = children.get(DockPosition.TOP);
                        resizing = Physics2D.pointInsideAABB(
                                mousePoint,
                                Vector2f.add(top.position, new Vector2f(0f, top.size.getY() - 20f)),
                                new Vector2f(top.size.getX(), 20f)
                        );
                    } else
                    {
                        DockingArea left = children.get(DockPosition.LEFT);
                        resizing = Physics2D.pointInsideAABB(
                                mousePoint,
                                Vector2f.add(left.position, new Vector2f(left.size.getX() - 20f, 0f)),
                                new Vector2f(20f, left.size.getY())
                        );
                    }
                }
            }
            if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) resizing = false;
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                if (resizing)
                {
                    if (vertical)
                    {
                        float difference = children.get(DockPosition.TOP).size.getY() - (float)((MouseMovedEvent) event).yPos - position.getY();
                        children.get(DockPosition.TOP).addSize(new Vector2f(0f, -difference));
                        children.get(DockPosition.BOTTOM).addSize(new Vector2f(0f, difference));
                        children.get(DockPosition.BOTTOM).addPosition(new Vector2f(0f, -difference));
                    } else
                    {
                        float difference = children.get(DockPosition.LEFT).size.getX() - (float)((MouseMovedEvent) event).xPos - position.getX();
                        children.get(DockPosition.LEFT).addSize(new Vector2f(-difference, 0f));
                        children.get(DockPosition.RIGHT).addSize(new Vector2f(difference, 0f));
                        children.get(DockPosition.RIGHT).addPosition(new Vector2f(-difference, 0f));
                    }
                }
            }

            for (DockingArea dockingArea : children.values())
            {
                dockingArea.onEvent(event, window);
            }
        }

        private void setPosition(Vector2f position)
        {
            this.position.set(position);
            dockGizmo.setPosition(this.position);
            if (dockedPanel != null)
            {
                dockedPanel.setPosition(position);
            }
        }

        private void setSize(Vector2f size)
        {
            this.size.set(size);
            dockGizmo.setSize(this.size);
            if (dockedPanel != null)
            {
                dockedPanel.setSize(size);
            }
        }

        private void addPosition(Vector2f position)
        {
            this.position.add(position);
            dockGizmo.setPosition(this.position);
            if (dockedPanel != null) dockedPanel.setPosition(this.position);

            for (DockingArea dockingArea : children.values())
            {
                if (dockingArea.dockPosition == DockPosition.RIGHT || dockingArea.dockPosition == DockPosition.BOTTOM)
                {
                    dockingArea.addPosition(Vector2f.divide(position, 2f));
                } else
                {
                    dockingArea.addPosition(position);
                }
            }
        }

        private void addSize(Vector2f size)
        {
            this.size.add(size);
            dockGizmo.setSize(this.size);
            if (dockedPanel != null) dockedPanel.setSize(this.size);

            for (DockingArea dockingArea : children.values())
            {
                dockingArea.addSize(Vector2f.divide(size, 2f));
            }
        }

        /**
         * Attempts to dock a panel to a certain position in the docking area currently hovered.
         * When docking, the area splits into two: One containing the panel and an empty one for the leftover area.
         * These become children of the area for which the docking attempt occurred.
         * Recurses through children until success.
         * @param panel the panel to dock.
         * @return whether it was docked successfully.
         */
        private boolean dockPanel(DockablePanel panel)
        {
            DockPosition dockPosition = dockGizmo.getDockPosition(panel.getPosition());
            if (hovered && dockable)
            {
                return switch (dockPosition)
                {
                    case CENTER -> { //TODO: Let panels have own dockspaces and make center dock inside that panel
                        panel.setPosition(position);
                        panel.setSize(size);
                        dockable = false;
                        dockedPanel = panel;
                        if (parent != null)
                        {
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                if (!dockingArea.equals(this))
                                {
                                    this.dockPosition = dockingArea.dockPosition.getOpposite();
                                    break;
                                }
                            }
                        } else
                        {
                            this.dockPosition = DockPosition.CENTER;
                        }
                        yield true;
                    }
                    case LEFT -> {
                        Vector2f leftSize = Vector2f.divide(size, new Vector2f(2f, 1f));
                        DockingArea leftDockingArea = new DockingArea(position, leftSize, false, panel, DockPosition.LEFT, false, this);
                        children.putIfAbsent(DockPosition.LEFT, leftDockingArea);
                        children.putIfAbsent(DockPosition.RIGHT, new DockingArea(
                                Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                                Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f)),
                                DockPosition.RIGHT, this
                        ));
                        panel.setPosition(leftDockingArea.position);
                        panel.setSize(leftDockingArea.size);

                        dockable = false;
                        vertical = false;
                        yield true;
                    }
                    case RIGHT -> {
                        Vector2f leftSize = Vector2f.divide(size, new Vector2f(2f, 1f));
                        DockingArea rightDockingArea = new DockingArea(
                                Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                                Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f)),
                                false, panel, DockPosition.RIGHT, false, this
                        );
                        children.putIfAbsent(DockPosition.LEFT, new DockingArea(position, leftSize, DockPosition.LEFT, this));
                        children.putIfAbsent(DockPosition.RIGHT, rightDockingArea);
                        panel.setPosition(rightDockingArea.position);
                        panel.setSize(rightDockingArea.size);

                        dockable = false;
                        vertical = false;
                        yield true;
                    }
                    case TOP -> {
                        Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                        DockingArea topDockingArea = new DockingArea(position, topSize, false, panel, DockPosition.TOP, true, this);
                        children.putIfAbsent(DockPosition.TOP, topDockingArea);
                        children.putIfAbsent(DockPosition.BOTTOM, new DockingArea(
                                Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                                Vector2f.add(size, new Vector2f(0f, -topSize.getY())),
                                DockPosition.BOTTOM, true, this
                        ));
                        panel.setPosition(topDockingArea.position);
                        panel.setSize(topDockingArea.size);

                        dockable = false;
                        vertical = true;
                        yield true;
                    }
                    case BOTTOM -> {
                        Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                        DockingArea bottomDockingArea = new DockingArea(
                                Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                                Vector2f.add(size, new Vector2f(0f, -topSize.getY())),
                                false, panel, DockPosition.BOTTOM, true, this
                        );
                        children.putIfAbsent(DockPosition.TOP, new DockingArea(position, topSize, DockPosition.TOP, true, this));
                        children.putIfAbsent(DockPosition.BOTTOM, bottomDockingArea);
                        panel.setPosition(bottomDockingArea.position);
                        panel.setSize(bottomDockingArea.size);

                        dockable = false;
                        vertical = true;
                        yield true;
                    }
                    case NONE -> false;
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

        /**
         * Undocks this panel and updates the opposite area on the same level and its children accordingly.
         */
        private void undock()
        {
            switch (dockPosition)
            {
                case LEFT, RIGHT, TOP, BOTTOM -> {
                    Map<DockPosition, DockingArea> adjacentChildren = parent.children.get(dockPosition.getOpposite()).children;
                    if (adjacentChildren.size() == 0 && parent.children.get(dockPosition.getOpposite()).dockedPanel == null)
                    {
                        parent.dockable = true;
                        parent.children.clear();
                    } else if (parent.children.get(dockPosition.getOpposite()).dockedPanel != null)
                    {
                        parent.dockedPanel = parent.children.get(dockPosition.getOpposite()).dockedPanel;
                        parent.dockedPanel.setPosition(parent.position);
                        parent.dockedPanel.setSize(parent.size);
                        parent.dockPosition = DockPosition.CENTER;
                        parent.children.clear();
                    } else
                    {
                        parent.children.clear();
                        parent.children.putAll(adjacentChildren);
                        for (DockingArea dockingArea : parent.children.values())
                        {
                            parent.vertical = dockingArea.vertical;
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

        /**
         * @return the area containing panel or null if not found.
         */
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

        /**
         * Checks if a dockable area is hovered.
         */
        private void checkHovered(Vector2f mousePosition)
        {
            if (dockable)
            {
                hovered = Physics2D.pointInsideAABB(mousePosition, position, size);
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    dockingArea.checkHovered(mousePosition);
                }
            }
        }

        /**
         * Updates dimensions of this area and its children when the area adjacent to parent has been undocked.
         * @param undockedSize the size of the undocked area.
         * @param undockedPosition the position of the undocked area in relation to its parent.
         */
        private void onUndock(Vector2f undockedSize, DockPosition undockedPosition)
        {
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

        private void render(Batch2D batch)
        {
            if (hovered)
            {
                if (dockable)
                {
                    batch.drawQuad(position, size, color);
                    dockGizmo.render(batch);
                } else
                {
                    for (DockingArea dockingArea : children.values())
                    {
                        dockingArea.render(batch);
                    }
                }
            }
        }
    }
}