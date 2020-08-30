package haraldr.scene;

import haraldr.event.Event;
import haraldr.main.Window;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;

//TODO: Not finished
public class OrthographicCamera extends Camera
{
    private float aspectRatio, width = 16f, height;

    public OrthographicCamera(float width, float height)
    {
        aspectRatio = width / height;
        this.height = this.width / aspectRatio;
        setPosition(new Vector3f(3f));
        calculateViewMatrix();
        calculateProjectionMatrix();
    }

    @Override
    public void onUpdate(float deltaTime, Window window)
    {
    }

    @Override
    public void onEvent(Event event, Window window)
    {
    }

    @Override
    public void calculateViewMatrix()
    {
        viewMatrix = Matrix4f.lookAt(position, new Vector3f(), Vector3f.UP);
    }

    @Override
    public void calculateProjectionMatrix()
    {
        projectionMatrix = Matrix4f.orthographic(-width, width, -height, height, -1f, 1f);
    }
}
