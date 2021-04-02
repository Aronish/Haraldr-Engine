package haraldr.graphics;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL40;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

//Just to have oldgame work, but separated from LWJGL.
public class Wrapper
{
    public static final int GL_DRAW_INDIRECT_BUFFER = GL40.GL_DRAW_INDIRECT_BUFFER;

    public static void viewport(int x, int y, int width, int height)
    {
        glViewport(x, y, width, height);
    }

    public static void enableVertexAttribArrayWrapper(int attribIndex)
    {
        glEnableVertexAttribArray(attribIndex);
    }

    public static void vertexAttribPointer(int attribIndex, @NotNull VertexBufferElement element, int stride)
    {
        glVertexAttribPointer(attribIndex, element.getSize(), element.getType(), element.isNormalized(), stride, element.getOffset());
    }

    public static void vertexAttribDivisor(int attribIndex, int divisor)
    {
        glVertexAttribDivisor(attribIndex, divisor);
    }

    public static int getError()
    {
        return glGetError();
    }
}
