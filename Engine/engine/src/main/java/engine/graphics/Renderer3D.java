package engine.graphics;

import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

public class Renderer3D
{
    public static SceneData3D sceneData = new SceneData3D();

    public static void beginScene(@NotNull PerspectiveCamera camera)
    {
        sceneData.setViewMatrix(camera.getViewMatrix());
        Shader.DEFAULT3D.bind();
    }

    public static void drawCube(Vector3f position)
    {
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.translate(position, false), "model");
        Shader.DEFAULT3D.setMatrix4f(sceneData.getViewMatrix(), "view");
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.perspective, "projection");
        Shader.DEFAULT3D.setVector4f(new Vector4f(1.0f), "color");
        SceneData3D.CUBE.bind();
        SceneData3D.CUBE.draw();
    }
}