package offlinerenderer;

import haraldr.dockspace.DockPosition;
import haraldr.dockspace.Dockspace;
import haraldr.event.Event;
import haraldr.graphics.Renderer;
import haraldr.main.Application;
import haraldr.main.IOUtils;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.UIButton;
import haraldr.ui.components.UIHorizontalBreak;
import haraldr.ui.components.UIInfoLabel;
import haraldr.ui.components.UIInputField;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import static org.lwjgl.opengl.GL11.glViewport;
//TODO: Fix everything
public class OfflineRendererApplication extends Application
{
    private Dockspace dockspace;
    private MainPanel mainPanel;

    private UIInputField<UIInputField.IntValue> diffuseIrradianceMapSize;
    private UIInputField<UIInputField.IntValue> prefilteredEnvironmentMapSize;
    private UIInfoLabel environmentMapData;
    private UIInfoLabel diffuseIrradianceMapData;
    private UIInfoLabel prefilteredEnvironmentMapdata;
    private UIInfoLabel readyToGenerateIblMaps;
    private UIInfoLabel diffuseIrradianceMapPath;
    private UIInfoLabel prefilteredMapPath;
    private UIButton generateIblMaps;

    private GeneratedCubeMap environmentMap;
    private GeneratedCubeMap diffuseIrradianceMap;
    private GeneratedCubeMap prefilteredEnvironmentMap;
    private boolean diffIrrSizeValid, prefSizeValid;

    public OfflineRendererApplication()
    {
        super(new Window.WindowProperties("Haraldr Offline Renderer", 1000, 600, 0, false, false, true, true));
    }

    @Override
    protected void clientInit(Window window)
    {
        dockspace = new Dockspace(new Vector2f(), new Vector2f(window.getWidth(), window.getHeight()));
        mainPanel = new MainPanel(new Vector2f(), new Vector2f(window.getWidth(), window.getHeight()), new Vector4f(0.2f, 0.2f, 0.2f, 1f), "Haraldr Offline Renderer");

        //Original environment map
        UIButton loadHdr = new UIButton(mainPanel.getLayers(), 0, () ->
        {
            String sourcePath;
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                PointerBuffer filterPatterns = stack.mallocPointer(1);
                filterPatterns.put(IOUtils.stringToByteBuffer("*.hdr"));
                sourcePath = TinyFileDialogs.tinyfd_openFileDialog("Select .hdr file", "", filterPatterns, "", false);
                if (sourcePath == null) sourcePath = "";
            }

            if (!sourcePath.isBlank() && sourcePath.endsWith(".hdr"))
            {
                environmentMap = CubeMapGenerator.createEnvironmentMap(sourcePath);
                environmentMapData.setValue(environmentMap.getName() + " | Size: " + environmentMap.getSize());
                glViewport(0, 0, window.getWidth(), window.getHeight());
            }
        });
        environmentMapData = new UIInfoLabel(mainPanel.getLayers(), 0, "");

        //IBL maps
        diffuseIrradianceMapSize = new UIInputField<>(mainPanel.getLayers(), 0, new UIInputField.IntValue(16, 2, 8192), (value) ->
        {
            if (value.toString().length() > 0 && value.toString().length() <= 4)
            {
                double powerOfTwo = Math.log(Integer.parseInt(value.toString())) / Math.log(2);
                diffIrrSizeValid = powerOfTwo != 0 && powerOfTwo % 1 == 0;
            } else diffIrrSizeValid = false;
            checkReadiness();
        });
        prefilteredEnvironmentMapSize = new UIInputField<>(mainPanel.getLayers(), 0, new UIInputField.IntValue(128, 2, 8192), (value) ->
        {
            if (value.toString().length() > 0 && value.toString().length() <= 4)
            {
                double powerOfTwo = Math.log(Integer.parseInt(value.toString())) / Math.log(2);
                prefSizeValid = powerOfTwo != 0 && powerOfTwo % 1 == 0;
            } else prefSizeValid = false;
            checkReadiness();
        });

        readyToGenerateIblMaps = new UIInfoLabel(mainPanel.getLayers(), 0, "");
        diffuseIrradianceMapData = new UIInfoLabel(mainPanel.getLayers(), 0, "");
        prefilteredEnvironmentMapdata = new UIInfoLabel(mainPanel.getLayers(), 0, "");

        generateIblMaps = new UIButton(mainPanel.getLayers(), 0, () ->
        {
            diffuseIrradianceMap = CubeMapGenerator.createDiffuseIrradianceMap(environmentMap, Integer.parseInt(diffuseIrradianceMapSize.getValue().toString()));
            prefilteredEnvironmentMap = CubeMapGenerator.createPrefilteredEnvironmentMap(environmentMap, Integer.parseInt(prefilteredEnvironmentMapSize.getValue().toString()));
            diffuseIrradianceMapData.setValue("Loaded: " + diffuseIrradianceMap.getName() +  " | Size: " + diffuseIrradianceMap.getSize());
            prefilteredEnvironmentMapdata.setValue("Loaded: " + prefilteredEnvironmentMap.getName() + " | Size: " + prefilteredEnvironmentMap.getSize());
            glViewport(0, 0, window.getWidth(), window.getHeight());
        });
        generateIblMaps.setEnabled(false);

