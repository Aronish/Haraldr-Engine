package engine.main;

import static engine.main.Application.MAIN_LOGGER;

public interface ArgumentValidator
{
    default void validateArguments(String[] args)
    {
        MAIN_LOGGER.info("No argument validator, using default.");
    }
}
