package gui;

import event.MouseMovedEvent;
import event.WindowResizedEvent;
import graphics.Shader;
import graphics.ShaderDataType;
import graphics.VertexBuffer;
import graphics.VertexBufferElement;
import graphics.VertexBufferLayout;
import gui.constraintt.Constraint;
import main.Window;
import math.Matrix4f;
import math.Vector3f;
import math.Vector4f;
import org.jetbrains.annotations.NotNull;

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
        VertexBuffer vertexBuffer = new VertexBuffer(createVertexData(), layout, true);
        setVertexBuffer(vertexBuffer);
    }

    public void onMouseMoved(MouseMovedEvent mouseMovedEvent, Window window)
    {
        //padding = (int) (mouseMovedEvent.xPos);
        //width = (int) (mouseMovedEvent.yPos / window.getHeight() * window.getWidth());
    }

    @Override
    public void onResize(@NotNull WindowResizedEvent windowResizedEvent)
    {
        super.onResize(windowResizedEvent);
        vertexArray.getVertexBuffer().setData(createVertexData());
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
