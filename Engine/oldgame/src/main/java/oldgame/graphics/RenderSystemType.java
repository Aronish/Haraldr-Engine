package oldgame.graphics;

import static engine.main.Application.MAIN_LOGGER;

public enum RenderSystemType
{
    INSTANCING,
    MULTI_DRAW;

    public static RenderSystemType validateRenderSystemArgument(String argument) throws Exception
    {
        for (RenderSystemType renderSystemType : RenderSystemType.values())
        {
            if (renderSystemType.toString().equals(argument))
            {
                return renderSystemType;
            }
        }
        MAIN_LOGGER.fatal(new IllegalArgumentException("Unknown render system argument!"));
        return null;
    }
}
