package engine.graphics;

import engine.graphics.lighting.SceneLights;
import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ForwardRenderer extends Renderer3D
{
    private final UniformBuffer matrixBuffer = new UniformBuffer(128);
    private SceneLights sceneLights = new SceneLights();

    public void setSceneLights(SceneLights sceneLights)
    {
        this.sceneLights = sceneLights;
    }

    public SceneLights getSceneLights()
    {
        return sceneLights;
    }

    @Override
    public void begin(@NotNull PerspectiveCamera camera)
    {
        viewPosition = camera.getPosition();
        matrixBuffer.bind(0);
        matrixBuffer.setDataUnsafe(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setDataUnsafe(Matrix4f.perspective.matrix, 64);
        sceneLights.bind();
    }

    public void dispose()
    {
        sceneLights.dispose();
        matrixBuffer.delete();
    }
}