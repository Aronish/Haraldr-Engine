package sandbox;

import engine.graphics.Renderer2D;
import engine.main.Application;
import engine.math.Vector4f;

class ExampleApplication extends Application
{
    @Override
    public void start()
    {
        init(1280, 720, true, false, false);
        loop();
    }

    @Override
    protected void init(int windowWidth, int windowHeight, boolean maximized, boolean fullscreen, boolean vSync)
    {
        super.init(windowWidth, windowHeight, maximized, fullscreen, vSync);
        Renderer2D.setClearColor(new Vector4f(0.1f, 0.1f, 0.2f, 1f));
        //layerStack.pushLayers(new TextureTestingLayer("TexTest"));
        layerStack.pushLayers(new LightCastersLayer("LightCasters"));
    }
}
