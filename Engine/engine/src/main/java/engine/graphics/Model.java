package engine.graphics;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class Model
{
    private VertexArray mesh;
    public Material material;

    private static Matrix4f modelMatrix = Matrix4f.scale(new Vector3f(4f, 4f, 1f));

    public Model(String modelPath)
    {
        mesh = ObjParser.load(modelPath);
    }

    public Model(VertexArray mesh, Material material)
    {
        this.mesh = mesh;
        this.material = material;
    }

    public void render(@NotNull ForwardRenderer renderer)
    {
        material.bind();
        material.getShader().setMatrix4f(modelMatrix, "model");
        material.getShader().setVector3f(renderer.getViewPosition(), "viewPosition");
        for (int i = 0; i < renderer.getSceneLights().getLights().size(); ++i)
        {
            material.getShader().setVector3f(renderer.getSceneLights().getLights().get(i).getColor(), "lightColor[" + i + "]");
            material.getShader().setVector3f(renderer.getSceneLights().getLights().get(i).getPosition(), "lightPosition[" + i + "]");
        }
        mesh.bind();
        mesh.drawElements();
    }
}
