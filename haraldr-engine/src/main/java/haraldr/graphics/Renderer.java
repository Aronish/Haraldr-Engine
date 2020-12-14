package haraldr.graphics;

import org.jetbrains.annotations.Contract;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DECR;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_GEQUAL;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_INCR;
import static org.lwjgl.opengl.GL11.GL_INVERT;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_NEVER;
import static org.lwjgl.opengl.GL11.GL_NOTEQUAL;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilMask;
import static org.lwjgl.opengl.GL11.glStencilOp;
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

    public static void enableDepthTest()
    {
        glEnable(GL_DEPTH_TEST);
    }

    public static void disableDepthTest()
    {
        glDisable(GL_DEPTH_TEST);
    }

    public static void enableStencilTest()
    {
        glEnable(GL_STENCIL_TEST);
    }

    public static void disableStencilTest()
    {
        glDisable(GL_STENCIL_TEST);
    }

    public static void stencilFunc(StencilFunc stencilFunc, int reference, int mask)
    {
        glStencilFunc(stencilFunc.stencilFunc, reference, mask);
    }

    public static void stencilOp(StencilOpAction stencilFail, StencilOpAction stencilPassDepthFail, StencilOpAction bothPass)
    {
        glStencilOp(stencilFail.stencilOpAction, stencilPassDepthFail.stencilOpAction, bothPass.stencilOpAction);
    }

    public static void stencilMask(int mask)
    {
        glStencilMask(mask);
    }

    public static void cullFront()
    {
        glCullFace(GL_FRONT);
    }

    public static void cullBack()
    {
        glCullFace(GL_BACK);
    }

    public static int getError()
    {
        return glGetError();
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

        @Contract(pure = true)
        ClearMask(int bitmask)
        {
            this.bitmask = bitmask;
        }
    }

    public enum StencilFunc
    {
        NEVER(GL_NEVER),
        LESS(GL_LESS),
        LEQUAL(GL_LEQUAL),
        GREATER(GL_GREATER),
        GEQUAL(GL_GEQUAL),
        EQUAL(GL_EQUAL),
        NOT_EQUAL(GL_NOTEQUAL),
        ALWAYS(GL_ALWAYS);

        private final int stencilFunc;

        @Contract(pure = true)
        StencilFunc(int stencilFunc)
        {
            this.stencilFunc = stencilFunc;
        }
    }

    public enum StencilOpAction
    {
        KEEP(GL_KEEP),
        ZERO(GL_ZERO),
        REPLACE(GL_REPLACE),
        INCR(GL_INCR),
        DECR(GL_DECR),
        INVERT(GL_INVERT);

        private final int stencilOpAction;

        @Contract(pure = true)
        StencilOpAction(int stencilOpAction)
        {
            this.stencilOpAction = stencilOpAction;
        }
    }
}
