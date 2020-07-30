package haraldr.graphics;

import haraldr.debug.Logger;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL30.glRenderbufferStorageMultisample;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;
import static org.lwjgl.opengl.GL45.glCreateFramebuffers;
import static org.lwjgl.opengl.GL45.glCreateRenderbuffers;
import static org.lwjgl.opengl.GL45.glCreateTextures;

@SuppressWarnings("unused")
public class Framebuffer
{
    private int frameBufferId;
    private ColorAttachment colorAttachment;
    private RenderBuffer depthBuffer;

    public Framebuffer()
    {
        frameBufferId = glCreateFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        glDrawBuffers(new int[] { GL_COLOR_ATTACHMENT0 });
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void setColorAttachment(@NotNull ColorAttachment colorAttachment)
    {
        this.colorAttachment = colorAttachment;
        colorAttachment.init();
        colorAttachment.attach(this);
    }

    public void setDepthBuffer(@NotNull RenderBuffer depthBuffer)
    {
        this.depthBuffer = depthBuffer;
        depthBuffer.init();
        depthBuffer.attach(this);
    }

    public void resize(int width, int height)
    {
        if (width <= 0 || height <= 0) return;
        colorAttachment.resize(width, height);
        depthBuffer.resize(width, height);
    }

    public void bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
    }

    public void unbind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getColorAttachmentTexture()
    {
        return colorAttachment.getTextureId(this);
    }

    public ColorAttachment getColorAttachment()
    {
        return colorAttachment;
    }

    public RenderBuffer getDepthAttachment()
    {
        return depthBuffer;
    }

    public void delete()
    {
        glDeleteFramebuffers(frameBufferId);
        colorAttachment.delete();
        depthBuffer.delete();
    }

    public static class ColorAttachment
    {
        protected int textureId, internalFormat, width, height, format, type;

        public ColorAttachment(int width, int height, int internalFormat)
        {
            this.internalFormat = internalFormat;
            this.width = width;
            this.height = height;
            switch (internalFormat)
            {
                case GL_RGBA8 ->
                {
                    format = GL_RGBA;
                    type = GL_UNSIGNED_BYTE;
                }
                case GL_RGBA16F ->
                {
                    format = GL_RGBA;
                    type = GL_FLOAT;
                }
                case GL_RGB8 ->
                {
                    format = GL_RGB;
                    type = GL_UNSIGNED_BYTE;
                }
                case GL_RGB16F ->
                {
                    format = GL_RGB;
                    type = GL_FLOAT;
                }
                default -> Logger.error("Unsupported format for color attachment!");
            }
        }

        protected void init()
        {
            textureId = glCreateTextures(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }

        protected void attach(@NotNull Framebuffer framebuffer)
        {
            glBindFramebuffer(GL_FRAMEBUFFER, framebuffer.frameBufferId);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        protected void resize(int width, int height)
        {
            this.width = width;
            this.height = height;
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0);
        }

        public void bind()
        {
            glBindTextureUnit(0, textureId);
        }

        protected int getTextureId(Framebuffer framebuffer)
        {
            return textureId;
        }

        public void delete()
        {
            glDeleteTextures(textureId);
        }
    }

    public static class MultisampledColorAttachment extends ColorAttachment
    {
        private int samples, intermediateFramebuffer, intermediateColorAttachment;

        public MultisampledColorAttachment(int width, int height, int internalFormat, int samples)
        {
            super(width, height, internalFormat);
            this.samples = samples;
            intermediateColorAttachment = glCreateTextures(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, intermediateColorAttachment);
            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            intermediateFramebuffer = glCreateFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, intermediateFramebuffer);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, intermediateColorAttachment, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        @Override
        protected void init()
        {
            textureId = glCreateTextures(GL_TEXTURE_2D_MULTISAMPLE);
            glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, textureId);
            glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, internalFormat, width, height, true);
        }

        @Override
        protected void attach(@NotNull Framebuffer framebuffer)
        {
            glBindFramebuffer(GL_FRAMEBUFFER, framebuffer.frameBufferId);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, textureId, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        @Override
        protected void resize(int width, int height)
        {
            this.width = width;
            this.height = height;
            glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, textureId);
            glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, internalFormat, width, height, true);
            glBindTexture(GL_TEXTURE_2D, intermediateColorAttachment);
            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0);
        }

        @Override
        protected int getTextureId(@NotNull Framebuffer framebuffer)
        {
            glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer.frameBufferId);
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, intermediateFramebuffer);
            glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
            return intermediateColorAttachment;
        }

        @Override
        public void delete()
        {
            super.delete();
            glDeleteFramebuffers(intermediateFramebuffer);
            glDeleteTextures(intermediateColorAttachment);
        }
    }

    public static class RenderBuffer
    {
        protected int renderBufferId, internalFormat, width, height;

        public RenderBuffer(int width, int height, int internalFormat)
        {
            this.internalFormat = internalFormat;
            this.width = width;
            this.height = height;
        }

        protected void init()
        {
            renderBufferId = glCreateRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, renderBufferId);
            glRenderbufferStorage(GL_RENDERBUFFER, internalFormat, width, height);
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
        }

        private void attach(@NotNull Framebuffer framebuffer)
        {
            glBindFramebuffer(GL_FRAMEBUFFER, framebuffer.frameBufferId);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBufferId);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        protected void resize(int width, int height)
        {
            this.width = width;
            this.height = height;
            glBindRenderbuffer(GL_RENDERBUFFER, renderBufferId);
            glRenderbufferStorage(GL_RENDERBUFFER, internalFormat, width, height);
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
        }

        public void delete()
        {
            glDeleteRenderbuffers(renderBufferId);
        }
    }

    public static class MultisampledRenderBuffer extends RenderBuffer
    {
        private int samples;

        public MultisampledRenderBuffer(int width, int height, int internalFormat, int samples)
        {
            super(width, height, internalFormat);
            this.samples = samples;
        }

        @Override
        protected void init()
        {
            renderBufferId = glCreateRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, renderBufferId);
            glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, internalFormat, width, height);
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
        }

        @Override
        protected void resize(int width, int height)
        {
            this.width = width;
            this.height = height;
            glBindRenderbuffer(GL_RENDERBUFFER, renderBufferId);
            glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, internalFormat, width, height);
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
        }
    }
}
