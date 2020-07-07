package engine.graphics;

public abstract class ShaderSampler
{
    public abstract void bind(int unit);

    public static class Texture2D extends ShaderSampler
    {
        private Texture texture;

        public Texture2D(Texture texture)
        {
            this.texture = texture;
        }

        @Override
        public void bind(int unit)
        {
            texture.bind(unit);
        }
    }

    public static class CubeMap extends ShaderSampler
    {
        private engine.graphics.CubeMap cubemap;

        public CubeMap(engine.graphics.CubeMap cubemap)
        {
            this.cubemap = cubemap;
        }

        @Override
        public void bind(int unit)
        {
            cubemap.bind(unit);
        }
    }
}
