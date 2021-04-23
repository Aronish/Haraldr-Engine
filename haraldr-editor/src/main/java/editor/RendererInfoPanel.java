package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.UIInfoLabel;
import haraldr.ui.components.UILabeledList;
import haraldr.ui.components.UIVerticalListGroup;

public class RendererInfoPanel extends DockablePanel
{
    private UIVerticalListGroup uiVerticalListGroup;
    private UIInfoLabel fpsLabel;

    public RendererInfoPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    @Override
    protected void initializeUI()
    {
        uiVerticalListGroup = new UIVerticalListGroup(uiLayerStack, 1);
        UILabeledList uiLabeledList = new UILabeledList(uiLayerStack, 1, Vector2f.addY(position, headerSize.getY()), size);
        fpsLabel = new UIInfoLabel(uiLayerStack, 1, "");
        uiLabeledList.addComponent("Frame Info: ", fpsLabel);
        uiVerticalListGroup.addComponent(uiLabeledList);
    }

    @Override
    public void setUIPosition(Vector2f position)
    {
        uiVerticalListGroup.setPosition(position);
    }

    @Override
    public void setUISize(Vector2f size)
    {
        uiVerticalListGroup.setSize(size);
    }

    public void setFrameRateInfo(int fps, int ups, double frameTime)
    {
        fpsLabel.setValue(String.format("FPS: %d | UPS: %d | Frame time: %f ms", fps, ups, frameTime));
    }
}
