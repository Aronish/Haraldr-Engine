package offlinerenderer;

import haraldr.debug.Logger;
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
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyexr.EXRChannelInfo;
import org.lwjgl.util.tinyexr.EXRHeader;
import org.lwjgl.util.tinyexr.EXRImage;
import org.lwjgl.util.tinyexr.TinyEXR;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;

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
        //TODO: Found this example (there is basically nothing on the internet about this lib):
        //http://forum.lwjgl.org/index.php?topic=6757.0
        EXRHeader header = EXRHeader.create();
        TinyEXR.InitEXRHeader(header);

        EXRImage image = EXRImage.create();
        TinyEXR.InitEXRImage(image);
        image.num_channels(3);

        List<List<Float>> images = new ArrayList<>(3);
        images.add(Stream.generate(() -> 0f).limit(environmentMap.getSize() * environmentMap.getSize()).collect(Collectors.toList()));
        images.add(Stream.generate(() -> 0f).limit(environmentMap.getSize() * environmentMap.getSize()).collect(Collectors.toList()));
        images.add(Stream.generate(() -> 0f).limit(environmentMap.getSize() * environmentMap.getSize()).collect(Collectors.toList()));

        FloatBuffer data = MemoryUtil.memAllocFloat(environmentMap.getSize() * environmentMap.getSize() * 4 * 2);
        glBindTexture(GL_TEXTURE_CUBE_MAP, environmentMap.getCubeMapId());
        glGetTexImage(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL_RGB, GL_FLOAT, data);

        for (int i = 0; i < environmentMap.getSize() * environmentMap.getSize(); ++i)
        {
            images.get(0).set(i, data.get(3 * i));
            images.get(1).set(i, data.get(3 * i + 1));
            images.get(2).set(i, data.get(3 * i + 2));
        }

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            PointerBuffer imagePtr = stack.mallocPointer(3);
            imagePtr.put(0, ByteBuffer.allocateDirect(images.get(2).size()).order(ByteOrder.nativeOrder()).asFloatBuffer());
            imagePtr.put(1, ByteBuffer.allocateDirect(images.get(1).size()).order(ByteOrder.nativeOrder()).asFloatBuffer());
            imagePtr.put(2, ByteBuffer.allocateDirect(images.get(0).size()).order(ByteOrder.nativeOrder()).asFloatBuffer());
            image.images(imagePtr);
            image.width(environmentMap.getSize());
            image.height(environmentMap.getSize());
        }

        header.num_channels(3);
        EXRChannelInfo.Buffer channelInfo = EXRChannelInfo.create(3);
        header.channels(channelInfo);

        //Something missing
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer pixelType = stack.mallocInt(1);
            pixelType.put(4 * header.num_channels());
            header.pixel_types(pixelType);
        }

        for (int i = 0; i < header.num_channels(); ++i)
        {
            header.pixel_types().put(i, TinyEXR.TINYEXR_PIXELTYPE_FLOAT);
            header.requested_pixel_types().put(i, TinyEXR.TINYEXR_PIXELTYPE_HALF);
        }

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            PointerBuffer err = stack.mallocPointer(1);
            int ret = TinyEXR.SaveEXRImageToFile(image, header, "C:/dev/Haraldr-Engine/haraldr-offline-renderer/src/main/resources/output/test.exr", err);
            if (ret != TinyEXR.TINYEXR_SUCCESS)
            {
                Logger.error("Could not save EXR");
                TinyEXR.FreeEXRErrorMessage(err.getByteBuffer(1));
            }
        }

        header.channels().free();
        header.free();

        MemoryUtil.memFree(data);
/*
        int channels = 4;
        SampleModel sampleModel = new PixelInterleavedSampleModel(
                DataBuffer.TYPE_FLOAT,
                environmentMap.getSize(),
                environmentMap.getSize(),
                channels,
                environmentMap.getSize() * channels,
                new int[] { 0, 1, 2, 3 }
        );

        DataBuffer dataBuffer = new DataBufferFloat(environmentMap.getSize() * environmentMap.getSize() * channels);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel colorModel = new ComponentColorModel(colorSpace, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_FLOAT);

        BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);

        File outputFolder = new File("haraldr-offline-renderer/src/main/resources/output");
        if (Files.exists(outputFolder.toPath()))
        {
            Logger.info("Found output");
            File imageFile = new File("haraldr-offline-renderer/src/main/resources/output/test.tif");
            Logger.info(imageFile.getAbsolutePath());
            try
            {
                Logger.info(ImageIO.write(image, "TIFF", imageFile));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        MemoryUtil.memFree(data);
        */
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
