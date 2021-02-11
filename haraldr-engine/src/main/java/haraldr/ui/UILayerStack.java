package haraldr.ui;

import haraldr.event.Event;
import haraldr.main.Window;
import haraldr.ui.components.UIContainer;
import haraldr.ui.components.UILayerable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UILayerStack implements UIContainer
{
    private LinkedList<UIEventLayer> uiEventLayers = new LinkedList<>();

    public void addLayer(UIEventLayer layer)
    {
        uiEventLayers.add(layer);
    }

    public UILayerable.UIEventResult onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false, consumed = false;
        for (Iterator<UIEventLayer> it = uiEventLayers.descendingIterator(); it.hasNext();)
        {
            UIEventLayer uiEventLayer = it.next();
            UILayerable.UIEventResult eventResult = uiEventLayer.onEvent(event, window);
            if (eventResult.requiresRedraw()) requiresRedraw = true;
            if (eventResult.consumed())
            {
                consumed = true;
                break;
            }
        }
        return new UILayerable.UIEventResult(requiresRedraw, consumed);
    }
/*
    public void addPosition(Vector2f difference)
    {
        for (UIEventLayer uiEventLayer : uiEventLayers)
        {
            uiEventLayer.addPosition(difference);
        }
    }
*/
    public void refresh()
    {
        for (UIEventLayer uiEventLayer : uiEventLayers)
        {
            uiEventLayer.getTextBatch().refreshTextMeshData();
        }
    }

    public void draw()
    {
        for (UIEventLayer uiEventLayer : uiEventLayers)
        {
            uiEventLayer.draw();
        }
    }

    public void render()
    {
        for (UIEventLayer uiEventLayer : uiEventLayers)
        {
            uiEventLayer.render();
        }
    }

    public void clear()
    {
        for (UIEventLayer uiEventLayer : uiEventLayers)
        {
            uiEventLayer.clear();
        }
    }

    @Override
    public List<UIEventLayer> getLayers()
    {
        return uiEventLayers;
    }

    @Override
    public UIEventLayer getLayer(int index)
    {
        if (index >= uiEventLayers.size())
        {
            UIEventLayer layer = new UIEventLayer();
            uiEventLayers.add(layer);
            return layer;
        }
        return uiEventLayers.get(index);
    }
}
