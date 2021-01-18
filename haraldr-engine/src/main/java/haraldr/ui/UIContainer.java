package haraldr.ui;

import haraldr.graphics.Batch2D;

public interface UIContainer
{
    Batch2D getMainBatch();
    default Batch2D getOverlayBatch()
    {
        return getMainBatch();
    }
    
    TextBatch getTextBatch();
    default TextBatch getOverlayTextBatch()
    {
        return getTextBatch();
    }
}