package oldgame.graphics;

import engine.graphics.Shader;

public class Shaders
{
    public static final Shader SHADER = new Shader("shaders/shader.vert", "shaders/shader.frag");
    public static final Shader INSTANCED_SHADER = new Shader("shaders/instanced_shader.vert", "shaders/shader.frag");
    public static final Shader FLAT_COLOR_SHADER = new Shader("shaders/flat_shader.vert", "shaders/flat_shader.frag");
    public static final Shader MULTI_DRAW_SHADER = new Shader("shaders/indirect_shader.vert", "shaders/indirect_shader.frag");
    public static final Shader COMBINED_SHADER = new Shader("shaders/combined.vert", "shaders/combined.frag");

    public static void dispose()
    {
        SHADER.delete();
        INSTANCED_SHADER.delete();
        FLAT_COLOR_SHADER.delete();
        MULTI_DRAW_SHADER.delete();
        COMBINED_SHADER.delete();
    }
}
