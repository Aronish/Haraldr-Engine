package haraldr.main;

import haraldr.debug.Logger;
import org.jetbrains.annotations.Contract;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Function;

import static org.lwjgl.BufferUtils.createByteBuffer;

public class IOUtils
{
    public static <R> R readResource(String path, Function<InputStream, R> function)
    {
        try (InputStream inputStream = IOUtils.class.getModule().getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                try (InputStream inputStreamClient = EntryPoint.application.getClass().getModule().getResourceAsStream(path))
                {
                    if (inputStreamClient == null)
                    {
                        Logger.error("Resource at path " + path + " not found!");
                        return null;
                    } else
                    {
                        return function.apply(inputStreamClient);
                    }
                }
            } else
            {
                return function.apply(inputStream);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        throw new NullPointerException("Couldn't read resource at " + path + "!");
    }

    public static boolean resourceExists(String path)
    {
        try (InputStream inputStream = IOUtils.class.getModule().getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                try (InputStream inputStreamClient = EntryPoint.application.getClass().getModule().getResourceAsStream(path))
                {
                    return inputStreamClient != null;
                }
            } else return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static String resourceToString(InputStream file)
    {
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            int data = file.read();
            while (data != -1)
            {
                stringBuilder.append((char) data);
                data = file.read();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static ByteBuffer resourceToByteBuffer(InputStream data, int initialCapacity)
    {
        ByteBuffer buffer = null;
        try
        {
            ReadableByteChannel rbc = Channels.newChannel(data);
            buffer = createByteBuffer(initialCapacity);
            while (true)
            {
                int bytes = rbc.read(buffer);
                if (bytes == -1) break;
                if (buffer.remaining() == 0)
                {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity)
    {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}