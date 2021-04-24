package haraldr.dockspace;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class DockGizmo
{
    private static final float GIZMO_SIZE = 50f;
    private static final Vector4f GIZMO_COLOR = new Vector4f(1f);

    private Vector2f parentPosition = new Vector2f(), parentSize = new Vector2f();

    public DockGizmo(Vector2f position, Vector2f size)
    {
        setPosition(position);
        setSize(size);
    }

    public void setPosition(Vector2f parentPosition)
    {
        this.parentPosition = parentPosition;
    }

    public void addPosition(Vector2f position)
    {
        parentPosition.add(position);
    }

    public void setSize(Vector2f parentSize)
    {
        this.parentSize = parentSize;
    }

    public DockPosition getDockPosition(Vector2f panelPosition)
    {
        final float HALF_GIZMO_SIZE = GIZMO_SIZE / 2f;
        if (Physics2D.pointInsideAABB(panelPosition, Vector2f.addX(parentPosition, parentSize.getX() / 2f - HALF_GIZMO_SIZE), new Vector2f(GIZMO_SIZE, HALF_GIZMO_SIZE)))
            return DockPosition.TOP;
        if (Physics2D.pointInsideAABB(panelPosition, Vector2f.add(parentPosition, new Vector2f(parentSize.getX() / 2f - HALF_GIZMO_SIZE, parentSize.getY() - HALF_GIZMO_SIZE)), new Vector2f(GIZMO_SIZE, HALF_GIZMO_SIZE)))
            return DockPosition.BOTTOM;
        if (Physics2D.pointInsideAABB(panelPosition, Vector2f.addY(parentPosition, parentSize.getY() / 2f - HALF_GIZMO_SIZE), new Vector2f(HALF_GIZMO_SIZE, GIZMO_SIZE)))
            return DockPosition.LEFT;
        if (Physics2D.pointInsideAABB(panelPosition, Vector2f.add(parentPosition, new Vector2f(parentSize.getX() - HALF_GIZMO_SIZE, parentSize.getY() / 2f - HALF_GIZMO_SIZE)), new Vector2f(HALF_GIZMO_SIZE, GIZMO_SIZE)))
            return DockPosition.RIGHT;
        if (Physics2D.pointInsideAABB(panelPosition, Vector2f.add(parentPosition, Vector2f.add(Vector2f.divide(parentSize, new Vector2f(2f)), new Vector2f(-HALF_GIZMO_SIZE))), new Vector2f(GIZMO_SIZE)))
            return DockPosition.CENTER;
        return DockPosition.NONE;
    }

    public void render(Batch2D batch)
    {
        final float HALF_GIZMO_SIZE = GIZMO_SIZE / 2f;
        batch.drawQuad(Vector2f.addX(parentPosition, parentSize.getX() / 2f - HALF_GIZMO_SIZE), new Vector2f(GIZMO_SIZE, HALF_GIZMO_SIZE), GIZMO_COLOR);
        batch.drawQuad(Vector2f.add(parentPosition, new Vector2f(parentSize.getX() / 2f - HALF_GIZMO_SIZE, parentSize.getY() - HALF_GIZMO_SIZE)), new Vector2f(GIZMO_SIZE, HALF_GIZMO_SIZE), GIZMO_COLOR);
        batch.drawQuad(Vector2f.addY(parentPosition, parentSize.getY() / 2f - HALF_GIZMO_SIZE), new Vector2f(HALF_GIZMO_SIZE, GIZMO_SIZE), GIZMO_COLOR);
        batch.drawQuad(Vector2f.add(parentPosition, new Vector2f(parentSize.getX() - HALF_GIZMO_SIZE, parentSize.getY() / 2f - HALF_GIZMO_SIZE)), new Vector2f(HALF_GIZMO_SIZE, GIZMO_SIZE), GIZMO_COLOR);
        batch.drawQuad(Vector2f.add(parentPosition, Vector2f.add(Vector2f.divide(parentSize, new Vector2f(2f)), new Vector2f(-HALF_GIZMO_SIZE))), new Vector2f(GIZMO_SIZE), GIZMO_COLOR);
    }
}
