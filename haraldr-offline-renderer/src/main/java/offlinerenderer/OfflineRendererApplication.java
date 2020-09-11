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
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyexr.EXRChannelInfo;
import org.lwjgl.util.tinyexr.EXRHeader;
import org.lwjgl.util.tinyexr.EXRImage;
import org.lwjgl.util.tinyexr.TinyEXR;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
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
    private InputField exportName;
    private InfoLabel environmentMapData;
    private InfoLabel diffuseIrradianceMapData;
    private InfoLabel prefilteredEnvironmentMapdata;
    private InfoLabel readyToGenerateIblMaps;
    private Button generateIblMaps;
    private Button export;

    private GeneratedCubeMap environmentMap;
    private GeneratedCubeMap diffuseIrradianceMap;
    private GeneratedCubeMap prefilteredEnvironmentMap;
    private boolean diffIrrSizeValid, prefSizeValid;

    public OfflineRendererApplication()
    {
        super(new Window.WindowProperties(800, 600, 0, false, false, true));
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
        //Original environment map
        sourcePath = new InputField("Equirectangular HDR path", mainPane);
        Button loadHdr = new Button("Load HDR", mainPane, () ->
        {
            if (IOUtils.resourceExists(sourcePath.getValue()) && sourcePath.getValue().endsWith("hdr"))
            {
                environmentMap = CubeMapGenerator.createEnvironmentMap(sourcePath.getValue());
                environmentMapData.setText("Loaded: " + environmentMap.getName() + " | Size: " + environmentMap.getSize());
                glViewport(0, 0, window.getWidth(), window.getHeight());
            } else
            {
                generateIblMaps.setEnabled(false);
                environmentMapData.setText("");
                diffuseIrradianceMapData.setText("");
                prefilteredEnvironmentMapdata.setText("");
            }
        });
        environmentMapData = new InfoLabel("Environment Map", mainPane);

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
        diffuseIrradianceMapData = new InfoLabel("Diffuse Irradiance Map", mainPane);
        prefilteredEnvironmentMapdata = new InfoLabel("Prefiltered Map", mainPane);

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
        exportName = new InputField("Name", mainPane, ((addedChar, fullText) ->
        {
            export.setEnabled(fullText.length() > 0 && diffuseIrradianceMap != null && prefilteredEnvironmentMap != null);
        }));
        export = new Button("Export Maps", mainPane, () ->
        {
            exportCubeMap(diffuseIrradianceMap, exportName.getValue() + "_DIFF_IRR.exr");
            exportCubeMap(prefilteredEnvironmentMap, exportName.getValue() + "_PREF.exr");
        });
        export.setEnabled(false);

        mainPane.addChild(sourcePath);
        mainPane.addChild(loadHdr);
        mainPane.addChild(environmentMapData);

        mainPane.addChild(diffuseIrradianceMapSize);
        mainPane.addChild(prefilteredEnvironmentMapSize);
        mainPane.addChild(readyToGenerateIblMaps);
        mainPane.addChild(generateIblMaps);
        mainPane.addChild(diffuseIrradianceMapData);
        mainPane.addChild(prefilteredEnvironmentMapdata);

        mainPane.addChild(exportName);
        mainPane.addChild(export);
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

    private void exportCubeMap(GeneratedCubeMap cubeMap, String name)
    {
        String path = "C:/dev/Haraldr-Engine/haraldr-offline-renderer/src/main/resources/output/" + name;

        //Load cubemap faces
        int width = cubeMap.getSize();
        int height = cubeMap.getSize();
        int totalHeight = 0;
        for (int mipLevel = 0; mipLevel <= cubeMap.getHighestMipLevel(); ++mipLevel)
        {
            totalHeight += cubeMap.getSize() * Math.pow(0.5, mipLevel);
        }

        FloatBuffer[][] faceData = new FloatBuffer[cubeMap.getHighestMipLevel() + 1][6]; //Holds raw pixel data from OpenGL

        glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMap.getCubeMapId());
        for (int mipLevel = 0; mipLevel <= cubeMap.getHighestMipLevel(); ++mipLevel)
        {
            for (int i = 0; i < faceData[mipLevel].length; ++i)
            {
                FloatBuffer face = MemoryUtil.memAllocFloat(cubeMap.getSize() * cubeMap.getSize() * 3 * 2);
                glGetTexImage(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, mipLevel, GL_RGBA, GL_FLOAT, face); //OpenGL stores in RGBA even without alpha.
                faceData[mipLevel][i] = face;
            }
        }

        float[][] channelData = new float[3][6 * width * totalHeight]; //Holds raw pixel data for each color channel

        int mipLevelPixelOffset = 0;
        for (int mipLevel = 0; mipLevel < faceData.length; ++mipLevel)
        {
            int mipLevelWidth = (int) (width * Math.pow(0.5f, mipLevel));
            int mipLevelHeight = (int) (height * Math.pow(0.5f, mipLevel));
            int lineOffset = mipLevelWidth * 4;
            int mipLineOffset = 6 * width - 6 * mipLevelWidth;

            for (int i = 0, pos = mipLevelPixelOffset; i < 6 * mipLevelWidth * mipLevelHeight; ++i) //Separate color channels from all faces
            {
                int face = i / mipLevelWidth % 6;
                int index = i % mipLevelWidth;
                int line = i / (mipLevelWidth * 6);
                channelData[0][pos] = faceData[mipLevel][face].get(4 * index + line * lineOffset);     //B
                channelData[1][pos] = faceData[mipLevel][face].get(4 * index + line * lineOffset + 1); //G
                channelData[2][pos] = faceData[mipLevel][face].get(4 * index + line * lineOffset + 2); //R
                if (mipLevel > 0 && ((i % (6 * mipLevelWidth)) + 1) / (6 * mipLevelWidth) == 1) pos += mipLineOffset + 1;
                else ++pos;
            }
            Logger.info(mipLevelPixelOffset);
            mipLevelPixelOffset += 6 * width * mipLevelHeight;
        }

        //The internal storage format is BGRA, this reorders it for TinyEXR.
        FloatBuffer red = MemoryUtil.memAllocFloat(6 * width * totalHeight);
        red.put(channelData[2]);
        red.flip();
        FloatBuffer green = MemoryUtil.memAllocFloat(6 * width * totalHeight);
        green.put(channelData[1]);
        green.flip();
        FloatBuffer blue = MemoryUtil.memAllocFloat(6 * width * totalHeight);
        blue.put(channelData[0]);
        blue.flip();

        //Create EXR image
        EXRHeader exr_header = EXRHeader.create();
        TinyEXR.InitEXRHeader(exr_header);

        EXRImage exr_image = EXRImage.create();
        TinyEXR.InitEXRImage(exr_image);

        int numChannels = 3;
        exr_image.num_channels(numChannels);
        exr_header.num_channels(numChannels);

        PointerBuffer imagesPtr = BufferUtils.createPointerBuffer(numChannels);
        imagesPtr.put(red);
        imagesPtr.put(green);
        imagesPtr.put(blue);
        imagesPtr.flip();

        exr_image.images(imagesPtr);
        exr_image.width(6 * width);
        exr_image.height(totalHeight);

        EXRChannelInfo.Buffer channelInfos = EXRChannelInfo.create(numChannels);
        exr_header.channels(channelInfos);
        channelInfos.get(0).name(stringToByteBuffer("R\0"));
        channelInfos.get(1).name(stringToByteBuffer("G\0"));
        channelInfos.get(2).name(stringToByteBuffer("B\0"));

        IntBuffer pixelTypes = BufferUtils.createIntBuffer(exr_header.num_channels());
        IntBuffer requestedPixelTypes = BufferUtils.createIntBuffer(exr_header.num_channels());
        exr_header.pixel_types(pixelTypes);
        exr_header.requested_pixel_types(requestedPixelTypes);
        for (int i = 0; i < exr_header.num_channels(); i++)
        {
            pixelTypes.put(i, TinyEXR.TINYEXR_PIXELTYPE_FLOAT);
            requestedPixelTypes.put(i, TinyEXR.TINYEXR_PIXELTYPE_FLOAT);
        }

        PointerBuffer err = BufferUtils.createPointerBuffer(1);
        int ret = TinyEXR.SaveEXRImageToFile(exr_image, exr_header, path, err);
        if (ret != TinyEXR.TINYEXR_SUCCESS)
        {
            Logger.error("Could not export EXR + " + path + ": " + err.get(0));
        }
        Logger.info("Exported EXR to " + path);

        for (FloatBuffer[] facesAtMipLevel : faceData)
        {
            for (FloatBuffer buffer : facesAtMipLevel) MemoryUtil.memFree(buffer);
        }
        MemoryUtil.memFree(red);
        MemoryUtil.memFree(green);
        MemoryUtil.memFree(blue);
    }

    private static ByteBuffer stringToByteBuffer(String s)
    {
        byte[] bytes = s.getBytes();
        ByteBuffer bb = BufferUtils.createByteBuffer(bytes.length);
        bb.put(bytes);
        bb.flip();
        return bb;
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
