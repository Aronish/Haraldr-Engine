package haraldr.graphics;

public abstract class ShaderSampler
{
    protected final int unit;

    protected ShaderSampler(int unit)
    {
        this.unit = unit;
    }

    public abstract void bind();

    public static class Texture2D extends ShaderSampler
    {
        private Texture texture;

        public Texture2D(Texture texture, int unit)
        {
            super(unit);
            this.texture = texture;
        }

        @Override
        public void bind()
        {
            texture.bind(unit);
        }
    }

    public static class CubeMap extends ShaderSampler
    {
        private haraldr.graphics.CubeMap cubemap;

        public CubeMap(haraldr.graphics.CubeMap cubemap, int unit)
        {
            super(unit);
            this.cubemap = cubemap;
        }

        @Override
        public void bind()
        {
            cubemap.bind(unit);
        }
    }
}
