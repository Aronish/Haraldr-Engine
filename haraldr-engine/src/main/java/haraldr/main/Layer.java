package haraldr.main;

import haraldr.graphics.Batch2D;
import haraldr.ui.Font;
import haraldr.ui.TextBatch;

import java.util.ArrayList;
import java.util.List;

public class Layer
{
    private List<Batch2D> batches = new ArrayList<>();
    private List<TextBatch> textBatches = new ArrayList<>();

    public Batch2D createBatch2D()
    {
        Batch2D batch;
        batches.add(batch = new Batch2D());
        return batch;
    }

    public TextBatch createTextBatch()
    {
        TextBatch textBatch;
        textBatches.add(textBatch = new TextBatch(Font.DEFAULT_FONT));
        return textBatch;
    }

    public void render()
    {
        for (Batch2D batch : batches)
        {
            batch.render();
        }
        for (TextBatch textBatch : textBatches)
        {
            textBatch.render();
        }
    }
}