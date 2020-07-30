package sandbox;

import haraldr.graphics.Renderer;
import haraldr.main.Application;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;

class ExampleApplication extends Application
{
    @Override
    public void start()
    {
        int samples = ProgramArguments.isArgumentSet("MSAA") ? Integer.parseInt(ProgramArguments.getStringValue("MSAA")) : 0;
        Window.WindowProperties windowProperties = new Window.WindowProperties(1280, 720, samples, true, false, false);
        init(windowProperties);
        loop();
    }

    @Override
    protected void init(Window.WindowProperties windowProperties)
    {
        super.init(windowProperties);
        Renderer.setClearColor(0.1f, 0.1f, 0.2f, 1f);
        //layerStack.pushLayer(new PBRLayer());
        layerStack.pushLayer(new MaterialLayer());
        if (EntryPoint.DEBUG) layerStack.pushOverlay(new DebugLayer());
    }
}