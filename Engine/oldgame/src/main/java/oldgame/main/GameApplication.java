package oldgame.main;

import engine.graphics.Renderer2D;
import engine.main.Application;
import oldgame.graphics.Models;
import oldgame.graphics.Shaders;
import oldgame.layer.WorldLayer;

public class GameApplication extends Application
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
        layerStack.pushLayers(new WorldLayer("World"));
        Renderer2D.setClearColor(0.2f, 0.6f, 0.65f, 1.0f);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        Shaders.dispose();
        Models.dispose();
    }
}
