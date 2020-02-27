package oldgame.main;

import engine.graphics.Renderer2D;
import engine.main.Application;
import engine.math.Vector4f;
import oldgame.graphics.Models;
import oldgame.graphics.Shaders;
import oldgame.layer.WorldLayer;

public class GameApplication extends Application
{
    @Override
    public void start()
    {
        init(1280, 720, false, false, false);
        loop();
    }

    @Override
    protected void init(int windowWidth, int windowHeight, boolean maximized, boolean fullscreen, boolean vSync)
    {
        super.init(windowWidth, windowHeight, maximized, fullscreen, vSync);
        layerStack.pushLayers(new WorldLayer("World"));
        Renderer2D.setClearColor(new Vector4f(0.2f, 0.6f, 0.65f, 1.0f));
    }

    @Override
    public void dispose()
    {
        super.dispose();
        Shaders.dispose();
        Models.dispose();
    }
}
