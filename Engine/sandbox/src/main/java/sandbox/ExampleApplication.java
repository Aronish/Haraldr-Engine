package sandbox;

import engine.graphics.Renderer2D;
import engine.main.Application;
import engine.main.ProgramArguments;
import engine.main.Window;
import engine.math.Vector4f;

class ExampleApplication extends Application
{
    @Override
    public void start()
    {
        int samples = ProgramArguments.isArgumentSet("MSAA") ? Integer.parseInt(ProgramArguments.getStringValue("MSAA")) : 0;
        Window.WindowProperties windowProperties = new Window.WindowProperties(1280, 720, samples, true, false, false);
        init(windowProperties);
        loop();
    }

    @Override
    protected void init(Window.WindowProperties windowProperties)
    {
        super.init(windowProperties);
        Renderer2D.setClearColor(new Vector4f(0.1f, 0.1f, 0.2f, 1f));
        layerStack.pushLayer(new CubeMapLayer("CubeMaps"), window);
        //layerStack.pushLayers(new TextureTestingLayer("TexTest"), window);
        //layerStack.pushLayers(new LightCastersLayer("LightCasters"), window);
    }
}
