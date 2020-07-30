package engine.graphics;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

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
}
