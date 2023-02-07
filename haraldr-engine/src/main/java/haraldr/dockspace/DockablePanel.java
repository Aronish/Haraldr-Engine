package haraldr.dockspace;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;
import haraldr.ui.Font;
import haraldr.ui.TextLabel;
import haraldr.ui.UILayerStack;
import haraldr.ui.groups.UIComponentGroup;
import haraldr.ui.components.UILayerable;
import haraldr.ui.components.UIPositionable;
import org.jetbrains.annotations.Contract;

@SuppressWarnings({"WeakerAccess", "rawtypes"})
public abstract class DockablePanel<UIRootGroupType extends UIComponentGroup>
{
    protected static final Vector4f HEADER_COLOR = new Vector4f(0.15f, 0.15f, 0.15f, 1f);

    protected Vector2f position, size, headerSize;
    protected Vector4f color;
    protected boolean headerPressed, contentPressed, hovered;
    protected TextLabel name;

    protected UILayerStack uiLayerStack = new UILayerStack();
    protected UIRootGroupType uiRoot;

    private PanelDimensionChangeAction panelDimensionChangeAction = (position, size) -> {};

    public DockablePanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        this.name = uiLayerStack.getLayer(0).getTextBatch().createTextLabel(name, position, new Vector4f(1f));
        this.color = color;
        this.size = new Vector2f(size);
        headerSize = new Vector2f(size.getX(), Font.DEFAULT_FONT.getSize());
        this.position = new Vector2f(position);
        this.name.setPosition(position);
        uiLayerStack.refresh();
        panelDimensionChangeAction.run(
                Vector2f.addY(position, headerSize.getY()),
                Vector2f.addY(size, -headerSize.getY())
        );
        uiLayerStack.getLayer(0).addComponent(setupPanelModel());

        initializeUI();
        setPosition(position);
        setSize(size);
        draw();
    }

    public void setPanelDimensionChangeAction(PanelDimensionChangeAction panelDimensionChangeAction)
    {
        this.panelDimensionChangeAction = panelDimensionChangeAction;
    }

    protected PanelModel setupPanelModel()
    {
        return new PanelModel(this);
    }

    protected void initializeUI() {}

    public boolean onEvent(Event event, Window window)
    {
        if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
            headerPressed = Physics2D.pointInsideAABB(mousePoint, position, headerSize);
            contentPressed = Physics2D.pointInsideAABB(mousePoint, Vector2f.addY(position, headerSize.getY()), Vector2f.addY(size, -headerSize.getY()));
        }
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1))
        {
            contentPressed = headerPressed = false;
        }
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            Vector2f mousePoint = new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos);
            hovered = Physics2D.pointInsideAABB(mousePoint, position, size);
            if (headerPressed)
            {
                setPosition(mousePoint);
            }
        }
        UILayerable.UIEventResult eventResult = uiLayerStack.onEvent(event, window);
        if (eventResult.requiresRedraw()) draw();
        return headerPressed || contentPressed || eventResult.consumed();
    }

    protected void clear()
    {
        uiLayerStack.clear();
        uiLayerStack.getLayer(0).addComponent(0, new PanelModel(this));
        uiLayerStack.getLayer(0).getTextBatch().addTextLabel(name);
    }

    protected void draw()
    {
        uiLayerStack.draw();
    }

    public void render()
    {
        uiLayerStack.render();
    }

    public void addPosition(Vector2f difference)
    {
        this.position.add(difference);
        name.addPosition(difference);
        uiLayerStack.refresh();
        panelDimensionChangeAction.run(
                Vector2f.addY(this.position, headerSize.getY()),
                Vector2f.addY(size, -headerSize.getY())
        );
        draw();
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
        name.setPosition(position);
        uiLayerStack.refresh();
        panelDimensionChangeAction.run(
                Vector2f.addY(this.position, headerSize.getY()),
                Vector2f.addY(size, -headerSize.getY())
        );
        if (uiRoot != null) uiRoot.setPosition(Vector2f.addY(position, headerSize.getY()));
        draw();
    }

    public void setSize(Vector2f size)
    {
        this.size.set(size);
        headerSize.setX(size.getX());
        panelDimensionChangeAction.run(
                Vector2f.addY(position, headerSize.getY()),
                Vector2f.addY(size, -headerSize.getY())
        );
        if (uiRoot != null) uiRoot.setSize(Vector2f.addY(size, -headerSize.getY()));
        draw();
    }

    public void dispose() {}

    public UILayerStack getLayers()
    {
        return uiLayerStack;
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    public Vector2f getHeaderSize()
    {
        return headerSize;
    }

    public Vector4f getColor()
    {
        return color;
    }

    public boolean isHeaderPressed()
    {
        return headerPressed;
    }

    public boolean isContentPressed()
    {
        return contentPressed;
    }

    public boolean isHovered()
    {
        return hovered;
    }

    public interface PanelDimensionChangeAction
    {
        void run(Vector2f position, Vector2f size);
    }

    public static class PanelModel implements UILayerable
    {
        protected DockablePanel panel;

        @Contract(pure = true)
        protected PanelModel(DockablePanel panel)
        {
            this.panel = panel;
        }

        @Override
        public void draw(Batch2D batch)
        {
            batch.drawQuad(panel.position, panel.size, panel.color);
            batch.drawQuad(panel.position, panel.headerSize, HEADER_COLOR);
        }
    }
}
