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
        matrixBuffer.bind();
        matrixBuffer.setDataUnsafe(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setDataUnsafe(Matrix4f.perspective.matrix, 64);
    }

    /////CUBE//////////////////////////////

    public void drawCube()
    {
        drawCube(new Vector3f(), 1f);
    }

    public void drawCube(Vector3f position)
    {
        drawCube(position, 1f);
    }

    public void drawCube(Vector3f position, float scale)
    {
        Texture.DEFAULT_TEXTURE.bind(0);
        Shader.DIFFUSE.bind();
        Shader.DIFFUSE.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        Shader.DIFFUSE.setVector3f(viewPosition, "viewPosition");

        for (int i = 0; i < sceneLights.getLights().size(); ++i)
        {
            Light light = sceneLights.getLights().get(i);
            Shader.DIFFUSE.setVector3f(light.getPosition(), "pointLights[" + i + "].position");
            Shader.DIFFUSE.setVector3f(light.getColor(), "pointLights[" + i + "].color");
            Shader.DIFFUSE.setFloat(1.0f, "pointLights[" + i + "].constant");
            Shader.DIFFUSE.setFloat(0.7f, "pointLights[" + i + "].linear");
            Shader.DIFFUSE.setFloat(1.8f, "pointLights[" + i + "].quadratic");
        }
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }

    public void drawCube(Vector3f position, DiffuseMaterial material)
    {
        drawCube(position, 1f, material);
    }

    public void drawCube(Vector3f position, float scale, DiffuseMaterial material)
    {
        //TODO: Set material uniforms
        Texture.DEFAULT_TEXTURE.bind(0);
        Shader.DIFFUSE.bind();
        Shader.DIFFUSE.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        Shader.DIFFUSE.setVector3f(viewPosition, "viewPosition");

        for (int i = 0; i < sceneLights.getLights().size(); ++i)
        {
            Shader.DIFFUSE.setVector3f(sceneLights.getLights().get(i).getColor(), "lightColor[" + i + "]");
            Shader.DIFFUSE.setVector3f(sceneLights.getLights().get(i).getPosition(), "lightPosition[" + i + "]");
        }
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }
}