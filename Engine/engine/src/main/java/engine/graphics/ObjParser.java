package engine.graphics;

import engine.main.ArrayUtils;
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
    @Nullable
    public static Mesh load(String path)
    {
        return IOUtils.readResource(path, ObjParser::load);
    }

    @NotNull
    @Contract("_ -> new")
    private static Mesh load(InputStream file)
    {
        List<Vector3f> inputPositions           = new ArrayList<>();
        List<Vector2f> inputTextureCoordinates  = new ArrayList<>();
        List<Vector3f> inputNormals             = new ArrayList<>();

        List<Float> vertices                    = new ArrayList<>();
        List<Integer> indices                   = new ArrayList<>();
        Material material = null;

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
                    case "mtllib":
                        material = IOUtils.readResource("models/" + split[1], ObjParser::loadMaterial);
                        break;
                    case "v":
                        inputPositions.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                        break;
                    case "vt":
                        inputTextureCoordinates.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2])));
                        break;
                    case "vn":
                        inputNormals.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
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
                new VertexBufferElement(ShaderDataType.FLOAT3), //Position
                new VertexBufferElement(ShaderDataType.FLOAT3), //Normal
                new VertexBufferElement(ShaderDataType.FLOAT2)  //UV
        );
        VertexBuffer vertexBuffer = new VertexBuffer(ArrayUtils.toPrimitiveArrayF(vertices), layout, false);
        VertexArray vertexArray = new VertexArray();
        vertexArray.setVertexBuffers(vertexBuffer);
        vertexArray.setIndexBuffer(ArrayUtils.toPrimitiveArrayI(indices));

        Mesh mesh = new Mesh(vertexArray);
        if (material != null)
        {
            mesh.setMaterial(material);
        }

        return mesh;
    }

    @NotNull
    private static Material loadMaterial(InputStream file)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        Material material = new Material();
        try{
            while (true)
            {
                String line = reader.readLine();
                if (line == null) break;
                line = line.trim();
                String[] split = line.split("\\s+");

                switch (split[0])
                {
                    case "Ns":
                        material.setSpecularExponent(Float.parseFloat(split[1]));
                        break;
                    case "Ka":
                        material.setAmbient(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                        break;
                    case "Kd":
                        material.setDiffuse(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                        break;
                    case "Ks":
                        material.setSpecular(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                        break;
                    case "d":
                        material.setOpacity(Float.parseFloat(split[1]));
                        break;
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return material;
    }

    private static void insertVertex(List<Float> vertices, List<Integer> indices, @NotNull Map<IndexSet, Integer> indexMap, List<Vector3f> inputPositions, List<Vector2f> inputTextureCoordinates, List<Vector3f> inputNormals, IndexSet indexSet)
    {
        if (indexMap.containsKey(indexSet))
        {
            indices.add(indexMap.get(indexSet));
        }
        else
        {
            indexMap.put(indexSet, vertices.size() / 8);
            indices.add(vertices.size() / 8);
            vertices.add(inputPositions.get(indexSet.position - 1).getX());
            vertices.add(inputPositions.get(indexSet.position - 1).getY());
            vertices.add(inputPositions.get(indexSet.position - 1).getZ());
            vertices.add(inputNormals.get(indexSet.normal - 1).getX());
            vertices.add(inputNormals.get(indexSet.normal - 1).getY());
            vertices.add(inputNormals.get(indexSet.normal - 1).getZ());
            vertices.add(inputTextureCoordinates.get(indexSet.textureCoordinate - 1).getX());
            vertices.add(inputTextureCoordinates.get(indexSet.textureCoordinate - 1).getY());
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