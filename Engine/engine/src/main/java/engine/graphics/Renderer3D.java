package engine.graphics;

import engine.graphics.material.DiffuseMaterial;
import engine.graphics.material.Material;
import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Renderer3D
{
    protected Vector3f viewPosition = new Vector3f();

    public abstract void begin(PerspectiveCamera camera);

    public Vector3f getViewPosition()
    {
        return viewPosition;
    }

    /////DEFAULT//////////

    public void drawCube()
    {
        drawCube(Matrix4f.identity());
    }

    public void drawCube(Vector3f position)
    {
        drawCube(Matrix4f.translate(position));
    }

    public void drawCube(Vector3f position, float scale)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.scale(new Vector3f(scale))));
    }

    public void drawCube(Vector3f position, Vector3f rotationAxis, float rotation)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)));
    }

    public void drawCube(Vector3f position, float scale, Vector3f rotationAxis, float rotation)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)).multiply(Matrix4f.scale(new Vector3f(scale))));
    }

    public void drawCube(Matrix4f transformation)
    {
        DiffuseMaterial.DEFAULT.bind();
        Shader.DIFFUSE.setMatrix4f(transformation, "model");
        Shader.DIFFUSE.setVector3f(viewPosition, "viewPosition");
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }

    /////CUSTOM////////////////////////////////////////////////////////////

    public void drawCube(Vector3f position, Material customMaterial)
    {
        drawCube(Matrix4f.translate(position), customMaterial);
    }

    public void drawCube(Vector3f position, float scale, Material customMaterial)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.scale(new Vector3f(scale))), customMaterial);
    }

    public void drawCube(Vector3f position, Vector3f rotationAxis, float rotation, Material customMaterial)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)), customMaterial);
    }

    public void drawCube(Vector3f position, float scale, Vector3f rotationAxis, float rotation, Material customMaterial)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)).multiply(Matrix4f.scale(new Vector3f(scale))), customMaterial);
    }

    public void drawCube(Matrix4f transformation, @NotNull Material customMaterial)
    {
        customMaterial.bind();
        customMaterial.getShader().setMatrix4f(transformation, "model");
        customMaterial.getShader().setVector3f(viewPosition, "viewPosition");
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }
}
