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
module haraldr
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
    //TinyEXR
    requires org.lwjgl.tinyexr;
    requires org.lwjgl.tinyexr.natives;
    //TinyFileDialogs
    requires org.lwjgl.tinyfd;
    requires org.lwjgl.tinyfd.natives;

    requires JsonParser;
    ///// EXPORTS ////////
    exports haraldr.debug;
    exports haraldr.dockspace;
    exports haraldr.dockspace.uicomponents;
    exports haraldr.ecs;
    exports haraldr.event;
    exports haraldr.graphics;
    exports haraldr.graphics.lighting;
    exports haraldr.input;
    exports haraldr.main;
    exports haraldr.math;
    exports haraldr.physics;
    exports haraldr.scene;

    ///// RESOURCES //////
    opens default_shaders;
    opens default_textures;
    opens default_models;
    opens default_meshes;
}