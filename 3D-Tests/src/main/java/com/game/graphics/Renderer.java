package com.game.graphics;

import com.game.Camera;
import com.game.gameobject.Entity;
import com.game.math.Matrix4f;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Normal renderer that renders objects which don't need to use instancing. (Players, ev. machines...).
 */
public class Renderer {

    private static final Shader SHADER = new Shader("shaders/shader");

    /**
     * Clears the framebuffer for the next render. Clear color is set in Main#init ATM.
     * (No need to clear in InstancedRenderer.)
     */
    public static void clear(){
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Sets the clear color to the rgb color specified by the arguments. [0-1]
     * @param r the red channel.
     * @param g the green channel.
     * @param b the blue channel.
     * @param a the alpha channel.
     */
    public static void setClearColor(float r, float g, float b, float a){
        glClearColor(r, g, b, a);
    }

    /**
     * Binds all the normal stuff like, sprite sheets, shaders and sets the view and projection uniforms.
     * Issues a normal draw call.
     * @param entity the entity to render.
     */
    public static void render(Camera camera, Entity entity){
        Models.SPRITE_SHEET.bind();
        SHADER.use();
        SHADER.setMatrix(camera.getViewMatrix().matrix, "view");
        SHADER.setMatrix(Matrix4f._orthographic.matrix, "projection");
        SHADER.setMatrix(entity.getMatrixArray(), "matrix");
        entity.getGameObjectType().model.getVertexArray().bind();
        entity.getGameObjectType().model.getVertexArray().draw();
    }

    public static void deleteShaders(){
        SHADER.delete();
    }
}
