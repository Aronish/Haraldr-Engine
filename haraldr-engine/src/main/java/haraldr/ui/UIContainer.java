package haraldr.ui;

import java.util.List;

public interface UIContainer
{
    List<UIEventLayer> getLayers();
    UIEventLayer getLayer(int index);
}