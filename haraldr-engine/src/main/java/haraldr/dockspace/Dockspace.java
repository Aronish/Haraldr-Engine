package haraldr.dockspace;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
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
    private Vector2f position, size;

    private LinkedList<DockablePanel> dockedPanels = new LinkedList<>(), undockedPanels = new LinkedList<>();
    private DockablePanel selectedPanel;
    private DockingArea rootArea;

    private Batch2D renderBatch = new Batch2D();

    public Dockspace(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;
        rootArea = new DockingArea(this.position, this.size);
        renderToBatch();
    }

    public void addPanel(DockablePanel panel)
    {
        undockedPanels.add(panel);
    }

    public void dockPanel(DockablePanel panel, DockPosition dockPosition)
    {
        if (undockedPanels.contains(panel) && rootArea.dockPanel(panel, dockPosition))
        {
            undockedPanels.remove(panel);
            dockedPanels.add(panel);
        }
    }

    public void resizePanel(DockablePanel panel, float size)
    {
        DockingArea dockedArea = rootArea.getDockedArea(panel);
        if (dockedArea != null && dockedArea.parent != null)
        {
            dockedArea.parent.resizeManually(size);
        }
    }

    public void onEvent(Event event, Window window)
    {
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            resize(windowResizedEvent.width, windowResizedEvent.height);
        }

        rootArea.onEvent(event, window); //Docking area resizing

        // Undocked panels are always on top
        boolean undockedPanelsConsumed = false;
        for (DockablePanel undockedPanel : undockedPanels)
        {
            boolean panelConsumedPress = undockedPanel.onEvent(event, window);
            if (panelConsumedPress) // The panel consumed the event
            {
                if (undockedPanel.isHeaderPressed()) selectedPanel = undockedPanel;
                // Only set panel on top if pressed somewhere
                undockedPanels.remove(undockedPanel);
                undockedPanels.addFirst(undockedPanel);
            }
            if (panelConsumedPress || undockedPanel.isHovered())
            {
                undockedPanelsConsumed = true;
                break;
            }
        }
        // Then send events to docked panels
        if (!undockedPanelsConsumed)
        {
            for (DockablePanel dockedPanel : dockedPanels)
            {
                boolean consumedEvent;
                if (consumedEvent = dockedPanel.onEvent(event, window) && dockedPanel.isHeaderPressed()) selectedPanel = dockedPanel;
                if (consumedEvent) break;
            }
        }

        // Try docking the selected panel
        if (selectedPanel != null && undockedPanels.contains(selectedPanel) && Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1))
        {
            if (rootArea.attemptDockPanel(selectedPanel))
            {
                undockedPanels.remove(selectedPanel);
                dockedPanels.addFirst(selectedPanel);
            }
            selectedPanel = null;
            renderToBatch();
        }

        if (selectedPanel != null && event.eventType == EventType.MOUSE_MOVED)
        {
            // Undock the selected panel if it is docked and is moved.
            DockingArea dockedArea = rootArea.getDockedArea(selectedPanel);
            if (dockedArea != null)
            {
                if (dockedArea.parent == null || !dockedArea.parent.resizing)
                {
                    dockedArea.undock();
                    dockedPanels.remove(selectedPanel);
                    undockedPanels.addFirst(selectedPanel);
                    selectedPanel.setSize(new Vector2f(300f));
                    selectedPanel = null;
                    return;
                }
            }
            rootArea.checkHovered(selectedPanel.getPosition());
            renderToBatch();
        }
    }

    public void resize(float width, float height)
    {
        this.size.set(width, height - position.getY());
        Vector2f scaleFactors = Vector2f.divide(size, rootArea.size);
        rootArea.scalePosition(scaleFactors, new Vector2f(0f, -(position.getY() * scaleFactors.getY() - position.getY())));
        rootArea.scaleSize(scaleFactors);
        rootArea.recalculateSplitArea();
        renderToBatch();
    }

    private void renderToBatch()
    {
        renderBatch.begin();
        renderBatch.drawQuad(position, size, new Vector4f(0.3f, 0.3f, 0.3f, 1f));
        if (selectedPanel != null) rootArea.render(renderBatch);
        renderBatch.end();
    }

    public void render()
    {
        renderBatch.render();
        for (Iterator<DockablePanel> it = dockedPanels.descendingIterator(); it.hasNext();)
        {
            DockablePanel dockablePanel = it.next();
            dockablePanel.render();
        }
        for (Iterator<DockablePanel> it = undockedPanels.descendingIterator(); it.hasNext();)
        {
            DockablePanel dockablePanel = it.next();
            dockablePanel.render();
        }
    }

    public void dispose()
    {
        dockedPanels.forEach(DockablePanel::dispose);
        undockedPanels.forEach(DockablePanel::dispose);
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
        private Map<DockPosition, DockingArea> children = new LinkedHashMap<>();
        private Vector2f splitAreaPosition = new Vector2f(), splitAreaSize = new Vector2f();
        private boolean dockable, hovered, resizing, vertical;

        private static final float SPLIT_AREA_SIZE = 10f;

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

        private List<DockingArea> firstResizingAreas, secondResizingAreas;

        private void onEvent(Event event, Window window)
        {
            // Resize at split point if it exists
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
                if (resizing = children.size() != 0 && Physics2D.pointInsideAABB(mousePoint, splitAreaPosition, splitAreaSize))
                {
                    firstResizingAreas = children.get(vertical ? DockPosition.TOP : DockPosition.LEFT).getSecondDockingAreas(vertical);
                    secondResizingAreas = children.get(vertical ? DockPosition.BOTTOM : DockPosition.RIGHT).getPrimaryDockingAreas(vertical);
                }
            }
            if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) resizing = false;
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                Vector2f mousePoint = new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos);

                if (!children.isEmpty() && Physics2D.pointInsideAABB(mousePoint, splitAreaPosition, splitAreaSize))
                {
                    window.setCursorType(vertical ? Window.CursorType.RESIZE_VERTICAL : Window.CursorType.RESIZE_HORIZONTAL);
                }

                if (resizing)
                {
                    if (firstResizingAreas != null && secondResizingAreas != null)
                    {
                        resize(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos));
                    }
                }
            }

            for (DockingArea dockingArea : children.values())
            {
                dockingArea.onEvent(event, window);
            }
        }

        /**
         * Resize this panel to a predetermined size.
         */
        private void resizeManually(float size)
        {
            if (children.size() != 0)
            {
                firstResizingAreas = children.get(vertical ? DockPosition.TOP : DockPosition.LEFT).getSecondDockingAreas(vertical);
                secondResizingAreas = children.get(vertical ? DockPosition.BOTTOM : DockPosition.RIGHT).getPrimaryDockingAreas(vertical);
                resize(new Vector2f(size));
            }
        }

        private void resize(Vector2f size) // Needs a Vector2f for resizing with events, where different values are input for x and y.
        {
            if (vertical)
            {
                float amount = size.getY() - splitAreaPosition.getY() + SPLIT_AREA_SIZE / 2f;
                splitAreaPosition.addY(amount);
                for (DockingArea dockingArea : firstResizingAreas)
                {
                    dockingArea.addSize(new Vector2f(0f, amount));
                    if (dockingArea.children.size() != 0 && !dockingArea.vertical)
                    {
                        dockingArea.splitAreaSize.addY(amount);
                    }
                }
                for (DockingArea dockingArea : secondResizingAreas)
                {
                    dockingArea.addPosition(new Vector2f(0f, amount));
                    dockingArea.addSize(new Vector2f(0f, -amount));
                    if (dockingArea.children.size() != 0 && !dockingArea.vertical)
                    {
                        dockingArea.splitAreaPosition.addY(amount);
                        dockingArea.splitAreaSize.addY(-amount);
                    }
                }
            } else
            {
                float amount = size.getX() - splitAreaPosition.getX() + SPLIT_AREA_SIZE / 2f;
                splitAreaPosition.addX(amount);
                for (DockingArea dockingArea : firstResizingAreas)
                {
                    dockingArea.addSize(new Vector2f(amount, 0f));
                    if (dockingArea.children.size() != 0 && dockingArea.vertical)
                    {
                        dockingArea.splitAreaSize.addX(amount);
                    }
                }
                for (DockingArea dockingArea : secondResizingAreas)
                {
                    dockingArea.addPosition(new Vector2f(amount, 0f));
                    dockingArea.addSize(new Vector2f(-amount, 0f));
                    if (dockingArea.children.size() != 0 && dockingArea.vertical)
                    {
                        dockingArea.splitAreaPosition.addX(amount);
                        dockingArea.splitAreaSize.addX(-amount);
                    }
                }
            }
        }

        /**
         * Adds position to this panel only. Used for resizing.
         */
        private void addPosition(Vector2f position)
        {
            this.position.add(position);
            dockGizmo.setPosition(this.position);
            if (dockedPanel != null) dockedPanel.setPosition(this.position);
        }

        /**
         * Adds position to this panel only. Used for resizing.
         */
        private void addSize(Vector2f size)
        {
            this.size.add(size);
            dockGizmo.setSize(this.size);
            if (dockedPanel != null) dockedPanel.setSize(this.size);
        }

        /**
         * Scales position of this panel and its children with scale factors. Used for undocking.
         */
        private void scalePosition(Vector2f scaleFactors, Vector2f offsets)
        {
            position.multiply(scaleFactors).add(offsets);
            dockGizmo.setPosition(position);
            if (dockedPanel != null) dockedPanel.setPosition(position);

            for (DockingArea dockingArea : children.values())
            {
                dockingArea.scalePosition(scaleFactors, offsets);
            }
        }

        /**
         * Scales size of this panel and its children with scale factors. Used for undocking.
         */
        private void scaleSize(Vector2f scaleFactors)
        {
            size.multiply(scaleFactors);
            dockGizmo.setSize(size);
            if (dockedPanel != null) dockedPanel.setSize(size);

            for (DockingArea dockingArea : children.values())
            {
                dockingArea.scaleSize(scaleFactors);
            }
        }

        /**
         * Updates split areas of panel and its children.
         */
        private void recalculateSplitArea()
        {
            if (children.size() == 0) return;
            if (vertical)
            {
                splitAreaPosition.set(Vector2f.addY(children.get(DockPosition.BOTTOM).position, -SPLIT_AREA_SIZE / 2f));
                splitAreaSize.setX(size.getX());
            } else
            {
                splitAreaPosition.set(Vector2f.addX(children.get(DockPosition.RIGHT).position, -SPLIT_AREA_SIZE / 2f));
                splitAreaSize.setY(size.getY());
            }

            for (DockingArea dockingArea : children.values())
            {
                dockingArea.recalculateSplitArea();
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
        private boolean attemptDockPanel(DockablePanel panel)
        {
            boolean docked = false;
            if (hovered && dockable)
            {
                return dockPanelToPosition(panel, dockGizmo.getDockPosition(panel.getPosition()));
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (docked = dockingArea.attemptDockPanel(panel)) break;
                }
            }
            return docked;
        }

        /**
         * Dock to specified position instead of testing for a dock position.
         * @param panel the panel to dock.
         * @param dockPosition the position to dock to.
         */
        private boolean dockPanel(DockablePanel panel, DockPosition dockPosition)
        {
            boolean docked = false;
            if (dockable)
            {
                return dockPanelToPosition(panel, dockPosition);
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (docked = dockingArea.dockPanel(panel, dockPosition)) break;
                }
            }
            return docked;
        }

        private boolean dockPanelToPosition(DockablePanel panel, DockPosition dockPosition)
        {
            return switch (dockPosition)
            {
                case CENTER -> {
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
                            Vector2f.addX(position, leftSize.getX()),
                            Vector2f.addX(size, -leftSize.getX()),
                            DockPosition.RIGHT, this
                    ));
                    panel.setPosition(leftDockingArea.position);
                    panel.setSize(leftDockingArea.size);

                    splitAreaPosition.set(leftDockingArea.position.getX() + leftDockingArea.size.getX() - SPLIT_AREA_SIZE / 2f, leftDockingArea.position.getY());
                    splitAreaSize.set(SPLIT_AREA_SIZE, leftDockingArea.size.getY());

                    vertical = false;
                    dockable = false;
                    yield true;
                }
                case RIGHT -> {
                    Vector2f leftSize = Vector2f.divide(size, new Vector2f(2f, 1f));
                    DockingArea rightDockingArea = new DockingArea(
                            Vector2f.addX(position, leftSize.getX()),
                            Vector2f.addX(size, -leftSize.getX()),
                            false, panel, DockPosition.RIGHT, this
                    );
                    children.putIfAbsent(DockPosition.LEFT, new DockingArea(position, leftSize, DockPosition.LEFT, this));
                    children.putIfAbsent(DockPosition.RIGHT, rightDockingArea);
                    panel.setPosition(rightDockingArea.position);
                    panel.setSize(rightDockingArea.size);

                    splitAreaPosition.set(rightDockingArea.position.getX() - SPLIT_AREA_SIZE / 2f, rightDockingArea.position.getY());
                    splitAreaSize.set(SPLIT_AREA_SIZE, rightDockingArea.size.getY());

                    vertical = false;
                    dockable = false;
                    yield true;
                }
                case TOP -> {
                    Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                    DockingArea topDockingArea = new DockingArea(position, topSize, false, panel, DockPosition.TOP, this);
                    children.putIfAbsent(DockPosition.TOP, topDockingArea);
                    children.putIfAbsent(DockPosition.BOTTOM, new DockingArea(
                            Vector2f.addY(position, topSize.getY()),
                            Vector2f.addY(size, -topSize.getY()),
                            DockPosition.BOTTOM, this
                    ));
                    panel.setPosition(topDockingArea.position);
                    panel.setSize(topDockingArea.size);

                    splitAreaPosition.set(topDockingArea.position.getX(), topDockingArea.position.getY() + topDockingArea.size.getY() - SPLIT_AREA_SIZE / 2f);
                    splitAreaSize.set(topDockingArea.size.getX(), SPLIT_AREA_SIZE);

                    vertical = true;
                    dockable = false;
                    yield true;
                }
                case BOTTOM -> {
                    Vector2f topSize = Vector2f.divide(size, new Vector2f(1f, 2f));
                    DockingArea bottomDockingArea = new DockingArea(
                            Vector2f.addY(position, topSize.getY()),
                            Vector2f.addY(size, -topSize.getY()),
                            false, panel, DockPosition.BOTTOM, this
                    );
                    children.putIfAbsent(DockPosition.TOP, new DockingArea(position, topSize, DockPosition.TOP, this));
                    children.putIfAbsent(DockPosition.BOTTOM, bottomDockingArea);
                    panel.setPosition(bottomDockingArea.position);
                    panel.setSize(bottomDockingArea.size);

                    splitAreaPosition.set(bottomDockingArea.position.getX(), bottomDockingArea.position.getY() - SPLIT_AREA_SIZE / 2f);
                    splitAreaSize.set(bottomDockingArea.size.getX(), SPLIT_AREA_SIZE);

                    vertical = true;
                    dockable = false;
                    yield true;
                }
                case NONE -> false;
            };
        }

        /**
         * Undocks this panel and shifts panels with children on opposite side one layer upwards in the tree.
         * Resizes areas with panels and resizing areas according to reclaimed area.
         * Don't ask me how this works, it is a bunch of small fixes that account for all cases clumped together in a mess.
         */
        private void undock()
        {
            if (dockPosition != DockPosition.CENTER)
            {
                DockingArea opposite = parent.children.get(dockPosition.getOpposite());
                Map<DockPosition, DockingArea> adjacentChildren = opposite.children;
                if (adjacentChildren.size() == 0) // Only one adjacent area
                {
                    if (opposite.dockedPanel != null)
                    {
                        // If there's a panel directly adjacent in the same parent, resize that to fit entire parent.
                        parent.dockedPanel = opposite.dockedPanel;
                        parent.dockedPanel.setPosition(parent.position);
                        parent.dockedPanel.setSize(parent.size);
                        parent.dockPosition = DockPosition.CENTER;
                        parent.dockable = false;
                        parent.children.clear();
                    } else
                    {
                        // If this is the only one, remove parent and resize its adjacent area to fit its parent.
                        if (parent.parent != null)
                        {
                            DockingArea parentOpposite = parent.parent.children.get(parent.dockPosition.getOpposite());
                            parent.parent.dockedPanel = parentOpposite.dockedPanel;
                            parent.parent.dockedPanel.setPosition(parent.parent.position);
                            parent.parent.dockedPanel.setSize(parent.parent.size);
                            parent.parent.dockPosition = DockPosition.CENTER;
                            parent.parent.dockable = true;
                            parent.parent.children.clear();
                        } else // Parent is rootArea
                        {
                            parent.dockable = true;
                            parent.dockPosition = DockPosition.NONE;
                            parent.dockedPanel = null;
                            parent.children.clear();
                        }
                    }
                } else // Two adjacent areas
                {
                    parent.children.clear();
                    parent.children.putAll(adjacentChildren);
                    parent.splitAreaPosition = opposite.splitAreaPosition;
                    parent.splitAreaSize = opposite.splitAreaSize;
                    parent.dockable = opposite.dockable;
                    // Expand areas with recovered space. Different positions and sizes depending on docking configuration. A bit ugly, but difficult to compact.
                    switch (dockPosition)
                    {
                        case LEFT -> {
                            float scaleFactor = parent.size.getX() / (parent.size.getX() - size.getX());
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                dockingArea.parent = parent;
                                dockingArea.scalePosition(new Vector2f(scaleFactor, 1f), new Vector2f(-size.getX() * scaleFactor, 0f));
                                dockingArea.scaleSize(new Vector2f(scaleFactor, 1f));
                                dockingArea.recalculateSplitArea();
                            }
                        }
                        case TOP -> {
                            float scaleFactor = parent.size.getY() / (parent.size.getY() - size.getY());
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                dockingArea.parent = parent;
                                dockingArea.scalePosition(new Vector2f(1f, scaleFactor), new Vector2f(0f, -size.getY() * scaleFactor));
                                dockingArea.scaleSize(new Vector2f(1f, scaleFactor));
                                dockingArea.recalculateSplitArea();
                            }
                        }
                        case RIGHT -> {
                            float scaleFactor = parent.size.getX() / (parent.size.getX() - size.getX());
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                dockingArea.parent = parent;
                                dockingArea.scalePosition(new Vector2f(scaleFactor, 1f), new Vector2f());
                                dockingArea.scaleSize(new Vector2f(scaleFactor, 1f));
                                dockingArea.recalculateSplitArea();
                            }
                        }
                        case BOTTOM -> {
                            float scaleFactor = parent.size.getY() / (parent.size.getY() - size.getY());
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                dockingArea.parent = parent;
                                dockingArea.scalePosition(new Vector2f(1f, scaleFactor), new Vector2f());
                                dockingArea.scaleSize(new Vector2f(1f, scaleFactor));
                                dockingArea.recalculateSplitArea();
                            }
                        }
                    }
                    parent.vertical = opposite.vertical;
                    parent.recalculateSplitArea();
                }
            } else // Area was in center of one of parent's child areas.
            {
                if (parent != null)
                {
                    DockingArea opposite = null;
                    for (DockingArea dockingArea : parent.children.values())
                    {
                        if (!dockingArea.equals(this))
                        {
                            opposite = dockingArea;
                        }
                    }
                    assert opposite != null;

                    parent.dockedPanel = opposite.dockedPanel;
                    parent.dockedPanel.setPosition(parent.position);
                    parent.dockedPanel.setSize(parent.size);
                    parent.dockPosition = DockPosition.CENTER;
                    parent.dockable = false;
                    parent.children.clear();
                } else // Area is rootArea
                {
                    dockable = true;
                    dockedPanel = null;
                    dockPosition = DockPosition.NONE;
                }
            }
        }

        /**
         * @return a list of all docking areas touching the split area of this area.
         */
        private List<DockingArea> getPrimaryDockingAreas(boolean baseVertical)
        {
            List<DockingArea> foundAreas = new ArrayList<>();
            foundAreas.add(this);
            if (vertical == baseVertical)
            {
                if (children.size() != 0)
                {
                    Iterator<DockingArea> iterator = children.values().iterator();
                    foundAreas.addAll(iterator.next().getPrimaryDockingAreas(baseVertical));
                }
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.children.size() != 0)
                    {
                        foundAreas.addAll(dockingArea.getPrimaryDockingAreas(baseVertical));
                    } else
                    {
                        foundAreas.add(dockingArea);
                    }
                }
            }
            return foundAreas;
        }

        /**
         * @return a list of all docking areas touching the split area of this area.
         */
        private List<DockingArea> getSecondDockingAreas(boolean baseVertical)
        {
            List<DockingArea> foundAreas = new ArrayList<>();
            foundAreas.add(this);
            if (vertical == baseVertical)
            {
                if (children.size() != 0)
                {
                    Iterator<DockingArea> iterator = children.values().iterator();
                    iterator.next();
                    foundAreas.addAll(iterator.next().getSecondDockingAreas(baseVertical));
                }
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.children.size() != 0)
                    {
                        foundAreas.addAll(dockingArea.getSecondDockingAreas(baseVertical));
                    } else
                    {
                        foundAreas.add(dockingArea);
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