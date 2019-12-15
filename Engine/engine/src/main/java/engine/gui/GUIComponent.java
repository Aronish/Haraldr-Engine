package engine.gui;

import engine.main.Window;
import engine.event.WindowResizedEvent;
import engine.graphics.VertexArray;
import engine.graphics.VertexBuffer;
import engine.gui.constraint.Constraint;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public abstract class GUIComponent
{
    protected Vector3f position;
    private Matrix4f modelMatrix;
    protected VertexArray vertexArray;
    private int windowWidth, windowHeight;

    protected Constraint constraint;

    public GUIComponent(Vector3f position, Constraint constraint, @NotNull Window window)
    {
        this.position = position;
        vertexArray = new VertexArray();
        this.constraint = constraint;
        windowWidth = window.getWidth();
        windowHeight = window.getHeight();
        calculateMatrix();
    }

    private void calculateMatrix()
    {
        modelMatrix = Matrix4f.transform(position, 0.0f, new Vector2f(1.0f), false);
    }

    protected float[] createVertexData()
    {
        return null;
    }

    public void onResize(@NotNull WindowResizedEvent windowResizedEvent)
    {
        windowWidth = windowResizedEvent.width;
        windowHeight = windowResizedEvent.height;
    }

    protected void setPositionX(float x)
    {
        position.setX(x);
        calculateMatrix();
    }

    protected void setPositionY(float y)
    {
        position.setY(y);
        calculateMatrix();
    }

    protected void setPosition(float x, float y)
    {
        position.set(x, y);
        calculateMatrix();
    }

    protected void setVertexBuffer(VertexBuffer vertexBuffer)
    {
        vertexArray.setVertexBuffer(vertexBuffer);
    }

    public float[] getMatrixArray()
    {
        return modelMatrix.matrix;
    }

    public VertexArray getVertexArray()
    {
        return vertexArray;
    }

    public abstract void draw();
}
