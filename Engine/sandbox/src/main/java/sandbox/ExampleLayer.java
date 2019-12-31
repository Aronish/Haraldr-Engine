package sandbox;

import engine.event.Event;
import engine.graphics.Renderer2D;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.layer.Layer;
import engine.main.OrthographicCamera;
import engine.main.Window;
import engine.math.Vector3f;
import engine.math.Vector4f;

public class ExampleLayer extends Layer
{
    private OrthographicCamera orthographicCamera = new OrthographicCamera();
    private Texture texture = new Texture("textures/pixel_test.png");

    public ExampleLayer(String name)
    {
        super(name);
    }

    @Override
    public void onEvent(Window window, Event event)
    {
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        EventHandler.processInput(orthographicCamera, window.getWindowHandle(), deltaTime);
    }

    @Override
    public void onRender()
    {
        Renderer2D.beginScene(orthographicCamera);
        Renderer2D.drawQuad(new Vector3f(), Shader.DEFAULT, new Vector4f(0.5f, 1.0f, 0.2f, 1.0f));
        Renderer2D.drawQuad(new Vector3f(3.0f, 4.0f), Shader.DEFAULT, texture, new Vector4f(1.0f, 0.2f, 0.5f, 1.0f));
    }
}
