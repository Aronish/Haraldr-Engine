package haraldr.ecs;

import haraldr.graphics.JsonModel;

public class ModelComponent
{
    public JsonModel model;

    public ModelComponent(String path)
    {
        this.model = new JsonModel(path);
    }
}
