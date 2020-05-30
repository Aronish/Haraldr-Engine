package engine.ecs.component;

import engine.math.Matrix4f;

public class MatrixComponent
{
    private Matrix4f transformationMatrix = Matrix4f.IDENTITY;

    public void setTransformationMatrix(Matrix4f transformationMatrix)
    {
        this.transformationMatrix = transformationMatrix;
    }

    public Matrix4f getTransformationMatrix()
    {
        return transformationMatrix;
    }
}
