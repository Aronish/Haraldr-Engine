package haraldr.graphics;

import haraldr.math.Matrix4f;

public class HDRGammaCorrectionPass
{
    private Shader passShader;
    private float exposure;

    public HDRGammaCorrectionPass(float exposure)
    {
        passShader = Shader.create("internal_shaders/hdr_gamma_correct.glsl");
        this.exposure = exposure;
    }

    public void setExposure(float exposure)
    {
        this.exposure = exposure;
    }

    public void render(RenderTexture renderTexture, Matrix4f projection)
    {
        passShader.bind();
        passShader.setMatrix4f("u_Projection", projection);
        passShader.setFloat("u_Exposure", exposure);
        renderTexture.getFramebuffer().getColorAttachmentTexture().bind(0);
        renderTexture.getQuad().bind();
        renderTexture.getQuad().drawElements();
    }
}
