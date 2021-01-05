package haraldr.ui;

import haraldr.graphics.Batch2D;

public interface UIContainer
{
    Batch2D getMainBatch();
    TextBatch getTextBatch();
}