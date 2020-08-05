package haraldr.scenegraph;

import haraldr.graphics.JsonModel;

import java.util.ArrayList;
import java.util.List;

public class Node
{
    private List<Node> children = new ArrayList<>();

    public void addChild(Node node)
    {
        children.add(node);
    }
}
