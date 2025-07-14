package haraldr.ui.components;

import haraldr.ui.UIEventLayer;

import java.util.List;

/**
 * A UI element that can be part of a UI hierarchy. Allows for retrieval of layers from root element layer stack.
 * The UI system is a hierarchy where some elements may have parents. The underlying rendering model is more like a collection of flat layer lists.
 */
public interface UIHierarchical
{
    List<UIEventLayer> getLayers();
    UIEventLayer getLayer(int index);
}