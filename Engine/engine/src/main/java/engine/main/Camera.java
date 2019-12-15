package engine.main;

import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;

import org.jetbrains.annotations.NotNull;

/**
 * A virtual camera for the game. Is essentially a normal object except that the transformation matrix is inverted.
 * That matrix is then applied to all objects in the scene to make it appear as if the camera was moving.
 */
public class Camera
{
    private final float[] zooms = { 0.125f, 0.25f, 0.5f, 1.0f, 2.0f, 4.0f };

    private Matrix4f viewMatrix;

    protected Vector3f position;
    private int currentZoom = 2;
    protected float scale = zooms[currentZoom];
    private Vector2f scaleVector = new Vector2f();

    public Camera()
    {
        this(new Vector3f());
    }

    public Camera(Vector3f position)
    {
        this.position = position;
        calculateViewMatrix();
    }

    private void calculateViewMatrix()
    {
        viewMatrix = Matrix4f.transform(position, 0.0f, scaleVector.setBoth(scale), true);
    }

    public void zoomIn()
    {
        ++currentZoom;
        if (currentZoom >= zooms.length) currentZoom = zooms.length - 1;
        scale = zooms[currentZoom];
        calculateViewMatrix();
    }

    public void zoomOut()
    {
        --currentZoom;
        if (currentZoom < 0) currentZoom = 0;
        scale = zooms[currentZoom];
        calculateViewMatrix();
    }

    public void setPosition(@NotNull Vector3f position)
    {
        this.position = position.multiply(scale);
        calculateViewMatrix();
    }

    public void addPosition(Vector3f pos)
    {
        position.add(pos);
        calculateViewMatrix();
    }

    public void setScale(float scale)
    {
        this.scale = scale;
        scaleVector.setBoth(scale);
        calculateViewMatrix();
    }

    public Matrix4f getViewMatrix()
    {
        return viewMatrix;
    }

    public float getScale()
    {
        return scale;
    }
}
