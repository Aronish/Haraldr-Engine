package sandbox;

import engine.graphics.Renderer;
import engine.main.ECSApplication;
import engine.main.ProgramArguments;
import engine.main.Window;

public class ECSExampleApplication extends ECSApplication
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
    }
}
