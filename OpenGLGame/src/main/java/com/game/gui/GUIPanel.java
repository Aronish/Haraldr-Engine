package com.game.gui;

import com.game.Window;
import com.game.event.MouseMovedEvent;
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

public class GUIPanel extends GUIComponent
{
    private int width, height, padding;
    private Vector4f color;

    public GUIPanel(Window window, Vector3f position, int width, int height, int padding, Vector4f color, Constraint constraint)
    {
        super(position, constraint, window);
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.color = color;
        VertexBufferLayout layout = new VertexBufferLayout
        (
                new VertexBufferElement(ShaderDataType.FLOAT2)
        );
        VertexBuffer vertexBuffer = new VertexBuffer(createVertexData(width, height, padding), layout, true);
        setVertexBuffer(vertexBuffer);
    }

    public void onMouseMoved(MouseMovedEvent mouseMovedEvent, Window window)
    {
        //padding = (int) (mouseMovedEvent.xPos);
        //width = (int) (mouseMovedEvent.yPos / window.getHeight() * window.getWidth());
        vertexArray.getVertexBuffer().setData(constraint.createVertexData(width, height, window.getWidth(), window.getHeight(), padding));
    }

    @Override
    public void onResize(WindowResizedEvent windowResizedEvent)
    {
        super.onResize(windowResizedEvent);
        vertexArray.getVertexBuffer().setData(createVertexData(width, height, padding));
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
