package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.graphics.HDRGammaCorrectionPass;
import haraldr.graphics.RenderTexture;
import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Scene3DPanel extends DockablePanel
{
    private HDRGammaCorrectionPass hdrGammaCorrectionPass;
    private RenderTexture sceneTexture;

    public Scene3DPanel(Vector2f position, Vector2f size, String name)
    {
        super(position, size, new Vector4f(0f), name);
        hdrGammaCorrectionPass = new HDRGammaCorrectionPass(0.5f);
        sceneTexture = new RenderTexture(
                Vector2f.add(position, new Vector2f(0f, headerSize.getY())),
                Vector2f.add(position, new Vector2f(0f, -headerSize.getY()))
        );
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
