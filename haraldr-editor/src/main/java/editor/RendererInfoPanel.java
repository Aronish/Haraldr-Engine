package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.UIInfoLabel;
import haraldr.ui.components.UILabeledList;
import haraldr.ui.components.UIVerticalListGroup;

public class RendererInfoPanel extends DockablePanel
{
    private UIVerticalListGroup uiVerticalListGroup = new UIVerticalListGroup(uiLayerStack, 1);
    private UIInfoLabel fpsLabel = new UIInfoLabel(uiLayerStack, 1, "");

    public RendererInfoPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
        UILabeledList uiLabeledList = new UILabeledList(uiLayerStack, 1, Vector2f.addY(position, headerSize.getY()), size);
        uiLabeledList.addComponent("Frame Info: ", fpsLabel);

        uiVerticalListGroup.addComponent(uiLabeledList);
        uiVerticalListGroup.setPosition(Vector2f.addY(position, headerSize.getY()));
        uiVerticalListGroup.setSize(size);
        draw();
    }

    public void setFrameRateInfo(int fps, int ups, double frameTime)
    {
        fpsLabel.setValue(String.format("FPS: %d | UPS: %d | Frame time: %f ms", fps, ups, frameTime));
    }

    @Override
    public void setPosition(Vector2f position)
    {
        uiVerticalListGroup.setPosition(Vector2f.addY(position, headerSize.getY()));
        super.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        uiVerticalListGroup.setSize(size);
        super.setSize(size);
    }
}
