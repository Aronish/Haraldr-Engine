package haraldr.graphics;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

@SuppressWarnings("unused")
public class Renderer
{
    public static void clear(ClearMask clearMask)
    {
        glClear(clearMask.bitmask);
    }

    public static void setClearColor(float r, float g, float b, float a)
    {
        glClearColor(r, g, b, a);
    }

    public static void setViewPort(int x, int y, int width, int height)
    {
        if (width < 0) width = 0;
        if (height < 0) height = 0;
        glViewport(x, y, width, height);
    }

    public enum ClearMask
    {
        COLOR(GL_COLOR_BUFFER_BIT),
        DEPTH(GL_DEPTH_BUFFER_BIT),
        STENCIL(GL_STENCIL_BUFFER_BIT),
        COLOR_DEPTH(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT),
        COLOR_STENCIL(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT),
        DEPTH_STENCIL(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT),
        COLOR_DEPTH_STENCIL(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        private final int bitmask;

        ClearMask(int bitmask)
        {
            this.bitmask = bitmask;
        }
    }
}
