package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.graphics.HDRGammaCorrectionPass;
import haraldr.graphics.RenderTexture;
import haraldr.graphics.Renderer2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Scene3DPanel extends DockablePanel
{
    private HDRGammaCorrectionPass hdrGammaCorrectionPass = new HDRGammaCorrectionPass(0.5f);
    private RenderTexture sceneTexture = new RenderTexture(
            Vector2f.addY(position, headerSize.getY()),
            Vector2f.addY(size, -headerSize.getY())
    );

    public Scene3DPanel(Vector2f position, Vector2f size, String name)
    {
        super(position, size, new Vector4f(0f), name);
    }

    @Override
    protected PanelModel setupPanelModel()
    {
        return new PanelModel(this)
        {
            @Override
            public void draw(Batch2D batch)
            {
                batch.drawQuad(panel.getPosition(), panel.getHeaderSize(), HEADER_COLOR);
            }
        };
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        super.onEvent(event, window);
        return headerPressed; // Don't block camera movement if content is hovered or pressed.
    }

    @Override
    public void render()
    {
        super.render();
        hdrGammaCorrectionPass.render(sceneTexture, Renderer2D.pixelOrthographic);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        sceneTexture.delete();
    }

    public RenderTexture getSceneTexture()
    {
        return sceneTexture;
    }

    public HDRGammaCorrectionPass getHdrGammaCorrectionPass()
    {
        return hdrGammaCorrectionPass;
    }
}
