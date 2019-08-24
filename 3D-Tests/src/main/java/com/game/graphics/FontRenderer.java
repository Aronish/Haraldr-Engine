package com.game.graphics;

import com.game.Application;
import com.game.Camera;
import com.game.Window;
import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import sun.nio.ch.IOUtil;
import sun.nio.fs.WindowsFileSystemProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.List;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;

public class FontRenderer {

    private static Window window;
    private static ByteBuffer ttf;
    private static STBTTFontinfo info;
    private static STBTTBakedChar.Buffer data;
    private static int ascent;
    private static int descent;
    private static int lineGap;
    private static String text = "HELLO, WORLD";
    private static int fontHeight = 24;
    private static int BITMAP_W, BITMAP_H;
    private static final Shader FONT_SHADER = new Shader("shaders/font");
    private static int texID;
    private static Matrix4f model = Matrix4f.transform(new Vector3f(0.0f, 250.0f), 0.0f, new Vector2f(1.0f), false);

    private static final int[] defIndices = {
            0, 1, 2,
            0, 2, 3
    };
    private static List<Float> vertexData;
    private int vao = glCreateVertexArrays(), vbo = glGenBuffers(), ebo = glGenBuffers();

    public FontRenderer(Window _window){
        window = _window;
        try {
            ttf = ioResourceToByteBuffer("fonts/VCR_OSD_MONO_1.001.ttf", 512 * 1024);
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
        }

        BITMAP_W = Math.round(512 * _window.contentScaleX);
        BITMAP_H = Math.round(512 * _window.contentScaleY);

        data = init(BITMAP_W, BITMAP_H);
    }

    public void setup(){
        renderText(data, BITMAP_W, BITMAP_H);
        /*for (float e : vertexData)
        {
            MAIN_LOGGER.info(e);
        }*/

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, toPrimitiveArray(vertexData), GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, 8);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, defIndices, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void render(){
        FONT_SHADER.use();
        //FONT_SHADER.setMatrix(model.matrix, "model");
        FONT_SHADER.setMatrix(Camera.viewMatrix.matrix, "view");
        FONT_SHADER.setMatrix(Matrix4f._orthographic.matrix, "projection");
        glBindVertexArray(vao);
        glBindTexture(GL_TEXTURE_2D, texID);
        //Models.SPRITE_SHEET.bind();
        glDrawElements(GL_TRIANGLES, vertexData.size() / 4, GL_UNSIGNED_INT, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    ///// UTILS //////////////////////////////////////////////

    private static float[] toPrimitiveArray(List<Float> list){
        float[] primitiveArray = new float[list.size()];
        int i = 0;
        for (Float element : list){
            primitiveArray[i++] = element;
        }
        return primitiveArray;
    }

    private static void renderText(STBTTBakedChar.Buffer cdata, int BITMAP_W, int BITMAP_H) {
        float scale = stbtt_ScaleForPixelHeight(info, fontHeight);
        vertexData = new ArrayList<>();

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);

            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);

            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

            float factorX = 1.0f / window.contentScaleX;
            float factorY = 1.0f / window.contentScaleY;

            float lineY = 0.0f;

            for (int i = 0, to = text.length(); i < to; ) {
                i += getCP(text, to, i, pCodePoint);

                int cp = pCodePoint.get(0);
                if (cp == '\n') {
                    y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scale);
                    x.put(0, 0.0f);

                    continue;
                } else if (cp < 32 || 128 <= cp) {
                    continue;
                }

                float cpX = x.get(0);
                stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, cp - 32, x, y, q, true);
                x.put(0, scale(cpX, x.get(0), factorX));

                float
                        x0 = scale(cpX, q.x0(), factorX),
                        x1 = scale(cpX, q.x1(), factorX),
                        y0 = scale(lineY, q.y0(), factorY),
                        y1 = scale(lineY, q.y1(), factorY);

                vertexData.add(x0);
                vertexData.add(y0);
                vertexData.add(q.s0());
                vertexData.add(q.t0());

                //glTexCoord2f(q.s0(), q.t0());
                //glVertex2f(x0, y0);

                vertexData.add(x1);
                vertexData.add(y0);
                vertexData.add(q.s1());
                vertexData.add(q.t0());

                //glTexCoord2f(q.s1(), q.t0());
                //glVertex2f(x1, y0);

                vertexData.add(x1);
                vertexData.add(y1);
                vertexData.add(q.s1());
                vertexData.add(q.t1());

                //glTexCoord2f(q.s1(), q.t1());
                //glVertex2f(x1, y1);

                vertexData.add(x0);
                vertexData.add(y1);
                vertexData.add(q.s0());
                vertexData.add(q.t1());

                //glTexCoord2f(q.s0(), q.t1());
                //glVertex2f(x0, y1);
            }
        }
    }

    private static int getCP(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    private static float scale(float center, float offset, float factor) {
        return (offset - center) * factor + center;
    }

    private static STBTTBakedChar.Buffer init(int BITMAP_W, int BITMAP_H) {
        texID = glGenTextures();
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        stbtt_BakeFontBitmap(ttf, fontHeight * window.contentScaleY, bitmap, BITMAP_W, BITMAP_H, 32, cdata);

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        //glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, 0);

        return cdata;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        String paths = FontRenderer.class.getClassLoader().getResource(resource).toString();
        File file = new File(paths.substring(9));
        MAIN_LOGGER.info(paths.substring(6));
        Path path = Paths.get(file.toURI());
        MAIN_LOGGER.info(Files.isReadable(path));
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {}
            }
        } else {
            try (
                    InputStream source = FontRenderer.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);
                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }
        buffer.flip();
        return buffer;
    }
}
