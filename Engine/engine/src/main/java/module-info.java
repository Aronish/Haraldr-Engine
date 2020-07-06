/**
 *  Resource Loading In Java 9+:
 *  If a module using the engine wants to use resource loading functions in the engine module:
 *      - Make a resource root
 *      - Open the folders in the resource root in module-info.java using 'opens'
 *
 *  Info about engine:
 *  To properly be able to load resources from client modules, the engine needs to access the classloader
 *  of a class which has a subclass in the client. This will always be EntryPoint#application.
 *  EntryPoint.application.getClass().getModule().getResource*();
 */
module Engine.engine
{
    ///// REQUIRES ////////////////////
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
    requires JsonParser;
    ///// EXPORTS ///////
    exports engine.debug;
    exports engine.event;
    exports engine.graphics;
    exports engine.graphics.material;
    exports engine.graphics.lighting;
    exports engine.graphics.ui;
    exports engine.input;
    exports engine.layer;
    exports engine.main;
    exports engine.math;
    exports engine.physics;
    exports engine.scenegraph;

    opens default_shaders;
    opens default_textures;
    opens default_models;
    exports engine;
}