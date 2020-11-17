package oldgame.main;

import haraldr.event.Event;
import haraldr.graphics.Renderer;
import haraldr.main.Application;
import haraldr.main.Window;
import oldgame.graphics.Models;

public class GameApplication extends Application
{
    public GameApplication()
    {
        super(new Window.WindowProperties("Old Game", 1280, 720, 0, false, false, true, false));
    }

    @Override
    public void clientDispose()
    {
        super.dispose();
        Models.dispose();
    }

    @Override
    protected void clientInit(Window window)
    {
        Renderer.setClearColor(0.2f, 0.6f, 0.65f, 1.0f);
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {

    }

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {

    }

    @Override
    protected void clientRender(Window window)
    {

    }
}
