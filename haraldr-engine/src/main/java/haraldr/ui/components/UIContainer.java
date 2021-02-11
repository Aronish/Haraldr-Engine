package haraldr.ui.components;

import haraldr.ui.UIEventLayer;

import java.util.List;

public interface UIContainer
{
    List<UIEventLayer> getLayers();
    UIEventLayer getLayer(int index);
}