package engine.main;

import engine.debug.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ArgumentValidator
{
    default void validateArguments(@NotNull List<String> args)
    {
        Logger.info("No argument validator, using default.");
        if (args.contains("MSAA"))
        {
            int level = Integer.parseInt(args.get(args.indexOf("MSAA") + 1));
            ProgramArguments.setArgument("MSAA", Integer.toString(level));
        }
    }
}
