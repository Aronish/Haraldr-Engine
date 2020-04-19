package engine.graphics;

import engine.main.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class Mesh
{
    private List<Float> vertexData = new ArrayList<>();
    private int[] indices;

    public Mesh(float[] vertexData, int[] indices)
    {
        this.vertexData.addAll(ArrayUtils.toList(vertexData));
        this.indices = indices;
    }

    public List<Float> getVertexData()
    {
        return vertexData;
    }

    public int[] getIndices()
    {
        return indices;
    }
}
