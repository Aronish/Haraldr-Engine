package engine.gui;

import engine.main.Window;
import engine.event.MouseMovedEvent;
import engine.event.WindowResizedEvent;
import engine.graphics.Shader;
import engine.graphics.ShaderDataType;
import engine.graphics.VertexBuffer;
import engine.graphics.VertexBufferElement;
import engine.graphics.VertexBufferLayout;
import engine.gui.constraint.Constraint;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
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
