package engine.graphics;

import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class Wrapper
{
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
