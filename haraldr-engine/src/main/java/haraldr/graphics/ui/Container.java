package haraldr.graphics.ui;

import haraldr.event.Event;
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
            refresh(position);
        }
    }

    public void addChild(Container container)
    {
        if (containers.size() < layout.getMaxSlots())
        {
            containers.add(container);
            refresh(position);
        }
    }

    public void refresh(Vector2f parentPosition)
    {
        int index = layout.orderComponents(components, parentPosition, 0);
        layout.orderComponents(containers, parentPosition, index);
        for (Container container : containers)
        {
            container.refresh(parentPosition);
        }
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        layout.setSize(width, height);
    }

    @Override
    public void onEvent(Event event)
    {
        components.forEach((component) -> component.onEvent(event));
        containers.forEach((container -> container.onEvent(event)));
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

    public Vector2f getLayoutSize()
    {
        return layout.getSize();
    }

    public GridLayout getLayout()
    {
        return layout;
    }
}
