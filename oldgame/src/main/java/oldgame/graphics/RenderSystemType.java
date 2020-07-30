package oldgame.graphics;

import java.util.Arrays;

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
        throw new IllegalArgumentException(String.format("Unknown render system argument %s! Expected %s", argument, Arrays.toString(values())));
    }
}
