package oldgame.main;

import engine.main.ArgumentValidator;
import oldgame.graphics.RenderSystemType;
import org.jetbrains.annotations.NotNull;

public class GameArgumentValidator implements ArgumentValidator
{
    @Override
    public void validateArguments(@NotNull String[] args) throws Exception
    {
        EntryPoint.gameRenderSystemType = RenderSystemType.validateRenderSystemArgument(args[0]);
    }
}
