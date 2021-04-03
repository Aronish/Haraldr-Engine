package haraldr.dockspace;

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
import haraldr.ui.TextLabel;
import haraldr.ui.UIEventLayer;
import haraldr.ui.UILayerStack;
import haraldr.ui.components.UIContainer;
import haraldr.ui.components.UILayerable;

@SuppressWarnings("WeakerAccess")
public abstract class DockablePanel
{
    private static final float HEADER_SIZE = 20f;
    protected static final Vector4f HEADER_COLOR = new Vector4f(0.15f, 0.15f, 0.15f, 1f);

    protected Vector2f position, size, headerSize;
    protected Vector4f color;
    protected boolean headerPressed, contentPressed, hovered;
    protected TextLabel name;

    protected UILayerStack uiLayerStack = new UILayerStack();
    protected UIEventLayer mainLayer = new UIEventLayer();

    private PanelDimensionChangeAction panelDimensionChangeAction = (position, size) -> {};

    public DockablePanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        uiLayerStack.addLayer(mainLayer);

        this.position = new Vector2f(position);
        headerSize = new Vector2f(size.getX(), HEADER_SIZE);
        this.size = size;
        this.color = color;
        this.name = mainLayer.getTextBatch().createTextLabel(name, position, new Vector4f(1f));
        draw();
    }

    public boolean onEvent(Event event, Window window)
    {
        if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
            headerPressed = Physics2D.pointInsideAABB(mousePoint, position, headerSize);
            contentPressed = Physics2D.pointInsideAABB(mousePoint, Vector2f.addY(position, HEADER_SIZE), Vector2f.addY(size, -HEADER_SIZE));
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
        if (uiLayerStack.onEvent(event, window).requiresRedraw()) draw();
        return headerPressed || contentPressed;
    }

    protected void draw()
    {
        uiLayerStack.draw();
    }

    public void render()
    {
        uiLayerStack.render();
    }

    public void setPanelDimensionChangeAction(PanelDimensionChangeAction panelDimensionChangeAction)
    {
        this.panelDimensionChangeAction = panelDimensionChangeAction;
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

    public float getHeaderHeight()
    {
        return headerSize.getY();
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

    public class PanelModel implements UILayerable
    {
        @Override
        public void draw(Batch2D batch)
        {
            batch.drawQuad(position, size, color);
            batch.drawQuad(position, headerSize, HEADER_COLOR);
        }
    }
}
