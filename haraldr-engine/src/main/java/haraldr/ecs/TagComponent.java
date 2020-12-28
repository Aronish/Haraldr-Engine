package haraldr.ecs;

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
    public void acceptVisitor(ComponentVisitor visitor)
    {
        visitor.visit(this);
    }
}