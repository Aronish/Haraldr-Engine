package engine.graphics;

import engine.debug.Logger;
import engine.main.ArrayUtils;
import engine.main.EntryPoint;
import engine.main.IOUtils;
import engine.math.Vector2f;
import engine.math.Vector3f;
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

public class ObjParser
{
    private static final int VERTEX_ELEMENT_COUNT = 11;

    @Nullable
    public static VertexArray loadMesh(String path)
    {
        if (ResourceManager.isMeshLoaded(path))
        {
            return ResourceManager.getLoadedMesh(path);
        }
        else
        {
            VertexArray mesh = IOUtils.readResource(path, ObjParser::loadMesh);
            if (EntryPoint.DEBUG) Logger.info("Loaded mesh " + path);
            ResourceManager.addMesh(path, mesh);
            return mesh;
        }
    }

    @NotNull
    @Contract("_ -> new")
    private static VertexArray loadMesh(InputStream file)
    {
        List<Vector3f> inputPositions           = new ArrayList<>();
        List<Vector2f> inputTextureCoordinates  = new ArrayList<>();
        List<Vector3f> inputNormals             = new ArrayList<>();

        List<Float> vertices                    = new ArrayList<>();
        List<Integer> indices                   = new ArrayList<>();

        Map<IndexSet, Integer> indexMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file)))
        {
            while (true)
            {
                String line = reader.readLine();
                if (line == null) break;
                line = line.trim();
                String[] split = line.split("\\s+");

                switch (split[0])
                {
                    case "v" -> inputPositions.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                    case "vt" -> inputTextureCoordinates.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2])));
                    case "vn" -> inputNormals.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                    case "f" -> {
                        String[] components = line.split("\\s+");
                        IndexSet[] indexSets = new IndexSet[3];
                        for (int comp = 1; comp < components.length; ++comp)
                        {
                            String[] inputIndexSet = components[comp].split("/");
                            indexSets[comp - 1] = new IndexSet(Integer.parseInt(inputIndexSet[0]), Integer.parseInt(inputIndexSet[1]), Integer.parseInt(inputIndexSet[2]));
                        }
                        Vector3f tangent = createTangent(indexSets, inputPositions, inputTextureCoordinates);
                        for (int comp = 1; comp < components.length; ++comp)
                        {
                            insertVertex(vertices, indices, indexMap, inputPositions, inputTextureCoordinates, inputNormals, tangent, indexSets[comp - 1]);
                        }
                    }
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        VertexBufferLayout layout = new VertexBufferLayout(
                new VertexBufferElement(ShaderDataType.FLOAT3), //Position
                new VertexBufferElement(ShaderDataType.FLOAT3), //Normal
                new VertexBufferElement(ShaderDataType.FLOAT2), //UV
                new VertexBufferElement(ShaderDataType.FLOAT3)  //Tangent
        );
        VertexBuffer vertexBuffer = new VertexBuffer(ArrayUtils.toPrimitiveArrayF(vertices), layout, VertexBuffer.Usage.STATIC_DRAW);
        VertexArray vertexArray = new VertexArray();
        vertexArray.setVertexBuffers(vertexBuffer);
        vertexArray.setIndexBufferData(ArrayUtils.toPrimitiveArrayI(indices));
        return vertexArray;
    }

    private static void insertVertex(List<Float> vertices,
                                     List<Integer> indices,
                                     @NotNull Map<IndexSet, Integer> indexMap,
                                     List<Vector3f> inputPositions,
                                     List<Vector2f> inputTextureCoordinates,
                                     List<Vector3f> inputNormals,
                                     Vector3f tangent,
                                     IndexSet indexSet)
    {
        if (indexMap.containsKey(indexSet))
        {
            indices.add(indexMap.get(indexSet));
        }
        else
        {
            indexMap.put(indexSet, vertices.size() / VERTEX_ELEMENT_COUNT);
            indices.add(vertices.size() / VERTEX_ELEMENT_COUNT);
            vertices.add(inputPositions.get(indexSet.position).getX());
            vertices.add(inputPositions.get(indexSet.position).getY());
            vertices.add(inputPositions.get(indexSet.position).getZ());
            vertices.add(inputNormals.get(indexSet.normal).getX());
            vertices.add(inputNormals.get(indexSet.normal).getY());
            vertices.add(inputNormals.get(indexSet.normal).getZ());
            vertices.add(inputTextureCoordinates.get(indexSet.textureCoordinate).getX());
            vertices.add(inputTextureCoordinates.get(indexSet.textureCoordinate).getY());
            vertices.add(tangent.getX());
            vertices.add(tangent.getY());
            vertices.add(tangent.getZ());
        }
    }

    private static @NotNull Vector3f createTangent(@NotNull IndexSet[] indexSets, @NotNull List<Vector3f> inputPositions, @NotNull List<Vector2f> inputTextureCoordinates)
    {
        Vector3f edge1 = Vector3f.subtract(inputPositions.get(indexSets[1].position), inputPositions.get(indexSets[0].position));
        Vector3f edge2 = Vector3f.subtract(inputPositions.get(indexSets[2].position), inputPositions.get(indexSets[0].position));
        Vector2f duv1 = Vector2f.subtract(inputTextureCoordinates.get(indexSets[1].textureCoordinate), inputTextureCoordinates.get(indexSets[0].textureCoordinate));
        Vector2f duv2 = Vector2f.subtract(inputTextureCoordinates.get(indexSets[2].textureCoordinate), inputTextureCoordinates.get(indexSets[0].textureCoordinate));
        float determinant = 1.0f / (duv1.getX() * duv2.getY() - duv2.getX() * duv1.getY());
        return Vector3f.normalize(new Vector3f(
                determinant * (duv2.getY() * edge1.getX() - duv1.getY() * edge2.getX()),
                determinant * (duv2.getY() * edge1.getY() - duv1.getY() * edge2.getY()),
                determinant * (duv2.getY() * edge1.getZ() - duv1.getY() * edge2.getZ())
        ));
    }

    private static class IndexSet
    {
        private final int position, textureCoordinate, normal;

        private IndexSet(int position, int textureCoordinate, int normal)
        {
            this.position = position - 1;
            this.textureCoordinate = textureCoordinate - 1;
            this.normal = normal - 1;
        }
    }
}