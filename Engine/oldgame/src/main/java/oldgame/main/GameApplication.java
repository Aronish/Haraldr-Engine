package oldgame.main;

import engine.main.Application;
import oldgame.graphics.Models;
import oldgame.layer.WorldLayer;

public class GameApplication extends Application
{
    @Override
    protected void init()
    {
        super.init();
        layerStack.pushLayers
        (
                new WorldLayer("World")
        );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        Models.dispose();
    }
}