package main.java.math;

import main.java.Camera;

public class Matrix4f {
    // All matrices are in column-major order!

    public float[] matrix = new float[16];

    public Matrix4f(){
        for (int i = 0; i < this.matrix.length; i++){
            this.matrix[i] = 0.0f;
        }
    }

    private Matrix4f identity(){
        Matrix4f identity = new Matrix4f();
        identity.matrix[0] = 1.0f;
        identity.matrix[5] = 1.0f;
        identity.matrix[10] = 1.0f;
        identity.matrix[15] = 1.0f;
        return identity;
    }

    private Matrix4f multiply(Matrix4f multiplicand){
        Matrix4f result = identity();
        for (int y = 0; y < 4; y++){
            for (int x = 0; x < 4; x++){
                float sum = 0.0f;
                for (int e = 0; e < 4; e++){
                    sum += this.matrix[x + e * 4] * multiplicand.matrix[e + y * 4];
                }
                result.matrix[x + y * 4] = sum;
            }
        }
        return result;
    }

    public Matrix4f MVP(Vector3f position, float angle, float scale){
        // Resolution must have the aspect ratio 16:9 as of now.
        return orthographic(16f, -16f, 9f, -9f, -5f, 5f).multiply(Camera.viewMatrix).multiply(transform(position, angle, scale, false));
    }

    // Used for static objects (crosshairs, players, etc.)
    public Matrix4f MP(Vector3f position, float angle, float scale){
        return orthographic(16f, -16f, 9f, -9f, -5f, 5f).multiply(transform(position, angle, scale, false));
    }

    public Matrix4f transform(Vector3f position, float angle, float scale, boolean isCamera){
        return translate(position, isCamera).multiply(rotate(angle, isCamera)).multiply(scale(scale));
    }

    private Matrix4f scale(float scale){
        Matrix4f result = new Matrix4f();
        result.matrix[0] = scale;
        result.matrix[5] = scale;
        result.matrix[10] = 1.0f;
        result.matrix[15] = 1.0f;
        return result;
    }

    private Matrix4f translate(Vector3f vector, boolean isCamera){
        Matrix4f result = identity();
        if (isCamera){
            result.matrix[12] = -vector.x;
            result.matrix[13] = -vector.y;
            result.matrix[14] = -vector.z;
        }else{
            result.matrix[12] = vector.x;
            result.matrix[13] = vector.y;
            result.matrix[14] = vector.z;
        }
        return result;
    }

    private Matrix4f rotate(float angle, boolean isCamera){
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

    private Matrix4f orthographic(float right, float left, float top, float bottom, float far, float near){
        Matrix4f result = identity();
        result.matrix[0] = 2.0f / (right - left);
        result.matrix[5] = 2.0f / (top - bottom);
        result.matrix[10] = -2.0f / (far - near);
        result.matrix[12] = -((right + left) / (right - left));
        result.matrix[13] = -((top + bottom) / (top - bottom));
        result.matrix[14] = -((far + near) / (far - near));
        return result;
    }
    //Unused in true 2D
    public Matrix4f perspective(double FOV){
        Matrix4f result = new Matrix4f();
        float ar = (float) 1920/1080;
        float near = 0.1f;
        float far = 1000.0f;
        float range = far - near;
        float tanHalfFOV = (float) Math.tan(Math.toRadians(FOV / 2));

        result.matrix[0] = 1.0f / (tanHalfFOV * ar);
        result.matrix[5] = 1.0f / tanHalfFOV;
        result.matrix[10] = -((far + near) / range);
        result.matrix[11] = -1;
        result.matrix[14] = -((2.0f * far * near) / range);
        return result;
    }
}