package haraldr.dockspace;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Dockspace
{
    private static final Vector4f BACKGROUND_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);

    private Vector2f position;
    private Vector2f size;

    private LinkedList<DockablePanel> panels = new LinkedList<>();
    private DockablePanel selectedPanel;
    private DockingArea rootArea;

    private Batch2D renderBatch = new Batch2D(); //Could do more dirty flagging, but not really necessary yet.

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
        //rootArea.setSize(this.size);
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

    public DockingArea getRootArea()
    {
        return rootArea;
    }

    /**
     * A node in a tree hierarchy of dockable areas. Can contain DockablePanels.
     */
    public static class DockingArea
    {
        private Vector2f position, size;
        private Vector4f color;
        private DockPosition dockPosition;

        private DockGizmo dockGizmo;
        private DockingArea parent;
        private Map<DockPosition, DockingArea> children = new LinkedHashMap<>();
        private Vector2f splitAreaPosition = new Vector2f(), splitAreaSize = new Vector2f();
        private boolean dockable, hovered, resizing, childrenPerpendicular, vertical;

        private DockablePanel dockedPanel;

        private DockingArea(Vector2f position, Vector2f size)
        {
            this(position, size, DockPosition.NONE, null);
        }

        private DockingArea(Vector2f position, Vector2f size, DockPosition dockPosition, DockingArea parent)
        {
            this(position, size, true, null, dockPosition, parent);
        }

        private DockingArea(Vector2f position, Vector2f size, boolean dockable, DockablePanel dockedPanel, DockPosition dockPosition, DockingArea parent)
        {
            this.position = new Vector2f(position);
            this.dockable = dockable;
            this.dockedPanel = dockedPanel;
            this.dockPosition = dockPosition;
            this.parent = parent;
            this.size = new Vector2f(size);
            color = new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.4f);
            dockGizmo = new DockGizmo(position, size);
        }

        private static List<DockingArea> leftResizingArea, rightResizingArea;

        private void onEvent(Event event, Window window)
        {
            // Resize at split point if it exists
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
                if (resizing = children.size() != 0 && Physics2D.pointInsideAABB(mousePoint, splitAreaPosition, splitAreaSize))
                {
                    leftResizingArea = children.get(DockPosition.LEFT).getRightMostArea(vertical);
                    rightResizingArea = children.get(DockPosition.RIGHT).getLeftMostArea(vertical);
                }
            }
            if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) resizing = false;
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                if (resizing)
                {
                    if (leftResizingArea != null && rightResizingArea != null)
                    {
                        float difference = (float) ((MouseMovedEvent) event).xPos - splitAreaPosition.getX();
                        splitAreaPosition.addX(difference);

                        for (DockingArea dockingArea : leftResizingArea)
                        {
                            dockingArea.addSize(new Vector2f(difference, 0f), vertical);
                        }
                        for (DockingArea dockingArea : rightResizingArea)
                        {
                            dockingArea.addPosition(new Vector2f(difference, 0f), vertical);
                            dockingArea.addSize(new Vector2f(-difference, 0f), vertical);
                        }
                    }
                }
            }

            for (DockingArea dockingArea : children.values())
            {
                dockingArea.onEvent(event, window);
            }
        }

        private void addPosition(Vector2f position, boolean baseVertical)
        {
            this.position.add(position);
            dockGizmo.setPosition(this.position);
            if (dockedPanel != null) dockedPanel.setPosition(this.position);

            if (children.size() > 0)
            {
                Iterator<DockingArea> iterator = children.values().iterator();
                iterator.next().addPosition(position, baseVertical);
                iterator.next().addPosition(vertical != baseVertical ? position : Vector2f.divide(position, 2f), baseVertical);
            }
        }

        private void addSize(Vector2f size, boolean baseVertical)
        {
            this.size.add(size);
            dockGizmo.setSize(this.size);
            if (dockedPanel != null) dockedPanel.setSize(this.size);

            if (children.size() > 0)
            {
                Iterator<DockingArea> iterator = children.values().iterator();
                iterator.next().addSize(vertical != baseVertical ? size : Vector2f.divide(size, 2f), baseVertical);
                iterator.next().addSize(vertical != baseVertical ? size : Vector2f.divide(size, 2f), baseVertical);
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
            if (hovered && dockable)
            {
                return dockPanelToPosition(panel, dockGizmo.getDockPosition(panel.getPosition()));
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.dockPanel(panel)) break;
                }
            }
            return false;
        }

        public boolean dockPanel(DockablePanel panel, DockPosition dockPosition)
        {
            if (dockable)
            {
                return dockPanelToPosition(panel, dockPosition);
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.dockPanel(panel, dockPosition)) break;
                }
            }
            return false;
        }

        private boolean dockPanelToPosition(DockablePanel panel, DockPosition dockPosition)
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
                    DockingArea leftDockingArea = new DockingArea(position, leftSize, false, panel, DockPosition.LEFT, this);
                    children.putIfAbsent(DockPosition.LEFT, leftDockingArea);
                    children.putIfAbsent(DockPosition.RIGHT, new DockingArea(
                            Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                            Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f)),
                            DockPosition.RIGHT, this
                    ));
                    panel.setPosition(leftDockingArea.position);
                    panel.setSize(leftDockingArea.size);

                    splitAreaPosition.set(leftDockingArea.position.getX() + leftDockingArea.size.getX() - 10f, leftDockingArea.position.getY());
                    splitAreaSize.set(20f, leftDockingArea.size.getY());

                    dockable = false;
                    childrenPerpendicular = parent != null && parent.childrenPerpendicular;
                    yield true;
                }
                case RIGHT -> {
                    Vector2f leftSize = Vector2f.divide(size, new Vector2f(2f, 1f));
                    DockingArea rightDockingArea = new DockingArea(
                            Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                            Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f)),
                            false, panel, DockPosition.RIGHT, this
                    );
                    children.putIfAbsent(DockPosition.LEFT, new DockingArea(position, leftSize, DockPosition.LEFT, this));
                    children.putIfAbsent(DockPosition.RIGHT, rightDockingArea);
                    panel.setPosition(rightDockingArea.position);
                    panel.setSize(rightDockingArea.size);

                    splitAreaPosition.set(rightDockingArea.position.getX() - 10f, rightDockingArea.position.getY());
                    splitAreaSize.set(20f, rightDockingArea.size.getY());

                    dockable = false;
                    childrenPerpendicular = parent != null && parent.childrenPerpendicular;
                    yield true;
                }
                case TOP -> {
                    Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                    DockingArea topDockingArea = new DockingArea(position, topSize, false, panel, DockPosition.TOP, this);
                    children.putIfAbsent(DockPosition.TOP, topDockingArea);
                    children.putIfAbsent(DockPosition.BOTTOM, new DockingArea(
                            Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                            Vector2f.add(size, new Vector2f(0f, -topSize.getY())),
                            DockPosition.BOTTOM, this
                    ));
                    panel.setPosition(topDockingArea.position);
                    panel.setSize(topDockingArea.size);

                    splitAreaPosition.set(topDockingArea.position.getX(), topDockingArea.position.getY() + topDockingArea.size.getY() - 10f);
                    splitAreaSize.set(topDockingArea.size.getX(), 20f);

                    vertical = true;
                    dockable = false;
                    childrenPerpendicular = parent == null || !parent.childrenPerpendicular;
                    yield true;
                }
                case BOTTOM -> {
                    Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                    DockingArea bottomDockingArea = new DockingArea(
                            Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                            Vector2f.add(size, new Vector2f(0f, -topSize.getY())),
                            false, panel, DockPosition.BOTTOM, this
                    );
                    children.putIfAbsent(DockPosition.TOP, new DockingArea(position, topSize, DockPosition.TOP, this));
                    children.putIfAbsent(DockPosition.BOTTOM, bottomDockingArea);
                    panel.setPosition(bottomDockingArea.position);
                    panel.setSize(bottomDockingArea.size);

                    splitAreaPosition.set(bottomDockingArea.position.getX(), bottomDockingArea.position.getY() - 10f);
                    splitAreaSize.set(bottomDockingArea.size.getX(), 20f);

                    vertical = true;
                    dockable = false;
                    childrenPerpendicular = parent == null || !parent.childrenPerpendicular;
                    yield true;
                }
                case NONE -> false;
            };
        }

        /**
         * Undocks this panel and updates the opposite area on the same level and its children accordingly.
         * Remember that this is called for the area that will disappear.
         */
        private void undock()
        {
            if (dockPosition != DockPosition.CENTER)
            {
                Map<DockPosition, DockingArea> adjacentChildren = parent.children.get(dockPosition.getOpposite()).children;
                if (adjacentChildren.size() == 0) // Only one adjacent area
                {
                    if (parent.children.get(dockPosition.getOpposite()).dockedPanel != null)
                    {
                        parent.dockedPanel = parent.children.get(dockPosition.getOpposite()).dockedPanel;
                        parent.dockedPanel.setPosition(parent.position);
                        parent.dockedPanel.setSize(parent.size);
                        parent.dockPosition = DockPosition.CENTER;
                        parent.children.clear();
                    }
                    parent.dockable = true;
                    parent.children.clear();
                } else // Two adjacent areas
                {
                    parent.childrenPerpendicular = parent.children.get(dockPosition.getOpposite()).childrenPerpendicular;
                    parent.children.clear();
                    parent.children.putAll(adjacentChildren);
                    // Expand areas with recovered space. A bit ugly, but difficult to compact.
                    switch (dockPosition)
                    {
                        case LEFT -> {
                            //TODO: Account for perpendicular adjacent children.
                            Iterator<DockingArea> iterator = parent.children.values().iterator();
                            // Left
                            DockingArea left = iterator.next();
                            left.parent = parent;
                            left.addPosition(new Vector2f(-size.getX(), 0f), parent.vertical);
                            left.addSize(new Vector2f(size.getX() / 2f, 0f), parent.vertical);
                            left.dockGizmo.setPosition(left.position);
                            left.dockGizmo.setSize(left.size);
                            if (left.dockedPanel != null)
                            {
                                left.dockedPanel.setPosition(left.position);
                                left.dockedPanel.setSize(left.size);
                            }
                            // Right
                            DockingArea right = iterator.next();
                            right.parent = parent;
                            right.addPosition(new Vector2f(-size.getX() / 2f, 0f), parent.vertical);
                            right.addSize(new Vector2f(size.getX() / 2f, 0f), parent.vertical);
                            right.dockGizmo.setPosition(right.position);
                            right.dockGizmo.setSize(right.size);
                            if (right.dockedPanel != null)
                            {
                                right.dockedPanel.setPosition(right.position);
                                right.dockedPanel.setSize(right.size);
                            }
                            parent.splitAreaPosition.addX(left.size.getX() - size.getX());
                        }
                        case TOP -> {
                            Iterator<DockingArea> iterator = parent.children.values().iterator();
                            // Left
                            DockingArea top = iterator.next();
                            top.parent = parent;
                            top.addPosition(new Vector2f(0f, -size.getY()), parent.vertical);
                            top.addSize(new Vector2f(0f, size.getY() / 2f), parent.vertical);
                            top.dockGizmo.setPosition(top.position);
                            top.dockGizmo.setSize(top.size);
                            if (top.dockedPanel != null)
                            {
                                top.dockedPanel.setPosition(top.position);
                                top.dockedPanel.setSize(top.size);
                            }
                            // Right
                            DockingArea bottom = iterator.next();
                            bottom.parent = parent;
                            bottom.addPosition(new Vector2f(0f, -size.getY() / 2f), parent.vertical);
                            bottom.addSize(new Vector2f(0f, size.getY() / 2f), parent.vertical);
                            bottom.dockGizmo.setPosition(bottom.position);
                            bottom.dockGizmo.setSize(bottom.size);
                            if (bottom.dockedPanel != null)
                            {
                                bottom.dockedPanel.setPosition(bottom.position);
                                bottom.dockedPanel.setSize(bottom.size);
                            }
                            parent.splitAreaPosition.addX(top.size.getX() - size.getX());
                        }
                        case RIGHT -> {
                            Iterator<DockingArea> iterator = parent.children.values().iterator();
                            // Left
                            DockingArea left = iterator.next();
                            left.parent = parent;
                            left.addSize(new Vector2f(size.getX() / 2f, 0f), parent.vertical);
                            left.dockGizmo.setPosition(left.position);
                            left.dockGizmo.setSize(left.size);
                            if (left.dockedPanel != null)
                            {
                                left.dockedPanel.setPosition(left.position);
                                left.dockedPanel.setSize(left.size);
                            }
                            // Right
                            DockingArea right = iterator.next();
                            right.parent = parent;
                            right.addPosition(new Vector2f(size.getX() / 2f, 0f), parent.vertical);
                            right.addSize(new Vector2f(size.getX() / 2f, 0f), parent.vertical);
                            right.dockGizmo.setPosition(right.position);
                            right.dockGizmo.setSize(right.size);
                            if (right.dockedPanel != null)
                            {
                                right.dockedPanel.setPosition(right.position);
                                right.dockedPanel.setSize(right.size);
                            }
                            parent.splitAreaPosition.addX(size.getX() - right.size.getX());
                        }
                        case BOTTOM -> {
                            Iterator<DockingArea> iterator = parent.children.values().iterator();
                            // Left
                            DockingArea top = iterator.next();
                            top.parent = parent;
                            top.addSize(new Vector2f(0f, size.getY() / 2f), parent.vertical);
                            top.dockGizmo.setPosition(top.position);
                            top.dockGizmo.setSize(top.size);
                            if (top.dockedPanel != null)
                            {
                                top.dockedPanel.setPosition(top.position);
                                top.dockedPanel.setSize(top.size);
                            }
                            // Right
                            DockingArea bottom = iterator.next();
                            bottom.parent = parent;
                            bottom.addPosition(new Vector2f(0f, size.getY() / 2f), parent.vertical);
                            bottom.addSize(new Vector2f(0f, size.getY() / 2f), parent.vertical);
                            bottom.dockGizmo.setPosition(bottom.position);
                            bottom.dockGizmo.setSize(bottom.size);
                            if (bottom.dockedPanel != null)
                            {
                                bottom.dockedPanel.setPosition(bottom.position);
                                bottom.dockedPanel.setSize(bottom.size);
                            }
                            parent.splitAreaPosition.addX(size.getX() - bottom.size.getX());
                        }
                    }
                }
            } else
            {
                dockable = true;
                dockedPanel = null;
                dockPosition = DockPosition.NONE;
            }
        }

        private List<DockingArea> getLeftMostArea(boolean baseVertical)
        {
            List<DockingArea> foundAreas = new ArrayList<>();
            if (vertical == baseVertical)
            {
                if (children.size() == 0)
                {
                    foundAreas.add(this);
                } else
                {
                    Iterator<DockingArea> iterator = children.values().iterator();
                    foundAreas.addAll(iterator.next().getLeftMostArea(baseVertical));
                }
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.children.size() == 0)
                    {
                        foundAreas.add(dockingArea);
                    } else
                    {
                        foundAreas.addAll(dockingArea.getLeftMostArea(baseVertical));
                    }
                }
            }
            return foundAreas;
        }

        private List<DockingArea> getRightMostArea(boolean baseVertical)
        {
            List<DockingArea> foundAreas = new ArrayList<>();
            if (vertical == baseVertical)
            {
                if (children.size() == 0)
                {
                    foundAreas.add(this);
                } else
                {
                    Iterator<DockingArea> iterator = children.values().iterator();
                    iterator.next();
                    foundAreas.addAll(iterator.next().getRightMostArea(baseVertical));
                }
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.children.size() == 0)
                    {
                        foundAreas.add(dockingArea);
                    } else
                    {
                        foundAreas.addAll(dockingArea.getRightMostArea(baseVertical));
                    }
                }
            }
            return foundAreas;
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

        private void render(Batch2D batch)
        {
            if (children.size() != 0) batch.drawQuad(splitAreaPosition, splitAreaSize, new Vector4f(1f));
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