package engine.graphics;

import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Model
{
    private VertexArray mesh;
    private Material material;
    private Matrix4f transformationMatrix;

    public Model(String modelPath, Material material)
    {
        this(ObjParser.loadMesh(modelPath), material, Matrix4f.identity());
    }

    public Model(String modelPath, Material material, Matrix4f transformationMatrix)
    {
        this(ObjParser.loadMesh(modelPath), material, transformationMatrix);
    }

    public Model(VertexArray mesh, Material material)
    {
        this(mesh, material, Matrix4f.identity());
    }

    public Model(VertexArray mesh, Material material, Matrix4f transformationMatrix)
    {
        this.mesh = mesh;
        this.material = material;
        this.transformationMatrix = transformationMatrix;
    }

    public void setTransformationMatrix(Matrix4f transformationMatrix)
    {
        this.transformationMatrix = transformationMatrix;
    }

    public void render(@NotNull ForwardRenderer renderer)
    {
        material.bind();
        material.getShader().setMatrix4f(transformationMatrix, "model");
        material.getShader().setVector3f(renderer.getViewPosition(), "viewPosition");
        mesh.bind();
        mesh.drawElements();
    }
}