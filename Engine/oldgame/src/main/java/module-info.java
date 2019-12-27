module oldgame
{
    requires org.jetbrains.annotations;
    requires java.desktop;
    requires Engine.engine;
    requires org.lwjgl.opengl;
    requires org.lwjgl.glfw;

    opens textures;
    opens shaders;
}