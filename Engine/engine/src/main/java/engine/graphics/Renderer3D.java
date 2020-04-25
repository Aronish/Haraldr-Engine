package engine.graphics;

import engine.main.PerspectiveCamera;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

@SuppressWarnings("unused")
public abstract class Renderer3D
{
    public static final VertexArray SCREEN_QUAD = new VertexArray();

    static
    {
        float[] quadVertexData =
                {
                        -1f,  1f,   0f, 1f,
                         1f,  1f,   1f, 1f,
                         1f, -1f,   1f, 0f,
                        -1f, -1f,   0f, 0f
                };
        VertexBuffer quadVertices = new VertexBuffer(
                quadVertexData,
                new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2), new VertexBufferElement(ShaderDataType.FLOAT2)),
                false
        );
        SCREEN_QUAD.setVertexBuffers(quadVertices);
        SCREEN_QUAD.setIndexBuffer(new int[] { 0, 3, 2, 0, 2, 1 });
    }

    protected static PerspectiveCamera perspectiveCamera = new PerspectiveCamera(); //TODO: Make camera base class.

    public static PerspectiveCamera getPerspectiveCamera()
    {
        return perspectiveCamera;
    }

    public static void clear(int mask)
    {
        glClear(mask);
    }

    public static void setClearColor(@NotNull Vector4f color)
    {
        glClearColor(color.getX(), color.getY(), color.getZ(), color.getW());
    }

    /////DEFAULT//////////
/*
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
*/
}
