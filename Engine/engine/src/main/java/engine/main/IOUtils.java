package engine.main;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class IOUtils
{
    public static <R> @Nullable R readResource(String path, Function<InputStream, R> function)
    {
        try (InputStream inputStream = IOUtils.class.getModule().getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                try (InputStream inputStreamClient = EntryPoint.application.getClass().getModule().getResourceAsStream(path))
                {
                    if (inputStreamClient == null)
                    {
                        throw new NullPointerException("Resource at path " + path + " not found!");
                    }
                    else
                    {
                        return function.apply(inputStreamClient);
                    }
                }
            }
            else
            {
                return function.apply(inputStream);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static @Nullable InputStream readResourceToInputStream(String path)
    {
        try (InputStream inputStream = IOUtils.class.getModule().getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                try (InputStream inputStreamClient = EntryPoint.application.getClass().getModule().getResourceAsStream(path))
                {
                    if (inputStreamClient == null)
                    {
                        throw new NullPointerException("Resource at path " + path + " not found!");
                    }
                    else
                    {
                        return inputStreamClient;
                    }
                }
            }
            else
            {
                return inputStream;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}