package offlinerenderer;

import haraldr.dockspace.DockablePanel;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.UIComponent;
import haraldr.ui.components.UILabeledList;
import haraldr.ui.groups.UIConstraintGroup;

public class MainPanel extends DockablePanel<UIConstraintGroup>
{
    private UILabeledList uiComponentList = new UILabeledList(uiLayerStack, 0, Vector2f.addY(position, headerSize.getY()), new Vector2f());

    public MainPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    public void addComponent(String label, UIComponent uiComponent)
    {
        uiComponentList.addComponent(label, uiComponent);
    }

    @Override
    public void setPosition(Vector2f position)
    {
        if (uiComponentList != null) uiComponentList.setPosition(Vector2f.addY(position, headerSize.getY()));
        super.setPosition(position); // This is stupid lol, I don't even remember my reasoning for this
    }

    @Override
    public void setSize(Vector2f size)
    {
        if (uiComponentList != null) uiComponentList.setSize(size);
        super.setSize(size);
    }
}
