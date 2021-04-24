package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.UIInfoLabel;
import haraldr.ui.components.UILabeledList;
import haraldr.ui.groups.UIVerticalListGroup;
import haraldr.ui.groups.VerticalListInsertData;

public class RendererInfoPanel extends DockablePanel<UIVerticalListGroup>
{
    private UIInfoLabel fpsLabel;

    public RendererInfoPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
    }

    @Override
    protected void initializeUI()
    {
        uiRoot = new UIVerticalListGroup();
        UILabeledList uiLabeledList = new UILabeledList(uiLayerStack, 1, Vector2f.addY(position, headerSize.getY()), size);
        fpsLabel = new UIInfoLabel(uiLayerStack, 1, "");
        uiLabeledList.addComponent("Frame Info: ", fpsLabel);
        uiRoot.addComponent(new VerticalListInsertData(uiLabeledList));
    }

    public void setFrameRateInfo(int fps, int ups, double frameTime)
    {
        fpsLabel.setValue(String.format("FPS: %d | UPS: %d | Frame time: %f ms", fps, ups, frameTime));
    }
}
