package oldgame.main;

import oldgame.graphics.RenderSystemType;

public class EntryPoint extends engine.main.EntryPoint
{
    public static RenderSystemType gameRenderSystemType;

    static
    {
        application = new GameApplication();
        argumentValidator = new GameArgumentValidator();
    }
}