package oldgame.main;

import engine.main.ArgumentValidator;
import oldgame.graphics.RenderSystemType;

public class GameArgumentValidator implements ArgumentValidator
{
    @Override
    public void validateArguments(String[] args) throws Exception
    {
        EntryPoint.gameRenderSystemType = RenderSystemType.validateRenderSystemArgument(args[0]);
    }
}
