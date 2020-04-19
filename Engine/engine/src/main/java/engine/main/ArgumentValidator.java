package engine.main;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static engine.main.Application.MAIN_LOGGER;

public interface ArgumentValidator
{
    default void validateArguments(@NotNull List<String> args)
    {
        MAIN_LOGGER.info("No argument validator, using default.\n");
        if (args.contains("MSAA"))
        {
            int level = Integer.parseInt(args.get(args.indexOf("MSAA") + 1));
            ProgramArguments.setArgument("MSAA", Integer.toString(level));
        }
    }
}
