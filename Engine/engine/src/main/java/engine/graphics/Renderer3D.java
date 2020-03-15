package engine.graphics;

import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Renderer3D
{
    private SceneLights sceneLights = new SceneLights();
    private Vector3f viewPosition = new Vector3f();
    private UniformBuffer matrixBuffer = new UniformBuffer(128);

    public SceneLights getSceneLights()
    {
        return sceneLights;
    }

    public Vector3f getViewPosition()
    {
        return viewPosition;
    }

    public void begin(@NotNull PerspectiveCamera camera)
    {
        viewPosition = camera.getPosition();
        matrixBuffer.bind(0);
        matrixBuffer.setDataUnsafe(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setDataUnsafe(Matrix4f.perspective.matrix, 64);
        sceneLights.bind();
    }

    /////CUBE//////////////////////////////

    public void drawCube()
    {
        drawCube(new Vector3f(), 1f, new Vector3f(), 0f);
    }

    public void drawCube(Vector3f position)
    {
        drawCube(position, 1f, new Vector3f(), 0f);
    }

    public void drawCube(Vector3f position, float scale, Vector3f rotationAxis, float rotation)
    {
        Texture.DEFAULT_TEXTURE.bind(0);
        Shader.DIFFUSE.bind();
        Shader.DIFFUSE.setMatrix4f(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        Shader.DIFFUSE.setVector3f(viewPosition, "viewPosition");
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }

    public void drawCube(Vector3f position, DiffuseMaterial material)
    {
        drawCube(position, 1f, material);
    }

    public void drawCube(Vector3f position, float scale, DiffuseMaterial material)
    {
        //TODO: Material
        Texture.DEFAULT_TEXTURE.bind(0);
        Shader.DIFFUSE.bind();
        Shader.DIFFUSE.setMatrix4f(Matrix4f.translate(position).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        Shader.DIFFUSE.setVector3f(viewPosition, "viewPosition");
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }
}