package com.game.gui;

import com.game.Window;
import com.game.event.MouseMovedEvent;
import com.game.event.MousePressedEvent;
import com.game.event.MouseReleasedEvent;
import com.game.event.WindowResizedEvent;
import com.game.graphics.Shader;
import com.game.graphics.ShaderDataType;
import com.game.graphics.VertexBuffer;
import com.game.graphics.VertexBufferElement;
import com.game.graphics.VertexBufferLayout;
import com.game.gui.constraint.Constraint;
import com.game.math.Matrix4f;
import com.game.math.Vector3f;
import com.game.math.Vector4f;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class GUIPanel extends GUIComponent
{
    private int width, height;
    private Vector4f color;

    private boolean mousedOver = false;
    private int mouseX, mouseY;
    private int lastPositionX, lastPositionY;
    private int clickedX, clickedY;

    private boolean editingSize = false, moving = false;

    public GUIPanel(Vector3f position, int width, int height, Vector4f color, Constraint constraint, Window window)
    {
        super(position, constraint, window);
        this.width = width;
        this.height = height;
        this.color = color;
        VertexBufferLayout layout = new VertexBufferLayout
        (
                new VertexBufferElement(ShaderDataType.FLOAT2)
        );
        VertexBuffer vertexBuffer = new VertexBuffer(createVertexData(width, height), layout, true);
        setVertexBuffer(vertexBuffer);
    }

    @Override
    public void onResize(WindowResizedEvent windowResizedEvent)
    {
        super.onResize(windowResizedEvent);
        vertexArray.getVertexBuffer().setData(createVertexData(width, height));
    }

    public void onMouseMoved(MouseMovedEvent mouseMovedEvent, Window window)
    {
        checkMousedOver(mouseMovedEvent);
        if (mousedOver)
        {
            mouseX = (int) mouseMovedEvent.xPos;
            mouseY = (int) mouseMovedEvent.yPos;
        }
        if (editingSize)
        {
            width = (int) (mouseMovedEvent.xPos - position.getX());
            height = (int) (mouseMovedEvent.yPos - position.getY());
            if (width < 10) width = 10;
            if (height < 10) height = 10;
            vertexArray.getVertexBuffer().setData(createVertexData(width, height));
        }
        if (moving)
        {
            setPosition((float) (mouseMovedEvent.xPos - (clickedX - lastPositionX)), (float) (mouseMovedEvent.yPos - (clickedY - lastPositionY)));
            if (position.getX() < 0) setPositionX(0);
            if (position.getY() < 0) setPositionY(0);
            if (position.getX() + width > window.getWidth()) setPositionX(window.getWidth() - width);
            if (position.getY() + height > window.getHeight()) setPositionY(window.getHeight() - height);
        }
    }

    public void onMousePressed(MousePressedEvent mousePressedEvent)
    {
        if (mousedOver)
        {
            if (mousePressedEvent.button == GLFW_MOUSE_BUTTON_1)
            {
                clickedX = mouseX;
                clickedY = mouseY;
                lastPositionX = (int) position.getX();
                lastPositionY = (int) position.getY();
                moving = true;
            }
            else if (mousePressedEvent.button == GLFW_MOUSE_BUTTON_2) editingSize = true;
        }
    }

    public void onMouseReleased(MouseReleasedEvent mouseReleasedEvent)
    {
        if (mouseReleasedEvent.button == GLFW_MOUSE_BUTTON_1) moving = false;
        if (mouseReleasedEvent.button == GLFW_MOUSE_BUTTON_2) editingSize = false;
    }

    private void checkMousedOver(MouseMovedEvent event)
    {
        mousedOver = event.xPos > position.getX() && event.xPos < position.getX() + width && event.yPos > position.getY() && event.yPos < position.getY() + height;
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
