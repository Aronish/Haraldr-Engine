package engine.graphics;

import engine.event.MouseMovedEvent;
import engine.event.MousePressedEvent;
import engine.event.MouseReleasedEvent;
import engine.main.ArrayUtils;
import engine.main.OrthographicCamera;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static engine.main.Application.MAIN_LOGGER;

public class LineChainVertJoint
{
    private List<Vector3f> joints;
    private Matrix4f model;
    private VertexArray vertexArray = new VertexArray();
    private VertexBuffer vertexBuffer;
    private Vector4f color = new Vector4f(1f);

    public LineChainVertJoint(Vector3f position, @NotNull List<Vector3f> joints, float jointThickness)
    {
        model = Matrix4f.translate(position, false);
        this.joints = joints;
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int currentJoint = 0;
        for (Vector3f joint : joints)
        {
            vertices.add(joint.getX());
            vertices.add(joint.getY() + jointThickness);
            vertices.add(joint.getX());
            vertices.add(joint.getY() - jointThickness);
        }
        for (int i = 0; i < joints.size() - 1; ++i)
        {
            indices.add(currentJoint);
            indices.add(currentJoint + 1);
            indices.add(currentJoint + 2);
            indices.add(currentJoint + 1);
            indices.add(currentJoint + 3);
            indices.add(currentJoint + 2);
            currentJoint += 2;
        }
        VertexBufferLayout layout = new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2));
        vertexBuffer = new VertexBuffer(ArrayUtils.toPrimitiveArrayF(vertices), layout, false);
        vertexArray.setVertexBuffer(vertexBuffer);
        vertexArray.bind();
        vertexArray.setIndexBuffer(ArrayUtils.toPrimitiveArrayI(indices));
        vertexArray.unbind();
    }

    public void onClick(@NotNull MousePressedEvent event)
    {
        MAIN_LOGGER.info(event.toString());
    }

    public void onRelease(@NotNull MouseReleasedEvent event)
    {
        MAIN_LOGGER.info(event.toString());
    }

    public void onMouseDrag(@NotNull MouseMovedEvent event, @NotNull Window window, @NotNull OrthographicCamera camera)
    {
        //MAIN_LOGGER.info(event.toString());
        float x = (float) ((event.xPos / window.getWidth()) * 2 - 1) * Matrix4f.FIXED_ORTHOGRAPHIC_AXIS;
        float y = (float) ((event.yPos / window.getHeight()) * 2 - 1) * Matrix4f.dynamicOrthographicAxis;
        Vector3f worldSpace = Vector3f.add(new Vector3f(x, y), camera.getPosition());
        MAIN_LOGGER.info("Unit Space: " + worldSpace.getX() + " " + worldSpace.getY());
    }

    public void draw()
    {
        SceneData2D.defaultTexture.bind();
        Shader.DEFAULT2D.setMatrix4f(model, "model");
        Shader.DEFAULT2D.setMatrix4f(Renderer2D.sceneData.getViewMatrix(), "view");
        Shader.DEFAULT2D.setMatrix4f(Matrix4f.orthographic, "projection");
        Shader.DEFAULT2D.setVector4f(color, "color");

        vertexArray.bind();
        vertexArray.draw();
    }
}
