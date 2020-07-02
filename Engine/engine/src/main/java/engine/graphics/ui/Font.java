package engine.graphics.ui;

import engine.main.ArrayUtils;
import engine.main.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;
import static org.lwjgl.opengl.GL45.glCreateTextures;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Font
{
    private static final int WIDTH = 512, HEIGHT = 512;

    private float scaleFactor;
    private int ascent, descent, lineGap;
    private int fontAtlas;
    private STBTTFontinfo fontinfo;
    private STBTTPackedchar.Buffer packedchars;
    @SuppressWarnings("FieldCanBeLocal")
    private ByteBuffer fontData; //STBTT only keeps pointers, don't let gc clean this up.

    public Font(String path, int size)
    {
        ///// Load Font /////
        fontData = IOUtils.readResource(path, (stream -> IOUtils.resourceToByteBuffer(stream, 512 * 1024)));
        fontinfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontinfo, fontData)) throw new IllegalStateException("Couldn't initialize font!");
        scaleFactor = stbtt_ScaleForPixelHeight(fontinfo, size);

        try (MemoryStack stack = stackPush())
        {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);
            stbtt_GetFontVMetrics(fontinfo, pAscent, pDescent, pLineGap);
            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
        }

        ///// Init Font Atlas /////
        ByteBuffer atlasData = BufferUtils.createByteBuffer(WIDTH * HEIGHT);
        packedchars = STBTTPackedchar.malloc(95);
        STBTTPackContext packContext = STBTTPackContext.create();
        stbtt_PackBegin(packContext, atlasData, WIDTH, HEIGHT, 0, 1);
        stbtt_PackFontRange(packContext, fontData, 0, (float) size, 32, packedchars);
        stbtt_PackEnd(packContext);

        fontAtlas = glCreateTextures(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, fontAtlas);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, WIDTH, HEIGHT, 0, GL_RED, GL_UNSIGNED_BYTE, atlasData);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public float[] createTextMesh(@NotNull String text)
    {
        List<Float> vertices = new ArrayList<>();

        try (MemoryStack stack = stackPush())
        {
            IntBuffer pCodePoint = stack.mallocInt(1);
            FloatBuffer x = stack.floats(0f);
            FloatBuffer y = stack.floats(0f);
            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);

            float factorX = 1f;
            float factorY = 1f;
            float lineY = 0f;

            for (int i = 0, to = text.length(); i < to; )
            {
                i += getCodePoint(text, to, i, pCodePoint);

                int codePoint = pCodePoint.get(0);
                if (codePoint == '\n')
                {
                    y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scaleFactor);
                    x.put(0, 0f);

                    continue;
                } else if (codePoint < 32 || 128 <= codePoint) continue;

                float codePointX = x.get(0);
                stbtt_GetPackedQuad(packedchars, WIDTH, HEIGHT, codePoint - 32, x, y, quad, true);
                x.put(0, scale(codePointX, x.get(0), factorX));
                ///// Kerning /////////////////
                if (i < to)
                {
                    getCodePoint(text, to, i, pCodePoint);
                    x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(fontinfo, codePoint, pCodePoint.get(0)) * scaleFactor);
                }

                float x0 = scale(codePointX, quad.x0(), factorX),
                        x1 = scale(codePointX, quad.x1(), factorX),
                        y0 = scale(lineY, quad.y0(), factorY),
                        y1 = scale(lineY, quad.y1(), factorY);

                vertices.add(x0);
                vertices.add(y0);
                vertices.add(quad.s0());
                vertices.add(quad.t0());

                vertices.add(x1);
                vertices.add(y0);
                vertices.add(quad.s1());
                vertices.add(quad.t0());

                vertices.add(x1);
                vertices.add(y1);
                vertices.add(quad.s1());
                vertices.add(quad.t1());

                vertices.add(x0);
                vertices.add(y1);
                vertices.add(quad.s0());
                vertices.add(quad.t1());
            }
        }
        return ArrayUtils.toPrimitiveArrayF(vertices);
    }

    public void bind(int unit)
    {
        glBindTextureUnit(unit, fontAtlas);
    }

    public void delete()
    {
        glDeleteTextures(fontAtlas);
    }

    private static float scale(float center, float offset, float factor)
    {
        return (offset - center) * factor + center;
    }

    private static int getCodePoint(@NotNull String text, int to, int i, IntBuffer codePointOut)
    {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to)
        {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2))
            {
                codePointOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        codePointOut.put(0, c1);
        return 1;
    }
}