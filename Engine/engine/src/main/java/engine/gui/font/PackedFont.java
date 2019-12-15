package engine.gui.font;

import engine.main.Application;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
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
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;
//TODO: Build new font system from the ground up using, supposedly awesome, packing.
//TODO: Probably able to pack different sizes in one bitmap.
//TODO: Readd kerning.
public class PackedFont
{
    //public static final PackedFont packedFont = new PackedFont("fonts/Roboto-Regular.ttf");

    private ByteBuffer fontData;
    private STBTTFontinfo fontInfo;
    private STBTTPackedchar.Buffer packedchars;

    private int fontSize = 50;
    private int ascent, descent, lineGap;
    private int fontBitmapWidth, fontBitmapHeight;
    private float scaleFactor;
    private float contentScaleX, contentScaleY;

    private Vector3f fontColor = new Vector3f(0.0f, 0.0f, 1.0f);

    private int textureAtlas;

    public PackedFont(String fontPath, @NotNull Window window)
    {
        contentScaleX = window.getContentScaleX();
        contentScaleY = window.getContentScaleY();
        initFont(fontPath);
        packFontBitmap();
    }

    private void initFont(String fontPath)
    {
        try
        {
            fontData = ioResourceToByteBuffer(fontPath, 512 * 1024);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, fontData))
        {
            throw new RuntimeException("Failed to initialize font at " + fontPath);
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
        fontBitmapWidth = Math.round(512 * contentScaleX);
        fontBitmapHeight = Math.round(512 * contentScaleY);
        scaleFactor = stbtt_ScaleForPixelHeight(fontInfo, fontSize);
    }

    private void packFontBitmap()
    {
        STBTTPackContext packContext = STBTTPackContext.create();
        ByteBuffer bitmap = BufferUtils.createByteBuffer(fontBitmapWidth * fontBitmapHeight);
        packedchars = STBTTPackedchar.create(96);

        stbtt_PackBegin(packContext, bitmap, fontBitmapWidth, fontBitmapHeight, 0, 1);
        stbtt_PackSetOversampling(packContext, 2, 2);
        stbtt_PackFontRange(packContext, fontData, 0, fontSize * contentScaleY, 32, packedchars);
        stbtt_PackEnd(packContext);

        textureAtlas = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureAtlas);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, fontBitmapWidth, fontBitmapHeight, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Font.TextRenderData createTextRenderData(@NotNull String text)
    {
        List<Float> vertexData = new ArrayList<>();
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
                    y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scaleFactor);
                    x.put(0, 0.0f);
                    continue;
                }else if (cp < 32 || 128 <= cp)
                {
                    continue;
                }

                float cpX = x.get(0);
                stbtt_GetPackedQuad(packedchars, fontBitmapWidth, fontBitmapHeight, cp - 32, x, y, q, true);
                x.put(0, scale(cpX, x.get(0), factorX));
                /*if (IS_KERNING_ENABLED && i < to)
                {
                    getCodePoint(text, to, i, pCodePoint);
                    x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(fontInfo, cp, pCodePoint.get(0)) * scaleFactor);
                }*/

                float x0 = scale(cpX, q.x0(), factorX);
                float x1 = scale(cpX, q.x1(), factorX);
                float y0 = -scale(lineY, q.y0(), factorY) - fontSize;
                float y1 = -scale(lineY, q.y1(), factorY) - fontSize;
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
        return new Font.TextRenderData(vertexData);
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, textureAtlas);
    }

    public float getScaleFactor(int pixels)
    {
        return stbtt_ScaleForPixelHeight(fontInfo, pixels);
    }

    ///// UTILITY /////////////////////////////////

    @NotNull
    private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException
    {
        ByteBuffer buffer;
        Path path = Paths.get(resource);
        if (Files.isReadable(path))
        {
            try (SeekableByteChannel fc = Files.newByteChannel(path))
            {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) { Application.MAIN_LOGGER.info(fc.size()); }
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

    @NotNull
    private static ByteBuffer resizeBuffer(@NotNull ByteBuffer buffer, int newCapacity)
    {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    private static int getCodePoint(@NotNull String text, int to, int i, IntBuffer cpOut)
    {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to)
        {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2))
            {
                cpOut.put(0, Character.toCodePoint(c1, c2));
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

                /*if (IS_KERNING_ENABLED && i < to)
                {
                    getCodePoint(text, to, i, pCodePoint);
                    width += stbtt_GetCodepointKernAdvance(fontInfo, cp, pCodePoint.get(0));
                }*/
            }
        }
        Application.MAIN_LOGGER.info(scaleFactor);
        return width * scaleFactor;
    }

    private static float scale(float center, float offset, float factor)
    {
        return (offset - center) * factor + center;
    }
}
