package oldgame.main;

import engine.main.ArgumentValidator;
import oldgame.graphics.RenderSystemType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameArgumentValidator implements ArgumentValidator
{
    @Override
    public void validateArguments(@NotNull List<String> args)
    {
        try
        {
            EntryPoint.gameRenderSystemType = RenderSystemType.validateRenderSystemArgument(args.get(0));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
