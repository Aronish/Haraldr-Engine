package graphics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class VertexBufferLayout implements Iterable<VertexBufferElement>
{
    private List<VertexBufferElement> elements;
    private int stride = 0;

    public VertexBufferLayout(VertexBufferElement... elements)
    {
        this.elements = Arrays.asList(elements);
        calculateStrideAndOffsets();
    }

    private void calculateStrideAndOffsets()
    {
        int offset = 0;
        for (VertexBufferElement element : elements)
        {
            element.setOffset(offset);
            offset += element.getSize() * element.getTypeSize();
            stride += element.getSize() * element.getTypeSize();
        }
    }

    public int getStride()
    {
        return stride;
    }

    @Override
    public Iterator<VertexBufferElement> iterator() {
        return elements.iterator();
    }
}
