package haraldr.ecs;

import org.jetbrains.annotations.Contract;

public class TagComponent
{
    public String tag;

    @Contract(pure = true)
    public TagComponent(String tag)
    {
        this.tag = tag;
    }
}
