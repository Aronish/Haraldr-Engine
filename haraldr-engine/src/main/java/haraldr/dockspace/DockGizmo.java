package haraldr.dockspace;

import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class DockGizmo
{
    private static final float GIZMO_SIZE = 50f;
    private static final Vector4f GIZMO_COLOR = new Vector4f(1f);

    private Vector2f parentPosition = new Vector2f(), parentSize = new Vector2f();

    private Vector2f topPosition = new Vector2f(), topSize = new Vector2f();
    private Vector2f bottomPosition = new Vector2f(), bottomSize = new Vector2f();
    private Vector2f leftPosition = new Vector2f(), leftSize = new Vector2f();
    private Vector2f rightPosition = new Vector2f(), rightSize = new Vector2f();
    private Vector2f centerPosition = new Vector2f(), centerSize = new Vector2f();

    public DockGizmo(Vector2f position, Vector2f size)
    {
        setPosition(position);
        setSize(size);
        refreshDockDimensions();
    }

    private void refreshDockDimensions()
    {
        float halfGizmoSize = GIZMO_SIZE / 2f;
        topPosition = Vector2f.add(parentPosition, new Vector2f(parentSize.getX() / 2f - halfGizmoSize, 0f));
        topSize = new Vector2f(GIZMO_SIZE, halfGizmoSize);

        bottomPosition = Vector2f.add(parentPosition, new Vector2f(parentSize.getX() / 2f - halfGizmoSize, parentSize.getY() - halfGizmoSize));
        bottomSize = new Vector2f(GIZMO_SIZE, halfGizmoSize);

        leftPosition = Vector2f.add(parentPosition, new Vector2f(0f, parentSize.getY() / 2f - halfGizmoSize));
        leftSize = new Vector2f(halfGizmoSize, GIZMO_SIZE);

        rightPosition = Vector2f.add(parentPosition, new Vector2f(parentSize.getX() - halfGizmoSize, parentSize.getY() / 2f - halfGizmoSize));
        rightSize = new Vector2f(halfGizmoSize, GIZMO_SIZE);

        centerPosition = Vector2f.add(parentPosition, Vector2f.add(Vector2f.divide(parentSize, new Vector2f(2f)), new Vector2f(-halfGizmoSize)));
        centerSize = new Vector2f(GIZMO_SIZE);
    }

    public void setPosition(Vector2f parentPosition)
    {
        this.parentPosition = parentPosition;
        refreshDockDimensions();
    }

    public void addPosition(Vector2f position)
    {
        parentPosition.add(position);
        refreshDockDimensions();
    }

    public void setSize(Vector2f parentSize)
    {
        this.parentSize = parentSize;
        refreshDockDimensions();
    }

    public void setWidth(float width)
    {
        this.parentSize.setX(width);
        refreshDockDimensions();
    }

    public void setHeight(float height)
    {
        this.parentSize.setY(height);
        refreshDockDimensions();
    }

    public DockPosition getDockPosition(Vector2f panelPosition)
    {
        if (Physics2D.pointInsideAABB(panelPosition, topPosition, topSize))         return DockPosition.TOP;
        if (Physics2D.pointInsideAABB(panelPosition, bottomPosition, bottomSize))   return DockPosition.BOTTOM;
        if (Physics2D.pointInsideAABB(panelPosition, leftPosition, leftSize))       return DockPosition.LEFT;
        if (Physics2D.pointInsideAABB(panelPosition, rightPosition, rightSize))     return DockPosition.RIGHT;
        if (Physics2D.pointInsideAABB(panelPosition, centerPosition, centerSize))   return DockPosition.CENTER;
        return DockPosition.NONE;
    }

    public void render()
    {
        Renderer2D.drawQuad(topPosition, topSize, GIZMO_COLOR);
        Renderer2D.drawQuad(bottomPosition, bottomSize, GIZMO_COLOR);
        Renderer2D.drawQuad(leftPosition, leftSize, GIZMO_COLOR);
        Renderer2D.drawQuad(rightPosition, rightSize, GIZMO_COLOR);
        Renderer2D.drawQuad(centerPosition, centerSize, GIZMO_COLOR);
    }
}
