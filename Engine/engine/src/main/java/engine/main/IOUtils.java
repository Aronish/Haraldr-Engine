package engine.main;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    public static @NotNull String resourceToString(@NotNull InputStream file)
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
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static @NotNull ByteBuffer resourceToByteBuffer(InputStream data, int initialCapacity)
    {
        ByteBuffer buffer = null;
        try
        {
            ReadableByteChannel rbc = Channels.newChannel(data);
            buffer = createByteBuffer(initialCapacity);
            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) break;
                if (buffer.remaining() == 0)
                {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                }
            }
        }catch (IOException e) { e.printStackTrace(); }
        buffer.flip();
        return buffer;
    }

    private static @NotNull ByteBuffer resizeBuffer(@NotNull ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}