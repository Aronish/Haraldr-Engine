package engine.graphics;

import engine.main.ArrayUtils;
import engine.main.EntryPoint;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static engine.main.Application.MAIN_LOGGER;

public class ObjParser
{
    @Nullable
    public static Mesh parseObj(String path)
    {
        try (InputStream inputStream = ObjParser.class.getModule().getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                try (InputStream inputStreamClient = EntryPoint.application.getClass().getModule().getResourceAsStream(path))
                {
                    if (inputStreamClient == null)
                    {
                        throw new NullPointerException("Obj file not found!");
                    }
                    else
                    {
                        return read(inputStreamClient);
                    }
                }
            }
            else
            {
                return read(inputStream);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        MAIN_LOGGER.error("Model at " + path + " could not be read!");
        return null;
    }

    @NotNull
    @Contract("_ -> new")
    private static Mesh read(InputStream file)
    {
        List<Float> inputPositions           = new ArrayList<>();
        List<Float> inputTextureCoordinates  = new ArrayList<>();
        List<Float> inputNormals             = new ArrayList<>();

        List<Float> vertices                 = new ArrayList<>();
        List<Integer> indices                = new ArrayList<>();

        Map<IndexSet, Integer> indexMap = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        try{
            while (true)
            {
                String line = reader.readLine();

                if (line == null) break;
                line = line.trim();
                String[] split = line.split("\\s+");

                switch (split[0])
                {
                    case "v":
                        inputPositions.add(Float.parseFloat(split[1]));
                        inputPositions.add(Float.parseFloat(split[2]));
                        inputPositions.add(Float.parseFloat(split[3]));
                        break;
                    case "vt":
                        inputTextureCoordinates.add(Float.parseFloat(split[1]));
                        inputTextureCoordinates.add(Float.parseFloat(split[2]));
                        break;
                    case "vn":
                        inputNormals.add(Float.parseFloat(split[1]));
                        inputNormals.add(Float.parseFloat(split[2]));
                        inputNormals.add(Float.parseFloat(split[3]));
                        break;
                    case "f":
                        String[] components = line.split("\\s+");
                        for (int comp = 1; comp < components.length; ++comp)
                        {
                            String[] inputIndexSet = components[comp].split("/");
                            IndexSet indexSet = new IndexSet(Integer.parseInt(inputIndexSet[0]), Integer.parseInt(inputIndexSet[1]), Integer.parseInt(inputIndexSet[2]));
                            insertVertex(vertices, indices, indexMap, inputPositions, inputTextureCoordinates, inputNormals, indexSet);
                        }
                        break;
                }
            }
            reader.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        VertexBufferLayout layout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT3),
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT3)
        );
        VertexBuffer vertexBuffer = new VertexBuffer(ArrayUtils.toPrimitiveArrayF(vertices), layout, false);
        VertexArray vertexArray = new VertexArray();
        vertexArray.setVertexBuffers(vertexBuffer);
        vertexArray.setIndexBuffer(ArrayUtils.toPrimitiveArrayI(indices));

        return new Mesh(vertexArray);
    }

    private static void insertVertex(List<Float> vertices, List<Integer> indices, @NotNull Map<IndexSet, Integer> indexMap, List<Float> inputPositions, List<Float> inputTextureCoordinates, List<Float> inputNormals, IndexSet indexSet)
    {
        if (indexMap.containsKey(indexSet))
        {
            indices.add(indexMap.get(indexSet));
        }
        else
        {
            indexMap.put(indexSet, vertices.size() / 3);
            indices.add(vertices.size() / 3);
            vertices.add(inputPositions.get(indexSet.position - 1));
            vertices.add(inputTextureCoordinates.get(indexSet.textureCoordinate - 1));
            vertices.add(inputNormals.get(indexSet.normal - 1));
        }
    }

    private static class IndexSet
    {
        private final int position, textureCoordinate, normal;

        private IndexSet(int position, int textureCoordinate, int normal)
        {
            this.position = position;
            this.textureCoordinate = textureCoordinate;
            this.normal = normal;
        }
    }
}