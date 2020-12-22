package haraldr.ecs;

import haraldr.dockspace.uicomponents.ComponentPropertyList;
import haraldr.graphics.Model;

public class ModelComponent implements Component
{
    public Model model; //TODO: Add serialization

    public ModelComponent(String path)
    {
        this.model = new Model(path);
    }

    @Override
    public void extractComponentProperties(ComponentPropertyList componentPropertyList)
    {
    }
}
