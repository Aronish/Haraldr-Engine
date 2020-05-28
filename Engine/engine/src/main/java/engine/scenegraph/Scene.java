package engine.scenegraph;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    private List<SceneObject> children = new ArrayList<>();

    public void addChildNode(SceneObject childObject)
    {
        children.add(childObject);
    }
}
