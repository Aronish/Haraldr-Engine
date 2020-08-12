package haraldr.graphics.ui;

import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Container extends UIComponent
{
    private GridLayout layout;

    private List<UIComponent> components = new ArrayList<>();
    private List<Container> containers = new ArrayList<>();

    public Container(Vector2f position, Vector2f size, GridLayout layout)
    {
        super(position, size);
        this.layout = layout;
    }

    public void addChild(UIComponent newComponent)
    {
        if (components.size() < layout.getMaxSlots())
        {
            components.add(newComponent);
            layout.orderComponents(components, position);
        }
    }

    public void addChild(Container newContainer)
    {
        if (containers.size() < layout.getMaxSlots())
        {
            containers.add(newContainer);
            refresh();
        }
    }

    private void refresh()
    {
        layout.orderComponents(components, position);
        layout.orderComponents(containers, position);
        for (Container container : containers)
        {
            container.refresh();
        }
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        layout.setSize(width, height);
        refresh();
    }

    protected void renderSelf(Vector2f screenPosition)
    {
        Renderer2D.drawQuad(Vector2f.add(screenPosition, position), size, new Vector4f(0.9f, 0.9f, 0.9f, 1f));
    }

    @Override
    public void render(Vector2f parentPosition)
    {
        Vector2f screenPosition = Vector2f.add(parentPosition, position);
        renderSelf(screenPosition);
        for (UIComponent child : components)
        {
            child.render(screenPosition);
        }
        for (Container container : containers)
        {
            container.render(screenPosition);
        }
    }

    public GridLayout getLayout()
    {
        return layout;
    }
}
