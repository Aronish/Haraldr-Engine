package offlinerenderer;

import org.jetbrains.annotations.Contract;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class GeneratedCubeMap
{
    private int cubeMapId, size, highestMipLevel;
    private String name;

    @Contract(pure = true)
    public GeneratedCubeMap(int cubeMapId, int size, int highestMipLevel, String name)
    {
        this.cubeMapId = cubeMapId;
        this.size = size;
        this.highestMipLevel = highestMipLevel;
        this.name = name;
    }

    public void delete()
    {
        glDeleteTextures(cubeMapId);
    }

    public int getCubeMapId()
    {
        return cubeMapId;
    }

    public int getSize()
    {
        return size;
    }

    public int getHighestMipLevel()
    {
        return highestMipLevel;
    }

    public String getName()
    {
        return name;
    }
}
