package haraldr.ecs;

import haraldr.graphics.Model;

public class ModelComponent implements Component
{
    public Model model;

    public ModelComponent(String path)
    {
        this.model = new Model(path);
    }

    @Override
    public void acceptVisitor(ComponentVisitor visitor)
    {
        visitor.visit(this);
    }
}