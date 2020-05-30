package engine.graphics;

import engine.graphics.material.Material;
import engine.math.Matrix4f;

@SuppressWarnings("unused")
public class Model
{
    private VertexArray mesh;
    private Material material;
    private Matrix4f transformationMatrix;

    public Model(String modelPath, Material material)
    {
        this(ResourceManager.getMesh(modelPath), material, Matrix4f.IDENTITY);
    }

    public Model(String modelPath, Material material, Matrix4f transformationMatrix)
    {
        this(ResourceManager.getMesh(modelPath), material, transformationMatrix);
    }

    private Model(VertexArray mesh, Material material, Matrix4f transformationMatrix)
    {
        this.mesh = mesh;
        this.material = material;
        this.transformationMatrix = transformationMatrix;
    }

    public void setTransformationMatrix(Matrix4f transformationMatrix)
    {
        this.transformationMatrix = transformationMatrix;
    }

    public void render()
    {
        material.bind();
        material.getShader().setMatrix4f("model", transformationMatrix);
        material.getShader().setVector3f("viewPosition", Renderer3D.getCamera().getPosition());
        mesh.bind();
        mesh.drawElements();
    }

    public void renderTransformed(Matrix4f transformationMatrix)
    {
        material.bind();
        material.getShader().setMatrix4f("model", transformationMatrix);
        material.getShader().setVector3f("viewPosition", Renderer3D.getCamera().getPosition());
        mesh.bind();
        mesh.drawElements();
    }

    public void renderNoMaterial()
    {
        mesh.bind();
        mesh.drawElements();
    }

    public VertexArray getMesh()
    {
        return mesh;
    }

    public Material getMaterial()
    {
        return material;
    }

    public Matrix4f getTransformationMatrix()
    {
        return transformationMatrix;
    }

    public void delete()
    {
        mesh.delete();
    }
}