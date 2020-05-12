package engine.graphics;

import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;
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
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;
import static org.lwjgl.opengl.GL45.glCreateFramebuffers;
import static org.lwjgl.opengl.GL45.glCreateRenderbuffers;
import static org.lwjgl.opengl.GL45.glCreateTextures;

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
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment.textureId, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void setDepthBuffer(@NotNull RenderBuffer depthBuffer)
    {
        this.depthBuffer = depthBuffer;
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer.renderBufferId);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
    }

    public void unbind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
        private int textureId, internalFormat, format, type;

        public ColorAttachment(int width, int height, int internalFormat)
        {
            this.internalFormat = internalFormat;
            switch (internalFormat)
            {
                case GL_RGBA8:
                    format = GL_RGBA;
                    type = GL_UNSIGNED_BYTE;
                    break;
                case GL_RGBA16F:
                    format = GL_RGBA;
                    type = GL_FLOAT;
                    break;
                case GL_RGB8:
                    format = GL_RGB;
                    type = GL_UNSIGNED_BYTE;
                    break;
                case GL_RGB16F:
                    format = GL_RGB;
                    type = GL_FLOAT;
                    break;
                default:
                    MAIN_LOGGER.error("Unsupported format for color attachment!");
                    break;
            }
            textureId = glCreateTextures(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }

        public void resize(int width, int height)
        {
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0);
        }

        public void bind()
        {
            glBindTextureUnit(0, textureId);
        }

        public void delete()
        {
            glDeleteTextures(textureId);
        }
    }

    public static class RenderBuffer
    {
        private int renderBufferId, internalFormat;

        public RenderBuffer(int width, int height, int internalFormat)
        {
            this.internalFormat = internalFormat;
            renderBufferId = glCreateRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, renderBufferId);
            glRenderbufferStorage(GL_RENDERBUFFER, internalFormat, width, height);
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
        }

        public void resize(int width, int height)
        {
            glBindRenderbuffer(GL_RENDERBUFFER, renderBufferId);
            glRenderbufferStorage(GL_RENDERBUFFER, internalFormat, width, height);
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
        }

        public void delete()
        {
            glDeleteRenderbuffers(renderBufferId);
        }
    }
}
