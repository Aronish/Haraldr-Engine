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

    public void dockPanel(DockablePanel panel, DockPosition dockPosition)
    {
        rootArea.dockPanel(panel, dockPosition);
    }

    public void resizePanel(DockablePanel panel, float size)
    {
        DockingArea dockedArea = rootArea.getDockedArea(panel);
        if (dockedArea != null && dockedArea.parent != null)
        {
            dockedArea.parent.resize(size);
        }
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

        rootArea.onEvent(event);

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

        private void onEvent(Event event)
        {
            // Resize at split point if it exists
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
                if (resizing = children.size() != 0 && Physics2D.pointInsideAABB(mousePoint, splitAreaPosition, splitAreaSize))
                {
                    firstResizingAreas = children.get(vertical ? DockPosition.TOP : DockPosition.LEFT).getBottomRightMostArea(vertical);
                    secondResizingAreas = children.get(vertical ? DockPosition.BOTTOM : DockPosition.RIGHT).getTopLeftMostArea(vertical);
                }
            }
            if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) resizing = false;
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                if (resizing)
                {
                    if (firstResizingAreas != null && secondResizingAreas != null)
                    {
                        float difference;
                        if (vertical)
                        {
                            difference = (float) ((MouseMovedEvent) event).yPos - splitAreaPosition.getY();
                            splitAreaPosition.addY(difference);

                            for (DockingArea dockingArea : firstResizingAreas)
                            {
                                dockingArea.addSize(new Vector2f(0f, difference));
                            }
                            for (DockingArea dockingArea : secondResizingAreas)
                            {
                                dockingArea.addPosition(new Vector2f(0f, difference));
                                dockingArea.addSize(new Vector2f(0f, -difference));
                            }
                        } else
                        {
                            difference = (float) ((MouseMovedEvent) event).xPos - splitAreaPosition.getX();
                            splitAreaPosition.addX(difference);

                            for (DockingArea dockingArea : firstResizingAreas)
                            {
                                dockingArea.addSize(new Vector2f(difference, 0f));
                            }
                            for (DockingArea dockingArea : secondResizingAreas)
                            {
                                dockingArea.addPosition(new Vector2f(difference, 0f));
                                dockingArea.addSize(new Vector2f(-difference, 0f));
                            }
                        }
                    }
                }
            }

            for (DockingArea dockingArea : children.values())
            {
                dockingArea.onEvent(event);
            }
        }

        private void resize(float size)
        {
            if (children.size() != 0)
            {
                firstResizingAreas = children.get(vertical ? DockPosition.TOP : DockPosition.LEFT).getBottomRightMostArea(vertical);
                secondResizingAreas = children.get(vertical ? DockPosition.BOTTOM : DockPosition.RIGHT).getTopLeftMostArea(vertical);

                if (vertical)
                {
                    float amount = size - splitAreaPosition.getY() + SPLIT_AREA_SIZE / 2f;
                    splitAreaPosition.addY(amount);
                    for (DockingArea dockingArea : firstResizingAreas)
                    {
                        dockingArea.addSize(new Vector2f(0f, amount));
                    }
                    for (DockingArea dockingArea : secondResizingAreas)
                    {
                        dockingArea.addPosition(new Vector2f(0f, amount));
                        dockingArea.addSize(new Vector2f(0f, -amount));
                    }
                } else
                {
                    float amount = size - splitAreaPosition.getX() + SPLIT_AREA_SIZE / 2f;
                    splitAreaPosition.addX(amount);
                    for (DockingArea dockingArea : firstResizingAreas)
                    {
                        dockingArea.addSize(new Vector2f(amount, 0f));
                    }
                    for (DockingArea dockingArea : secondResizingAreas)
                    {
                        dockingArea.addPosition(new Vector2f(amount, 0f));
                        dockingArea.addSize(new Vector2f(-amount, 0f));
                    }
                }
            }
        }

        private void addPosition(Vector2f position)
        {
            this.position.add(position);
            dockGizmo.setPosition(this.position);
            splitAreaPosition.add(position);
            if (dockedPanel != null) dockedPanel.setPosition(this.position);
        }

        private void addSize(Vector2f size)
        {
            this.size.add(size);
            dockGizmo.setSize(this.size);
            if (dockedPanel != null) dockedPanel.setSize(this.size);
        }

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

        private boolean dockPanel(DockablePanel panel, DockPosition dockPosition)
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
                            Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                            Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f)),
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
                            Vector2f.add(position, new Vector2f(leftSize.getX(), 0f)),
                            Vector2f.add(size, new Vector2f(-leftSize.getX(), 0f)),
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
                            Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                            Vector2f.add(size, new Vector2f(0f, -topSize.getY())),
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
                            Vector2f.add(position, new Vector2f(0f, topSize.getY())),
                            Vector2f.add(size, new Vector2f(0f, -topSize.getY())),
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
         * Undocks this panel and updates the opposite area on the same level and its children accordingly.
         * Remember that this is called for the area that will disappear.
         */
        private void undock()
        {
            if (dockPosition != DockPosition.CENTER)
            {
                Map<DockPosition, DockingArea> adjacentChildren = parent.children.get(dockPosition.getOpposite()).children;
                DockingArea opposite = parent.children.get(dockPosition.getOpposite());
                if (adjacentChildren.size() == 0) // Only one adjacent area
                {
                    if (opposite.dockedPanel != null)
                    {
                        parent.dockedPanel = opposite.dockedPanel;
                        parent.dockedPanel.setPosition(parent.position);
                        parent.dockedPanel.setSize(parent.size);
                        parent.dockPosition = DockPosition.CENTER;
                        parent.children.clear();
                    }
                    parent.dockable = true;
                    parent.children.clear();
                } else // Two adjacent areas
                {
                    parent.children.clear();
                    parent.children.putAll(adjacentChildren);
                    parent.splitAreaPosition = opposite.splitAreaPosition;
                    parent.splitAreaSize = opposite.splitAreaSize;
                    // Expand areas with recovered space. A bit ugly, but difficult to compact.
                    // TODO: Shift split areas
                    switch (dockPosition)
                    {
                        case LEFT -> {
                            float scaleFactor = parent.size.getX() / (parent.size.getX() - size.getX());
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                dockingArea.parent = parent;
                                dockingArea.scalePosition(new Vector2f(scaleFactor, 1f), new Vector2f(-size.getX() * scaleFactor, 0f));
                                dockingArea.scaleSize(new Vector2f(scaleFactor, 1f));
                            }

                            parent.splitAreaPosition.setX(scaleFactor * (parent.splitAreaPosition.getX() + SPLIT_AREA_SIZE / 2f - size.getX()) - SPLIT_AREA_SIZE / 2f);
                            if (parent.vertical != opposite.vertical) parent.splitAreaSize.multiply(new Vector2f(2f, 1f));
                        }
                        case TOP -> {
                            float scaleFactor = parent.size.getY() / (parent.size.getY() - size.getY());
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                dockingArea.parent = parent;
                                dockingArea.scalePosition(new Vector2f(1f, scaleFactor), new Vector2f(0f, -size.getY() * scaleFactor));
                                dockingArea.scaleSize(new Vector2f(1f, scaleFactor));
                            }
                            parent.splitAreaPosition.setY(scaleFactor * (parent.splitAreaPosition.getY() + SPLIT_AREA_SIZE / 2f - size.getY()) - SPLIT_AREA_SIZE / 2f);
                            if (parent.vertical != opposite.vertical) parent.splitAreaSize.multiply(new Vector2f(1f, 2f));
                        }
                        case RIGHT -> {
                            float scaleFactor = parent.size.getX() / (parent.size.getX() - size.getX());
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                dockingArea.parent = parent;
                                dockingArea.scalePosition(new Vector2f(scaleFactor, 1f), new Vector2f());
                                dockingArea.scaleSize(new Vector2f(scaleFactor, 1f));
                            }
                            parent.splitAreaPosition.setX(scaleFactor * (parent.splitAreaPosition.getX() + SPLIT_AREA_SIZE / 2f) - SPLIT_AREA_SIZE / 2f);
                            if (parent.vertical != opposite.vertical) parent.splitAreaSize.multiply(new Vector2f(2f, 1f));
                        }
                        case BOTTOM -> {
                            float scaleFactor = parent.size.getY() / (parent.size.getY() - size.getY());
                            for (DockingArea dockingArea : parent.children.values())
                            {
                                dockingArea.parent = parent;
                                dockingArea.scalePosition(new Vector2f(1f, scaleFactor), new Vector2f());
                                dockingArea.scaleSize(new Vector2f(1f, scaleFactor));
                            }
                            parent.splitAreaPosition.setY(scaleFactor * (parent.splitAreaPosition.getY() + SPLIT_AREA_SIZE / 2f) - SPLIT_AREA_SIZE / 2f);
                            if (parent.vertical != opposite.vertical) parent.splitAreaSize.multiply(new Vector2f(1f, 2f));
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

        private List<DockingArea> getTopLeftMostArea(boolean baseVertical)
        {
            List<DockingArea> foundAreas = new ArrayList<>();
            foundAreas.add(this);
            if (vertical == baseVertical)
            {
                if (children.size() != 0)
                {
                    Iterator<DockingArea> iterator = children.values().iterator();
                    foundAreas.addAll(iterator.next().getTopLeftMostArea(baseVertical));
                }
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.children.size() != 0)
                    {
                        foundAreas.addAll(dockingArea.getTopLeftMostArea(baseVertical));
                    } else
                    {
                        foundAreas.add(dockingArea);
                    }
                }
            }
            return foundAreas;
        }

        private List<DockingArea> getBottomRightMostArea(boolean baseVertical)
        {
            List<DockingArea> foundAreas = new ArrayList<>();
            foundAreas.add(this);
            if (vertical == baseVertical)
            {
                if (children.size() != 0)
                {
                    Iterator<DockingArea> iterator = children.values().iterator();
                    iterator.next();
                    foundAreas.addAll(iterator.next().getBottomRightMostArea(baseVertical));
                }
            } else
            {
                for (DockingArea dockingArea : children.values())
                {
                    if (dockingArea.children.size() != 0)
                    {
                        foundAreas.addAll(dockingArea.getBottomRightMostArea(baseVertical));
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