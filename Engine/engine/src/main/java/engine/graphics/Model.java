package engine.graphics;

import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class Model
{
    private Mesh mesh;
    public Material material;

    private static Matrix4f modelMatrix = Matrix4f.scale(new Vector3f(4f, 4f, 1f));

    public Model(String modelPath)
    {
        mesh = new Mesh(ObjParser.load(modelPath));
    }

    public Model(Mesh mesh, Material material)
    {
        this.mesh = mesh;
        this.material = material;
    }

    public void render(@NotNull ForwardRenderer renderer, @NotNull PerspectiveCamera camera)
    {
        material.bind();
        material.getShader().setMatrix4f(modelMatrix, "model");
        for (int i = 0; i < renderer.sceneLights.getLights().size(); ++i)
        {
            material.getShader().setVector3f(renderer.sceneLights.getLights().get(i).getColor(), "lightColor[" + i + "]");
            material.getShader().setVector3f(renderer.sceneLights.getLights().get(i).getPosition(), "lightPosition[" + i + "]");
            material.getShader().setVector3f(camera.getPosition(), "viewPosition");
        }
        mesh.bind();
        mesh.draw();
    }
}
