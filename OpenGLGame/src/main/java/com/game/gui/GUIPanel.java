package com.game.gui;

import com.game.event.MouseMovedEvent;
import com.game.event.MousePressedEvent;
import com.game.event.MouseReleasedEvent;
import com.game.event.WindowResizedEvent;
import com.game.graphics.Shader;
import com.game.graphics.ShaderDataType;
import com.game.graphics.VertexBuffer;
import com.game.graphics.VertexBufferElement;
import com.game.graphics.VertexBufferLayout;
import com.game.math.Matrix4f;
import com.game.math.Vector3f;
import com.game.math.Vector4f;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class GUIPanel extends GUIComponent
{
    private int width, height, paddingX, paddingY;
    private Vector4f color;

    private boolean editingPadding = false, moving = false;

    public GUIPanel(Vector3f position, int width, int height, int paddingX, int paddingY, Vector4f color)
    {
        super(position);
        this.width = width;
        this.height = height;
        this.paddingX = paddingX;
        this.paddingY = paddingY;
        this.color = color;
        VertexBufferLayout layout = new VertexBufferLayout
        (
                new VertexBufferElement(ShaderDataType.FLOAT2)
        );
        VertexBuffer vertexBuffer = new VertexBuffer(createVertexData(width, height, paddingX, paddingY), layout);
        setVertexBuffer(vertexBuffer);
    }

    //Padding in here or as uniform?
    private static float[] createVertexData(int width, int height, int paddingX, int paddingY)
    {
        return new float[]
        {
                0.0f + paddingX,    0.0f + paddingY,
                width - paddingX,   0.0f + paddingY,
                width - paddingX,   height - paddingY,
                0.0f + paddingX,    height - paddingY
        };
    }

    public void onResize(WindowResizedEvent windowResizedEvent)
    {
        width = windowResizedEvent.width;
        height = windowResizedEvent.height;
        vertexArray.getVertexBuffer().setData(createVertexData(width, height, paddingX, paddingY), vertexArray.getVertexBuffer().getLayout());
        MAIN_LOGGER.info("W: "  + width + " H: " + height);
    }

    public void onMouseMoved(MouseMovedEvent mouseMovedEvent)
    {
        if (editingPadding)
        {
            paddingX = (int) (mouseMovedEvent.xPos) < width / 2 ? (int) (mouseMovedEvent.xPos) : (int) (width - mouseMovedEvent.xPos);
            paddingY = (int) (mouseMovedEvent.yPos) < height / 2 ? (int) (mouseMovedEvent.yPos) : (int) (height - mouseMovedEvent.yPos);
            vertexArray.getVertexBuffer().setData(createVertexData(width, height, paddingX, paddingY), vertexArray.getVertexBuffer().getLayout());
        }
        if (moving)
        {
            setPosition((float) mouseMovedEvent.xPos - paddingX, (float) mouseMovedEvent.yPos - paddingY);
        }
    }

    public void onMousePressed(MousePressedEvent mousePressedEvent)
    {
        if (mousePressedEvent.button == GLFW_MOUSE_BUTTON_1)
        {
            moving = true;
        }
        else if (mousePressedEvent.button == GLFW_MOUSE_BUTTON_2)
        {
            editingPadding = !editingPadding;
        }
    }

    public void onMouseReleased(MouseReleasedEvent mouseReleasedEvent)
    {
        if (mouseReleasedEvent.button == GLFW_MOUSE_BUTTON_1)
        {
            moving = false;
        }
    }

    @Override
    public void draw()
    {
        Shader.FLAT_COLOR_SHADER.use();
        Shader.FLAT_COLOR_SHADER.setMatrix(getMatrixArray(), "matrix");
        Shader.FLAT_COLOR_SHADER.setMatrix(Matrix4f.pixelOrthographic.matrix, "projection");
        Shader.FLAT_COLOR_SHADER.setVector4f(color, "color");
        vertexArray.bind();
        vertexArray.draw();
    }
}
