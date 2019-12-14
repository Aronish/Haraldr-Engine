module Engine.engine
{
    //Load resources using <class>.class.getModule().getResource*(); required with modules.
    requires org.jetbrains.annotations;

    //LWJGL
    requires org.lwjgl.natives;
    //GLFW
    requires org.lwjgl.glfw;
    requires org.lwjgl.glfw.natives;
    //OpenGL
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengl.natives;
    //STB
    requires org.lwjgl.stb;
    requires org.lwjgl.stb.natives;
    //Misc
    requires java.desktop;

    opens graphics;
}