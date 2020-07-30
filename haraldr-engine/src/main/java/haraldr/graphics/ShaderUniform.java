package haraldr.graphics;

import org.jetbrains.annotations.NotNull;

public abstract class ShaderUniform
{
    protected String name;

    protected ShaderUniform(String name)
    {
        this.name = name;
    }

    public abstract void bind(Shader shader);

    public static class Float extends ShaderUniform
    {
        private float value;

        public Float(String name, double value)
        {
            super(name);
            this.value = (float) value;
        }

        @Override
        public void bind(@NotNull Shader shader)
        {
            shader.setFloat(name, value);
        }
    }

    public static class Vector2f extends ShaderUniform
    {
        private haraldr.math.Vector2f vector;

        public Vector2f(String name, haraldr.math.Vector2f vector)
        {
            super(name);
            this.vector = vector;
        }

        @Override
        public void bind(@NotNull Shader shader)
        {
            shader.setVector2f(name, vector);
        }
    }

    public static class Vector3f extends ShaderUniform
    {
        private haraldr.math.Vector3f vector;

        public Vector3f(String name, haraldr.math.Vector3f vector)
        {
            super(name);
            this.vector = vector;
        }

        @Override
        public void bind(@NotNull Shader shader)
        {
            shader.setVector3f(name, vector);
        }
    }

    public static class Vector4f extends ShaderUniform
    {
        private haraldr.math.Vector4f vector;

        protected Vector4f(String name, haraldr.math.Vector4f vector)
        {
            super(name);
            this.vector = vector;
        }

        @Override
        public void bind(Shader shader)
        {
            shader.setVector4f(name, vector);
        }
    }
}
