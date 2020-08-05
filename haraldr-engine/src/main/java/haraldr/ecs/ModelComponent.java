package haraldr.ecs;

import haraldr.graphics.Model;

public class ModelComponent
{
    public Model model;

    public ModelComponent(String path)
    {
        this.model = new Model(path);
    }
}
