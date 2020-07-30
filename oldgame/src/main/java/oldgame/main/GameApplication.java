package oldgame.main;

import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.main.Application;
import haraldr.main.Window;
import haraldr.math.Vector4f;
import oldgame.graphics.Models;
import oldgame.graphics.Shaders;
import oldgame.layer.WorldLayer;

public class GameApplication extends Application
{
    @Override
    public void start()
    {
        Window.WindowProperties windowProperties = new Window.WindowProperties(1280, 720, 0, false, false, false);
        init(windowProperties);
        loop();
    }

    @Override
    protected void init(Window.WindowProperties windowProperties)
    {
        super.init(windowProperties);
        layerStack.pushLayer(new WorldLayer());
        Renderer.setClearColor(0.2f, 0.6f, 0.65f, 1.0f);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        Models.dispose();
    }
}
