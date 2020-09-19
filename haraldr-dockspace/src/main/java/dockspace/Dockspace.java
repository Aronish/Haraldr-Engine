package dockspace;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Dockspace
{
    private static final Vector4f BACKGROUND_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);

    private Vector2f position, size;
    private Vector2f availablePosition, availableSize;

    private LinkedList<DockablePanel> panelOrderedQueue = new LinkedList<>();
    private DockablePanel activePanel, hoveredPanel;
    private boolean activePanelMoving;

    private Map<DockPosition, List<DockedPanel>> dockedPanels = new HashMap<>();

    private DockGizmo dockGizmo;
    private boolean drawDockGizmo;

    public Dockspace(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;
        dockGizmo = new DockGizmo(position, size);

        availablePosition = position;
        availableSize = size;

        dockedPanels.put(DockPosition.TOP, new ArrayList<>());
        dockedPanels.put(DockPosition.BOTTOM, new ArrayList<>());
        dockedPanels.put(DockPosition.LEFT, new ArrayList<>());
        dockedPanels.put(DockPosition.RIGHT, new ArrayList<>());
    }

    public void addChild(DockablePanel panel)
    {
        panelOrderedQueue.add(panel);
    }

    public void onEvent(Event event, Window window)
    {
        for (DockablePanel panel : panelOrderedQueue)
        {
            if (activePanelMoving = panel.onEvent(event, window)) break;
        }
        if (Input.wasKeyPressed(event, KeyboardKey.KEY_TAB))
        {
            panelOrderedQueue.addFirst(panelOrderedQueue.removeLast());
        }
        // Try to select a panel
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            activePanel = null;
            for (DockablePanel panel : panelOrderedQueue)
            {
                if (panel.select(mousePressedEvent))
                {
                    activePanel = panel;
                    panelOrderedQueue.remove(activePanel);
                    panelOrderedQueue.addFirst(activePanel);
                    break;
                }
            }
        }
        // Detect if the selected panel hovers above another panel
        if (activePanel != null && event.eventType == EventType.MOUSE_MOVED)
        {
            /*
            hoveredPanel = null;
            for (DockablePanel panel : panelOrderedQueue)
            {
                if (selectedPanel.equals(panel)) continue;
                if (Physics2D.pointInsideAABB(selectedPanel.getPosition(), panel.getPosition(), panel.getSize()))
                {
                    hoveredPanel = panel;
                    hoveredPanel.setDrawDockGizmo(true);
                    break;
                }
                panel.setDrawDockGizmo(false);
            }
            */
            if (activePanelMoving) activePanel.setSize(new Vector2f(400f));
        }
        // Dock if possible and deselect
        if (event.eventType == EventType.MOUSE_RELEASED)
        {
            if (activePanel != null)
            {
                /*
                if (hoveredPanel == null)
                {
                    dockSelectedPanel();
                } else
                {
                    hoveredPanel.dockPanel(selectedPanel);
                }
                */
                dockPanel(activePanel);
            }
            activePanel = null;
        }
        drawDockGizmo = activePanel != null;
    }

    /**
     * Stores data about the state of a docked panel. Undocking releases the portion of docked space to the available area.
     */
    private void dockPanel(DockablePanel panel)
    {
        switch (dockGizmo.getDockPosition(panel.getPosition()))
        {
            case TOP -> {
                panel.setPosition(availablePosition);
                panel.setSize(Vector2f.divide(availableSize, new Vector2f(1f, 2f)));
                dockedPanels.get(DockPosition.TOP).add(new DockedPanel(panel, panel.getPosition(), panel.getSize()));
                availablePosition.addY(panel.getSize().getY());
                availableSize.addY(-panel.getSize().getY());
            }
            case BOTTOM -> {
                panel.setPosition(Vector2f.add(availablePosition, new Vector2f(0f, availableSize.getY() / 2f)));
                panel.setSize(Vector2f.divide(availableSize, new Vector2f(1f, 2f)));
                dockedPanels.get(DockPosition.BOTTOM).add(new DockedPanel(panel, panel.getPosition(), panel.getSize()));
                availableSize.addY(-panel.getSize().getY());
            }
            case LEFT -> {
                panel.setPosition(availablePosition);
                panel.setSize(Vector2f.divide(availableSize, new Vector2f(2f, 1f)));
                dockedPanels.get(DockPosition.LEFT).add(new DockedPanel(panel, panel.getPosition(), panel.getSize()));
                availablePosition.addX(panel.getSize().getX());
                availableSize.addX(-panel.getSize().getX());
            }
            case RIGHT -> {
                panel.setPosition(Vector2f.add(availablePosition, new Vector2f(availableSize.getX() / 2f, 0f)));
                panel.setSize(Vector2f.divide(availableSize, new Vector2f(2f, 1f)));
                dockedPanels.get(DockPosition.RIGHT).add(new DockedPanel(panel, panel.getPosition(), panel.getSize()));
                availableSize.addX(-panel.getSize().getX());
            }
            case CENTER -> {
                panel.setPosition(availablePosition);
                panel.setSize(availableSize);
            }
            case NONE ->
            {
                for (DockPosition dockPosition : dockedPanels.keySet())
                {
                    for (Iterator<DockedPanel> it = dockedPanels.get(dockPosition).iterator(); it.hasNext();)
                    {
                        DockedPanel dockedPanel = it.next();
                        if (dockedPanel.panel.equals(panel))
                        {
                            switch (dockPosition) // Undoes the removal of available area.
                            {
                                case TOP -> {
                                    availablePosition.addY(-dockedPanel.dockedSize.getY());
                                    availableSize.addY(dockedPanel.dockedSize.getY());
                                }
                                case BOTTOM -> {
                                    availableSize.addY(dockedPanel.dockedSize.getY());
                                }
                                case LEFT -> {
                                    availablePosition.addX(-dockedPanel.dockedSize.getX());
                                    availableSize.addX(dockedPanel.dockedSize.getX());
                                }
                                case RIGHT -> {
                                    availableSize.addX(dockedPanel.dockedSize.getX());
                                }
                            }
                            it.remove();
                        }
                    }
                }
            }
        }
        dockGizmo.setPosition(availablePosition);
        dockGizmo.setSize(availableSize);
        Logger.info("Available: ");
        availablePosition.print();
        availableSize.print();

        for (List<DockedPanel> dockedPanels : dockedPanels.values())
        {
            for (DockedPanel dockedPanel : dockedPanels)
            {
                Logger.info(dockedPanel.panel);
                dockedPanel.dockedPosition.print();
                dockedPanel.dockedSize.print();
            }
        }
    }

    public void render()
    {
        Renderer2D.drawQuad(position, size, BACKGROUND_COLOR);
        panelOrderedQueue.descendingIterator().forEachRemaining(DockablePanel::render);
        if (drawDockGizmo) dockGizmo.render();
    }

    private static class DockedPanel
    {
        private DockablePanel panel;
        private Vector2f dockedPosition, dockedSize;

        private DockedPanel(DockablePanel panel, Vector2f dockedPosition, Vector2f dockedSize)
        {
            this.panel = panel;
            this.dockedPosition = new Vector2f(dockedPosition);
            this.dockedSize = new Vector2f(dockedSize);
        }
    }
}