        //Export options
        //Diffuse Irradiance Map
        UIButton exportDiffuseIrradianceMap = new UIButton(mainPanel.getLayers(), 0, () -> CubeMapGenerator.exportCubeMap(diffuseIrradianceMap, diffuseIrradianceMapPath.getValue()));
        exportDiffuseIrradianceMap.setEnabled(false);

        UIButton saveDiffuseIrradianceMapAs = new UIButton(mainPanel.getLayers(), 0, () ->
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                PointerBuffer filterPatterns = stack.mallocPointer(1);
                filterPatterns.put(IOUtils.stringToByteBuffer("*.exr"));
                String path = TinyFileDialogs.tinyfd_saveFileDialog("Save diffuse irradiance map", "", filterPatterns, "");
                diffuseIrradianceMapPath.setValue(path == null ? "" : path);
            }
            exportDiffuseIrradianceMap.setEnabled(!diffuseIrradianceMapPath.getValue().isBlank() && diffuseIrradianceMapPath.getValue().endsWith(".exr"));
        });
        diffuseIrradianceMapPath = new UIInfoLabel(mainPanel.getLayers(), 0, "");

        //Prefiltered Map
        UIButton exportPrefilteredMap = new UIButton(mainPanel.getLayers(), 0, () -> CubeMapGenerator.exportCubeMap(prefilteredEnvironmentMap, prefilteredMapPath.getValue()));
        exportPrefilteredMap.setEnabled(false);

        UIButton savePrefilteredMap = new UIButton(mainPanel.getLayers(), 0, () ->
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                PointerBuffer filterPatterns = stack.mallocPointer(1);
                filterPatterns.put(IOUtils.stringToByteBuffer("*.exr"));
                String path = TinyFileDialogs.tinyfd_saveFileDialog("Save prefiltered environment map", "", filterPatterns, "");
                prefilteredMapPath.setValue(path == null ? "" : path);
            }
            exportPrefilteredMap.setEnabled(!prefilteredMapPath.getValue().isBlank() && prefilteredMapPath.getValue().endsWith(".exr"));
        });
        prefilteredMapPath = new UIInfoLabel(mainPanel.getLayers(), 0, "");

        mainPanel.addComponent("", new UIHorizontalBreak(mainPanel.getLayers(), 0, 20));
        mainPanel.addComponent("Load HDR", loadHdr);
        mainPanel.addComponent("", environmentMapData);

        mainPanel.addComponent("PRECOMPUTE CUBEMAPS", new UIHorizontalBreak(mainPanel.getLayers(), 0, 20));
        mainPanel.addComponent("Diffuse Irradiance Map Size", diffuseIrradianceMapSize);
        mainPanel.addComponent("Prefiltered Map Size", prefilteredEnvironmentMapSize);
        mainPanel.addComponent("Ready to generate?", readyToGenerateIblMaps);
        mainPanel.addComponent("Generate IBL maps", generateIblMaps);
        mainPanel.addComponent("", diffuseIrradianceMapData);
        mainPanel.addComponent("", prefilteredEnvironmentMapdata);

        mainPanel.addComponent("DIFFUSE IRRADIANCE MAP", new UIHorizontalBreak(mainPanel.getLayers(), 0, 20));
        mainPanel.addComponent("Save", saveDiffuseIrradianceMapAs);
        mainPanel.addComponent("Save Path", diffuseIrradianceMapPath);
        mainPanel.addComponent("Export", exportDiffuseIrradianceMap);

        mainPanel.addComponent("PREFILTERED MAP", new UIHorizontalBreak(mainPanel.getLayers(), 0, 20));
        mainPanel.addComponent("Save", savePrefilteredMap);
        mainPanel.addComponent("Save Path", prefilteredMapPath);
        mainPanel.addComponent("Export", exportPrefilteredMap);
        dockspace.addPanel(mainPanel);
        dockspace.dockPanel(mainPanel, DockPosition.CENTER);

        checkReadiness();

        Renderer.disableDepthTest();
    }

    private void checkReadiness()
    {
        if (diffIrrSizeValid && prefSizeValid && environmentMap != null)
        {
            readyToGenerateIblMaps.setValue("Ready!");
            generateIblMaps.setEnabled(true);
        } else
        {
            readyToGenerateIblMaps.setValue("Sizes must be powers of two (2 - 8192) and HDR has to be loaded!");
            generateIblMaps.setEnabled(false);
        }
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        dockspace.onEvent(event, window);
    }

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {
    }

    @Override
    protected void clientRender(Window window)
    {
        dockspace.renderPanels();
    }

    @Override
    public void clientDispose()
    {
        dockspace.dispose();
        CubeMapGenerator.dispose();
    }
}
