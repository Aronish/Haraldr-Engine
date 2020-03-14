package engine.graphics;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Model
{
    private VertexArray mesh;
    private Material material;
    private Matrix4f modelMatrix;

    public Model(String modelPath, Material material)
    {
        this(ObjParser.load(modelPath), material, new Matrix4f());
    }

    public Model(String modelPath, Material material, Matrix4f modelMatrix)
    {
        this(ObjParser.load(modelPath), material, modelMatrix);
    }

    public Model(VertexArray mesh, Material material)
    {
        this(mesh, material, new Matrix4f());
    }

    public Model(VertexArray mesh, Material material, Matrix4f modelMatrix)
    {
        this.mesh = mesh;
        this.material = material;
        this.modelMatrix = modelMatrix;
    }

    public void render(@NotNull ForwardRenderer renderer)
    {
        List<Light> lights = renderer.getSceneLights().getLights();
        material.bind();
        material.getShader().setMatrix4f(modelMatrix, "model");
        material.getShader().setVector3f(renderer.getViewPosition(), "viewPosition");
        material.getShader().setFloat(lights.size(), "numPointLights");
        for (int i = 0; i < lights.size(); ++i)
        {
            material.getShader().setVector3f(lights.get(i).getPosition(), "pointLights[" + i + "].position");
            material.getShader().setVector3f(lights.get(i).getColor(), "pointLights[" + i + "].color");
            material.getShader().setFloat(1.0f, "pointLights[" + i + "].constant");
            material.getShader().setFloat(0.045f, "pointLights[" + i + "].linear");
            material.getShader().setFloat(0.0075f, "pointLights[" + i + "].quadratic");
        }
        mesh.bind();
        mesh.drawElements();
    }
}
