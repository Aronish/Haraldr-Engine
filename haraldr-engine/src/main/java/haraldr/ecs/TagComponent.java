package haraldr.ecs;

import haraldr.dockspace.uicomponents.ComponentPropertyList;
import haraldr.dockspace.uicomponents.UnlabeledInputField;
import org.jetbrains.annotations.Contract;

public class TagComponent implements Component
{
    public String tag;

    @Contract(pure = true)
    public TagComponent(String tag)
    {
        this.tag = tag;
    }

    @Override
    public void extractComponentProperties(ComponentPropertyList componentPropertyList)
    {
        componentPropertyList.addComponent("Tag: ", new UnlabeledInputField<>(componentPropertyList.getParent().getTextBatch(), new UnlabeledInputField.StringValue(tag), value -> tag = value.getValue()));
    }
}