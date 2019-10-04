package com.game.gui.font;

import com.game.Window;
import com.game.math.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Font
{
    private static final boolean IS_KERNING_ENABLED = false;

    private ByteBuffer ttfFont;
    private STBTTFontinfo fontInfo;
    private STBTTBakedChar.Buffer charData;
    private int ascent, descent, lineGap, fontHeight;
    private int fontBitmap, fontBitmapWidth, fontBitmapHeight;
    private Vector3f fontColor;
    private float contentScaleX, contentScaleY;

    public Font(String fontPath, int fontHeight, Vector3f fontColor, Window window)
    {
        this.fontHeight = fontHeight;
        this.fontColor = fontColor;
        try
        {
            ttfFont = ioResourceToByteBuffer(fontPath, 512 * 1024);
        }catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, ttfFont))
        {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        try (MemoryStack stack = stackPush())
        {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
        }
        contentScaleX = window.getContentScaleX();
        contentScaleY = window.getContentScaleY();
        fontBitmapWidth = Math.round(512 * contentScaleX);
        fontBitmapHeight = Math.round(512 * contentScaleY);

        charData = init();
    }

    private STBTTBakedChar.Buffer init()
    {
        fontBitmap = glGenTextures();
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(fontBitmapWidth * fontBitmapHeight);
        stbtt_BakeFontBitmap(ttfFont, fontHeight * contentScaleY, bitmap, fontBitmapWidth, fontBitmapHeight, 32, cdata);

        glBindTexture(GL_TEXTURE_2D, fontBitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, fontBitmapWidth, fontBitmapHeight, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glBindTexture(GL_TEXTURE_2D, 0);

        return cdata;
    }

    public TextRenderData createTextRenderData(String text)
    {
        List<Float> vertexData = new ArrayList<>();

        float scale = stbtt_ScaleForPixelHeight(fontInfo, fontHeight);
        try (MemoryStack stack = stackPush())
        {
            IntBuffer pCodePoint = stack.mallocInt(1);
            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);

            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

            float factorX = 1.0f / contentScaleX;
            float factorY = 1.0f / contentScaleY;
            float lineY = 0.0f;

            for (int i = 0, to = text.length(); i < to;)
            {
                i += getCodePoint(text, to, i, pCodePoint);

                int cp = pCodePoint.get(0);
                if (cp == '\n')
                {
                    y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scale);
                    x.put(0, 0.0f);
                    continue;
                }else if (cp < 32 || 128 <= cp)
                {
                    continue;
                }

                float cpX = x.get(0);
                stbtt_GetBakedQuad(charData, fontBitmapWidth, fontBitmapHeight, cp - 32, x, y, q, true);
                x.put(0, scale(cpX, x.get(0), factorX));
                if (IS_KERNING_ENABLED && i < to)
                {
                    getCodePoint(text, to, i, pCodePoint);
                    x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(fontInfo, cp, pCodePoint.get(0)) * scale);
                }

                float x0 = scale(cpX, q.x0(), factorX);
                float x1 = scale(cpX, q.x1(), factorX);
                float y0 = -scale(lineY, q.y0(), factorY) - fontHeight;
                float y1 = -scale(lineY, q.y1(), factorY) - fontHeight;
                //Top Left
                vertexData.add(x0);
                vertexData.add(y0);
                vertexData.add(q.s0());
                vertexData.add(q.t0());
                vertexData.add(fontColor.getX());
                vertexData.add(fontColor.getY());
                vertexData.add(fontColor.getZ());
                //Top Right
                vertexData.add(x1);
                vertexData.add(y0);
                vertexData.add(q.s1());
                vertexData.add(q.t0());
                vertexData.add(fontColor.getX());
                vertexData.add(fontColor.getY());
                vertexData.add(fontColor.getZ());
                //Bottom Right
                vertexData.add(x1);
                vertexData.add(y1);
                vertexData.add(q.s1());
                vertexData.add(q.t1());
                vertexData.add(fontColor.getX());
                vertexData.add(fontColor.getY());
                vertexData.add(fontColor.getZ());
                //Bottom Left
                vertexData.add(x0);
                vertexData.add(y1);
                vertexData.add(q.s0());
                vertexData.add(q.t1());
                vertexData.add(fontColor.getX());
                vertexData.add(fontColor.getY());
                vertexData.add(fontColor.getZ());
            }
        }
        return new TextRenderData(vertexData);
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, fontBitmap);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    ///// UTILITY FUNCTIONS ///////////////////////////////////////

    private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException
    {
        ByteBuffer buffer;
        Path path = Paths.get(resource);
        if (Files.isReadable(path))
        {
            try (SeekableByteChannel fc = Files.newByteChannel(path))
            {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) { MAIN_LOGGER.info(fc.size()); }
            }
        }else{
            try (InputStream source = TextRenderer.class.getClassLoader().getResourceAsStream(resource))
            {
                if (source == null)
                {
                    throw new RuntimeException("Source was null! (Font not readable/found)");
                }
                ReadableByteChannel rbc = Channels.newChannel(source);
                buffer = createByteBuffer(bufferSize);
                while (true)
                {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1)
                    {
                        rbc.close();
                        break;
                    }
                    if (buffer.remaining() == 0)
                    {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
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

    private static float scale(float center, float offset, float factor)
    {
        return (offset - center) * factor + center;
    }

    private static int getCodePoint(String text, int to, int i, IntBuffer cpOut)
    {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to)
        {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2))
            {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                MAIN_LOGGER.info("LOW");
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    public float getStringWidth(String text, int from, int to)
    {
        int width = 0;

        try (MemoryStack stack = stackPush())
        {
            IntBuffer pCodePoint       = stack.mallocInt(1);
            IntBuffer pAdvancedWidth   = stack.mallocInt(1);
            IntBuffer pLeftSideBearing = stack.mallocInt(1);

            int i = from;
            while (i < to)
            {
                i += getCodePoint(text, to, i, pCodePoint);
                int cp = pCodePoint.get(0);

                stbtt_GetCodepointHMetrics(fontInfo, cp, pAdvancedWidth, pLeftSideBearing);
                width += pAdvancedWidth.get(0);

                if (IS_KERNING_ENABLED && i < to)
                {
                    getCodePoint(text, to, i, pCodePoint);
                    width += stbtt_GetCodepointKernAdvance(fontInfo, cp, pCodePoint.get(0));
                }
            }
        }
        return width * stbtt_ScaleForPixelHeight(fontInfo, fontHeight);
    }

    public static class TextRenderData
    {
        public final List<Float> vertexData;

        public TextRenderData(List<Float> vertexData)
        {
            this.vertexData = vertexData;
        }
    }
}
