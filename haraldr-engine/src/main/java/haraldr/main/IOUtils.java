package haraldr.main;

import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
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
                        throw new NullPointerException("Resource at path " + path + " not found!");
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

    public static <R> R readNativeResource(String path, Function<InputStream, R> function)
    {
        try (FileInputStream inputStream = new FileInputStream(path))
        {
            return function.apply(inputStream);
        } catch (IOException exception)
        {
            exception.printStackTrace();
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

    //TODO: Remove, doesn't work with .jar's
    public static String getAbsolutePath(String relativePath)
    {
        try
        {
            return Path.of(IOUtils.class.getResource(relativePath).toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        return "";
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

    public static ByteBuffer stringToByteBuffer(String s)
    {
        byte[] bytes = s.getBytes();
        ByteBuffer bb = BufferUtils.createByteBuffer(bytes.length);
        bb.put(bytes);
        bb.flip();
        return bb;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity)
    {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}