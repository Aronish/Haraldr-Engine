package com.game.gui;

import com.game.event.MouseMovedEvent;
import com.game.event.WindowResizedEvent;
import com.game.graphics.Shader;
import com.game.graphics.ShaderDataType;
import com.game.graphics.VertexBuffer;
import com.game.graphics.VertexBufferElement;
import com.game.graphics.VertexBufferLayout;
import com.game.math.Matrix4f;
import com.game.math.Vector3f;
import com.game.math.Vector4f;

public class GUIPanel extends GUIComponent
{
    private Vector4f color;

    public GUIPanel(Vector3f position, int width, int height, Vector4f color)
    {
        super(position);
        this.color = color;
        VertexBufferLayout layout = new VertexBufferLayout
        (
                new VertexBufferElement(ShaderDataType.FLOAT2)
        );
        VertexBuffer vertexBuffer = new VertexBuffer(createVertexData(width, height), layout);
        setVertexBuffer(vertexBuffer);
    }

    //Padding in here or as uniform?
    private static float[] createVertexData(int width, int height)
    {
        return new float[]
        {
                0.0f, 0.0f,
                width, 0.0f,
                width, height,
                0.0f, height
        };
    }

    public void onResize(WindowResizedEvent windowResizedEvent)
    {

    }

    public void onMouseMoved(MouseMovedEvent mouseMovedEvent)
    {
        color.set((float) mouseMovedEvent.xPos / 1280f, (float) mouseMovedEvent.yPos / 720f, (float) mouseMovedEvent.xPos / 1280f, (float) mouseMovedEvent.yPos / 720f);
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
