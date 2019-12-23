package sandbox;

import engine.graphics.Renderer2D;
import engine.main.Application;

class ExampleApplication extends Application
{
    @Override
    public void start()
    {
        init(1280, 720, false, false);
        loop();
    }

    @Override
    protected void init(int windowWidth, int windowHeight, boolean fullscreen, boolean vSync)
    {
        super.init(windowWidth, windowHeight, fullscreen, vSync);
        Renderer2D.setClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        layerStack.pushLayers(new ExampleLayer("Ex"));
    }
}
