package offlinerenderer;

import haraldr.event.Event;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ui.Button;
import haraldr.graphics.ui.HorizontalBreak;
import haraldr.graphics.ui.InfoLabel;
import haraldr.graphics.ui.InputField;
import haraldr.graphics.ui.Pane;
import haraldr.main.Application;
import haraldr.main.IOUtils;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import static org.lwjgl.opengl.GL11.glViewport;

//TODO: Naming convention for Haraldr-Engine cubemaps not figured out yet
public class OfflineRendererApplication extends Application
{
    private Pane mainPane;
    private InputField diffuseIrradianceMapSize;
    private InputField prefilteredEnvironmentMapSize;
    private InfoLabel environmentMapData;
    private InfoLabel diffuseIrradianceMapData;
    private InfoLabel prefilteredEnvironmentMapdata;
    private InfoLabel readyToGenerateIblMaps;
    private InfoLabel diffuseIrradianceMapPath;
    private InfoLabel prefilteredMapPath;
    private Button generateIblMaps;

    private GeneratedCubeMap environmentMap;
    private GeneratedCubeMap diffuseIrradianceMap;
    private GeneratedCubeMap prefilteredEnvironmentMap;
    private boolean diffIrrSizeValid, prefSizeValid;

    public OfflineRendererApplication()
    {
        super(new Window.WindowProperties(1000, 600, 0, false, false, true));
    }

