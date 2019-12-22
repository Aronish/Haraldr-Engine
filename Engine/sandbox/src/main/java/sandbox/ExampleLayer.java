package sandbox;

import engine.event.Event;
import engine.graphics.Renderer2D;
import engine.graphics.Shader;
import engine.graphics.ShaderDataType;
import engine.graphics.VertexArray;
import engine.graphics.VertexBuffer;
import engine.graphics.VertexBufferElement;
import engine.graphics.VertexBufferLayout;
import engine.layer.Layer;
import engine.main.OrthograhpicCamera;
import engine.main.Window;
import engine.math.Vector3f;

public class ExampleLayer extends Layer
{
    private OrthograhpicCamera orthograhpicCamera = new OrthograhpicCamera();
    private VertexArray vertexArray;

    public ExampleLayer(String name)
    {
        super(name);
        float[] vertices = {
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.0f, 0.5f
        };
        int[] indices = {
                0, 1, 2
        };
        vertexArray = new VertexArray(indices);
        VertexBuffer buffer = new VertexBuffer(vertices, new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT2)
        ), false);
        vertexArray.setVertexBuffer(buffer);
    }

    @Override
    public void onEvent(Window window, Event event)
    {
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        EventHandler.processInput(orthograhpicCamera, window.getWindowHandle(), deltaTime);
    }

    @Override
    public void onRender()
    {
        Renderer2D.beginScene(orthograhpicCamera);
        Renderer2D.drawQuad(vertexArray, new Vector3f(), Shader.DEFAULT);
        Renderer2D.drawQuad(vertexArray, new Vector3f(3.0f, 4.0f), Shader.DEFAULT);
    }
}
