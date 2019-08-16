package com.game.math;

import java.io.Serializable;

/**
 * Math helper class for calculating 4x4 matrices.
 */
public class Matrix4f implements Serializable {

    public float[] matrix = new float[16];
    public static final Matrix4f _orthographic = orthographic(16f, -16f, 9f, -9f, -5f, 5f);

    private static Matrix4f identity(){
        Matrix4f identity = new Matrix4f();
        identity.matrix[0] = 1.0f;
        identity.matrix[5] = 1.0f;
        identity.matrix[10] = 1.0f;
        identity.matrix[15] = 1.0f;
        return identity;
    }

    /**
     * Multiplies this matrix with another.
     * Doesn't change this current matrix.
     */
    private Matrix4f multiply(Matrix4f multiplicand){
        Matrix4f result = identity();
        for (int y = 0; y < 4; y++){
            for (int x = 0; x < 4; x++){
                float sum = 0.0f;
                for (int e = 0; e < 4; e++){
                    sum += matrix[x + e * 4] * multiplicand.matrix[e + y * 4];
                }
                result.matrix[x + y * 4] = sum;
            }
        }
        return result;
    }

    /*
    /**
     * Creates a Model-View-Projection matrix including the transformation matrix, the view matrix of the camera
     * and and orthographic projection matrix.
     * @param position the position in world space.
     * @param angle the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier.
     * @return the resulting MVP matrix.
     */
    /*
    public static Matrix4f MVP(Vector3f position, float angle, Vector2f scale){
        // Resolution must have the aspect ratio 16:9 as of now.
        return _orthographic.multiply(Camera.viewMatrix).multiply(transform(scale.getX() == -1 ? position.add(new Vector3f(1.0f, 0.0f)) : position, angle, scale, false));
    }
    */

    /**
     * Creates a transformation/model matrix.
     * @param position the position in world space.
     * @param angle the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier.
     * @param isCamera if the transformation matrix is the view matrix of a camera. If <code>true</code>, the translation and rotation will be inverted.
     * @return the resulting transformation matrix.
     */
    public static Matrix4f transform(Vector3f position, float angle, Vector2f scale, boolean isCamera){
        return translate(scale.getX() == -1 ? position.addReturn(new Vector3f(1.0f, 0.0f)) : position, isCamera).multiply(scale(scale));
    }

    private static Matrix4f scale(Vector2f scale){
        Matrix4f result = new Matrix4f();
        result.matrix[0] = scale.getX();
        result.matrix[5] = scale.getY();
        result.matrix[10] = 1.0f;
        result.matrix[15] = 1.0f;
        return result;
    }

    private static Matrix4f translate(Vector3f vector, boolean isCamera){
        Matrix4f result = identity();
        if (isCamera){
            result.matrix[12] = -vector.getX();
            result.matrix[13] = -vector.getY();
            result.matrix[14] = -vector.getZ();
        }else{
            result.matrix[12] = vector.getX();
            result.matrix[13] = vector.getY();
            result.matrix[14] = vector.getZ();
        }
        return result;
    }

    /**
     * Not even supported ATM, so rarely used.
     */
    private static Matrix4f rotate(float angle, boolean isCamera){
        Matrix4f result = identity();
        float radians;
        if (isCamera) {
            radians = -(float) Math.toRadians(angle);
        }else{
            radians = (float) Math.toRadians(angle);
        }
        float cosAngle = (float) Math.cos(radians);
        float sinAngle = (float) Math.sin(radians);
        result.matrix[0] = cosAngle;
        result.matrix[1] = sinAngle;
        result.matrix[4] = -sinAngle;
        result.matrix[5] = cosAngle;
        return result;
    }

    /**
     * Creates an orthographic projection matrix. Objects further away will not become smaller.
     * Takes in parameters for the clipping planes that create the clip space. Objects outside the planes will not be visible.
     * The aspect ratio has to be 16:9 at the moment.
     * @param right the right clipping plane.
     * @param left the left clipping plane.
     * @param top the top clipping plane.
     * @param bottom the bottom clipping plane.
     * @param far the far clipping plane.
     * @param near the near clipping plane.
     * @return the resulting orthographic projection matrix.
     */
    private static Matrix4f orthographic(float right, float left, float top, float bottom, float far, float near){
        Matrix4f result = identity();
        result.matrix[0] = 2.0f / (right - left);
        result.matrix[5] = 2.0f / (top - bottom);
        result.matrix[10] = -2.0f / (far - near);
        result.matrix[12] = -((right + left) / (right - left));
        result.matrix[13] = -((top + bottom) / (top - bottom));
        result.matrix[14] = -((far + near) / (far - near));
        return result;
    }

    /**
     * Creates a perspective projection matrix. Objects further away will get smaller to simulate perspective.
     * A lot of things would need to be rewritten if this is to be used. Camera#isInView won't work correctly. It is specifically made for orthographic projection.
     * @param FOV the field-of-view of the camera frustum.
     * @return the resulting perspective projection matrix.
     */
    public static Matrix4f perspective(float FOV){
        Matrix4f result = new Matrix4f();
        float ar = 1280.0f/720.0f;
        float near = -1.0f;
        float far = 10.0f;
        float range = far - near;
        float tanHalfFOV = (float) Math.tan(Math.toRadians(FOV / 2));

        result.matrix[0] = 1.0f / (tanHalfFOV * ar);
        result.matrix[5] = 1.0f / tanHalfFOV;
        result.matrix[10] = -((far + near) / range);
        result.matrix[11] = -((2.0f * far * near) / range);
        result.matrix[14] = -1;
        return result;
    }
}