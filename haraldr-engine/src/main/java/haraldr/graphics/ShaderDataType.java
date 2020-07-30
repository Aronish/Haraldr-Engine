package haraldr.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

@SuppressWarnings("unused")
public enum ShaderDataType
{
    FLOAT2  (2, GL_FLOAT, 4, "vec2"),
    FLOAT3  (3, GL_FLOAT, 4, "vec3"),
    FLOAT4  (4, GL_FLOAT, 4, "vec4"),
    MAT4    (4, GL_FLOAT, 4, "mat4"); //Need 4 of these, one per column.

    public final int size;
    public final int GLType;
    public final int typeSize;
    public final String token;

    ShaderDataType(int size, int GLType, int typeSize, String token)
    {
        this.size = size;
        this.GLType = GLType;
        this.typeSize = typeSize;
        this.token = token;
    }
}
