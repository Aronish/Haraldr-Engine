package engine.graphics;

import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

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
        material.bind();
        material.getShader().setMatrix4f(modelMatrix, "model");
        material.getShader().setVector3f(renderer.getViewPosition(), "viewPosition");
        mesh.bind();
        mesh.drawElements();
    }
}
