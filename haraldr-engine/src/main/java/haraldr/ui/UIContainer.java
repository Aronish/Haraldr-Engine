package haraldr.ui;

import java.util.List;

public interface UIContainer
{
    List<UILayer> getLayers();
    UILayer getLayer(int index);
}