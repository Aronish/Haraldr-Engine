package offlinerenderer;

import haraldr.event.Event;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.ui.Button;
import haraldr.graphics.ui.InfoLabel;
import haraldr.graphics.ui.InputField;
import haraldr.graphics.ui.Pane;
import haraldr.main.Application;
import haraldr.main.IOUtils;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL45.glGetTextureImage;

public class OfflineRendererApplication extends Application
{
    private Pane mainPane;
    private InputField sourcePath;
    private InputField diffuseIrradianceMapSize;
    private InputField prefilteredEnvironmentMapSize;
    private InputField exportPath;
    private InfoLabel environmentMapData;
    private InfoLabel diffuseIrradianceMapData;
    private InfoLabel prefilteredEnvironmentMapdata;
    private InfoLabel readyToGenerateIblMaps;
    private Button loadHdr;
    private Button generateIblMaps;
    private Button export;

    private GeneratedCubeMap environmentMap;
    private GeneratedCubeMap diffuseIrradianceMap;
    private GeneratedCubeMap prefilteredEnvironmentMap;
    private int diffIrrSize = 32, prefSize = 128;
    private boolean diffIrrSizeValid, prefSizeValid;

    public OfflineRendererApplication()
    {
        super(new Window.WindowProperties(720, 720, 0, false, false, true));
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
        sourcePath = new InputField("Equirectangular HDR path", mainPane);
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

        environmentMapData = new InfoLabel("Environment Map", mainPane);
        diffuseIrradianceMapData = new InfoLabel("Diffuse Irradiance Map", mainPane);
        prefilteredEnvironmentMapdata = new InfoLabel("Prefiltered Map", mainPane);
        readyToGenerateIblMaps = new InfoLabel("Ready to generate?", mainPane);

        generateIblMaps = new Button("Generate IBL maps", mainPane, () ->
        {
            diffuseIrradianceMap = CubeMapGenerator.createDiffuseIrradianceMap(environmentMap, diffIrrSize);
            prefilteredEnvironmentMap = CubeMapGenerator.createPrefilteredEnvironmentMap(environmentMap, prefSize);
            diffuseIrradianceMapData.setText(diffuseIrradianceMap.getName() +  " | Size: " + diffuseIrradianceMap.getSize());
            prefilteredEnvironmentMapdata.setText(prefilteredEnvironmentMap.getName() + " | Size: " + prefilteredEnvironmentMap.getSize());
            glViewport(0, 0, window.getWidth(), window.getHeight());
        });
        generateIblMaps.setEnabled(false);

        loadHdr = new Button("Load HDR", mainPane, () ->
        {
            if (IOUtils.resourceExists(sourcePath.getValue()) && sourcePath.getValue().endsWith("hdr"))
            {
                environmentMap = CubeMapGenerator.createEnvironmentMap(sourcePath.getValue());
                environmentMapData.setText(environmentMap.getName() + " | Size: " + environmentMap.getSize());
                glViewport(0, 0, window.getWidth(), window.getHeight());
                generateIblMaps.setEnabled(true);
            } else
            {
                generateIblMaps.setEnabled(false);
                environmentMapData.setText("");
                diffuseIrradianceMapData.setText("");
                prefilteredEnvironmentMapdata.setText("");
            }
        });

        exportPath = new InputField("Export path", mainPane);
        export = new Button("Export Maps", mainPane, this::exportMaps);

        mainPane.addChild(sourcePath);
        mainPane.addChild(loadHdr);
        mainPane.addChild(environmentMapData);
        mainPane.addChild(diffuseIrradianceMapSize);
        mainPane.addChild(prefilteredEnvironmentMapSize);
        mainPane.addChild(readyToGenerateIblMaps);
        mainPane.addChild(generateIblMaps);
        mainPane.addChild(diffuseIrradianceMapData);
        mainPane.addChild(prefilteredEnvironmentMapdata);
        mainPane.addChild(exportPath);
        mainPane.addChild(export);
        checkReadiness();

        Renderer.disableDepthTest();
    }

    private void checkReadiness()
    {
        if (diffIrrSizeValid && prefSizeValid)
        {
            readyToGenerateIblMaps.setText("Ready!");
            generateIblMaps.setEnabled(true);
        } else
        {
            readyToGenerateIblMaps.setText("Invalid sizes! Must be powers of two (2 - 8192)");
            generateIblMaps.setEnabled(false);
        }
    }

    private void exportMaps()
    {
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
