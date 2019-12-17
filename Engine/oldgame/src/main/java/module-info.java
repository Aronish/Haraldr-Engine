module oldgame
{
    requires org.jetbrains.annotations;
    requires org.lwjgl.opengl;
    requires org.lwjgl.glfw;
    requires java.desktop;
    requires Engine.engine;

    opens textures;
    opens shaders;
}