package engine.scenegraph;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SceneObject
{
    private SceneObject parent;
    private List<SceneObject> children = new ArrayList<>();

    public SceneObject(@NotNull Scene scene)
    {
        scene.addChildNode(this);
    }

    public SceneObject(@NotNull Scene scene, @NotNull SceneObject parent)
    {
        this.parent = parent;
        parent.addChildObject(this);
        scene.addChildNode(this);
    }

    public void addChildObject(SceneObject childNode)
    {
        children.add(childNode);
    }
}
