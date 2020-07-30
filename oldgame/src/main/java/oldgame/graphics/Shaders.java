package oldgame.graphics;

import engine.graphics.Shader;

public class Shaders
{
    public static final Shader SHADER = Shader.create("shaders/shader.glsl");
    public static final Shader INSTANCED_SHADER = Shader.create("shaders/instanced_shader.glsl");
    public static final Shader FLAT_COLOR_SHADER = Shader.create("shaders/flat_shader.glsl");
    public static final Shader MULTI_DRAW_SHADER = Shader.create("shaders/indirect_shader.glsl");
    public static final Shader COMBINED_SHADER = Shader.create("shaders/combined.glsl");
}