    @Override
    protected void clientInit(Window window)
    {
        mainPane = new Pane(
                new Vector2f(),
                window.getWidth(), window.getHeight(),
                1f, 0.5f,
                false,
                "Haraldr Offline Renderer"
        );
        //TODO: JSONify
        //Original environment map
        Button loadHdr = new Button("Load HDR", mainPane, () ->
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
                environmentMapData.setText(environmentMap.getName() + " | Size: " + environmentMap.getSize());
                glViewport(0, 0, window.getWidth(), window.getHeight());
            }
        });
        environmentMapData = new InfoLabel("", mainPane);

        //IBL maps
        diffuseIrradianceMapSize = new InputField("Diffuse Irradiance Map Size", mainPane, InputField.InputType.NUMBERS, (addedChar, fullText) ->
        {
            if (fullText.length() > 0 && fullText.length() <= 4)
            {
                double powerOfTwo = Math.log(Integer.parseInt(fullText)) / Math.log(2);
                diffIrrSizeValid = powerOfTwo != 0 && powerOfTwo % 1 == 0;
            } else diffIrrSizeValid = false;
            checkReadiness();
        });
        prefilteredEnvironmentMapSize = new InputField("Prefiltered Map Size", mainPane, InputField.InputType.NUMBERS, (addedChar, fullText) ->
        {
            if (fullText.length() > 0 && fullText.length() <= 4)
            {
                double powerOfTwo = Math.log(Integer.parseInt(fullText)) / Math.log(2);
                prefSizeValid = powerOfTwo != 0 && powerOfTwo % 1 == 0;
            } else prefSizeValid = false;
            checkReadiness();
        });

        readyToGenerateIblMaps = new InfoLabel("Ready to generate?", mainPane);
        diffuseIrradianceMapData = new InfoLabel("", mainPane);
        prefilteredEnvironmentMapdata = new InfoLabel("", mainPane);

        generateIblMaps = new Button("Generate IBL maps", mainPane, () ->
        {
            diffuseIrradianceMap = CubeMapGenerator.createDiffuseIrradianceMap(environmentMap, Integer.parseInt(diffuseIrradianceMapSize.getValue()));
            prefilteredEnvironmentMap = CubeMapGenerator.createPrefilteredEnvironmentMap(environmentMap, Integer.parseInt(prefilteredEnvironmentMapSize.getValue()));
            diffuseIrradianceMapData.setText("Loaded: " + diffuseIrradianceMap.getName() +  " | Size: " + diffuseIrradianceMap.getSize());
            prefilteredEnvironmentMapdata.setText("Loaded: " + prefilteredEnvironmentMap.getName() + " | Size: " + prefilteredEnvironmentMap.getSize());
            glViewport(0, 0, window.getWidth(), window.getHeight());
        });
        generateIblMaps.setEnabled(false);

        //Export options
        //Diffuse Irradiance Map
        Button exportDiffuseIrradianceMap = new Button("Export", mainPane, () -> CubeMapGenerator.exportCubeMap(diffuseIrradianceMap, diffuseIrradianceMapPath.getValue()));
        exportDiffuseIrradianceMap.setEnabled(false);

        Button saveDiffuseIrradianceMapAs = new Button("Save", mainPane, () ->
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                PointerBuffer filterPatterns = stack.mallocPointer(1);
                filterPatterns.put(IOUtils.stringToByteBuffer("*.exr"));
                String path = TinyFileDialogs.tinyfd_saveFileDialog("Save diffuse irradiance map", "", filterPatterns, "");
                diffuseIrradianceMapPath.setText(path == null ? "" : path);
            }
            exportDiffuseIrradianceMap.setEnabled(!diffuseIrradianceMapPath.getValue().isBlank() && diffuseIrradianceMapPath.getValue().endsWith(".exr"));
        });
        diffuseIrradianceMapPath = new InfoLabel("Save Path", mainPane);

        //Prefiltered Map
        Button exportPrefilteredMap = new Button("Export", mainPane, () -> CubeMapGenerator.exportCubeMap(prefilteredEnvironmentMap, prefilteredMapPath.getValue()));
        exportPrefilteredMap.setEnabled(false);

        Button savePrefilteredMap = new Button("Save", mainPane, () ->
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                PointerBuffer filterPatterns = stack.mallocPointer(1);
                filterPatterns.put(IOUtils.stringToByteBuffer("*.exr"));
                String path = TinyFileDialogs.tinyfd_saveFileDialog("Save prefiltered environment map", "", filterPatterns, "");
                prefilteredMapPath.setText(path == null ? "" : path);
            }
            exportPrefilteredMap.setEnabled(!prefilteredMapPath.getValue().isBlank() && prefilteredMapPath.getValue().endsWith(".exr"));
        });
        prefilteredMapPath = new InfoLabel("Save Path", mainPane);

        mainPane.addChild(new HorizontalBreak("ENVIRONMENT MAP", mainPane));
        mainPane.addChild(loadHdr);
        mainPane.addChild(environmentMapData);

        mainPane.addChild(new HorizontalBreak("PRECOMPUTE CUBEMAPS", mainPane));
        mainPane.addChild(diffuseIrradianceMapSize);
        mainPane.addChild(prefilteredEnvironmentMapSize);
        mainPane.addChild(readyToGenerateIblMaps);
        mainPane.addChild(generateIblMaps);
        mainPane.addChild(diffuseIrradianceMapData);
        mainPane.addChild(prefilteredEnvironmentMapdata);

        mainPane.addChild(new HorizontalBreak("DIFFUSE IRRADIANCE MAP", mainPane));
        mainPane.addChild(saveDiffuseIrradianceMapAs);
        mainPane.addChild(diffuseIrradianceMapPath);
        mainPane.addChild(exportDiffuseIrradianceMap);

        mainPane.addChild(new HorizontalBreak("PREFILTERED MAP", mainPane));
        mainPane.addChild(savePrefilteredMap);
        mainPane.addChild(prefilteredMapPath);
        mainPane.addChild(exportPrefilteredMap);
        checkReadiness();

        Renderer.disableDepthTest();
    }

    private void checkReadiness()
    {
        if (diffIrrSizeValid && prefSizeValid && environmentMap != null)
        {
            readyToGenerateIblMaps.setText("Ready!");
            generateIblMaps.setEnabled(true);
        } else
        {
            readyToGenerateIblMaps.setText("Sizes must be powers of two (2 - 8192) and HDR has to be loaded!");
            generateIblMaps.setEnabled(false);
        }
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        mainPane.onEvent(event, window);
    }

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer2D.begin();
        mainPane.render();
        Renderer2D.end();
        mainPane.renderText();
    }

    @Override
    public void clientDispose()
    {
        CubeMapGenerator.dispose();
    }
}
