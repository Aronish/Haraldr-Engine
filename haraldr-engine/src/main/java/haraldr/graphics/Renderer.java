package haraldr.graphics;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

@SuppressWarnings("unused")
public class Renderer
{
    public static void clear(int mask)
    {
        glClear(mask);
    }

    public static void setClearColor(float r, float g, float b, float a)
    {
        glClearColor(r, g, b, a);
    }

    public static void setViewPort(int x, int y, int width, int height)
    {
        glViewport(x, y, width, height);
    }
}
