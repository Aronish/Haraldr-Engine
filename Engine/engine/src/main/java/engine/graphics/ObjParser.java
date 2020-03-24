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
    public static final int VERTEX_ELEMENT_COUNT = 11;

    @Nullable
    public static VertexArray loadMesh(String path)
    {
        return IOUtils.readResource(path, ObjParser::loadMesh);
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
                new VertexBufferElement(ShaderDataType.FLOAT3), //Tangent
                new VertexBufferElement(ShaderDataType.FLOAT2)  //UV
        );
        VertexBuffer vertexBuffer = new VertexBuffer(ArrayUtils.toPrimitiveArrayF(vertices), layout, false);
        VertexArray vertexArray = new VertexArray();
        vertexArray.setVertexBuffers(vertexBuffer);
        vertexArray.setIndexBuffer(ArrayUtils.toPrimitiveArrayI(indices));
        System.out.println();
        for (int i = 0; i < vertices.size(); ++i)
        {
            System.out.print(vertices.get(i) + " ");
            if ((i + 1) % 8 == 0) System.out.println();
        }
        System.out.println();
        return vertexArray;
    }

    @NotNull
    private static DiffuseMaterial loadMaterial(InputStream file)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        DiffuseMaterial material = new DiffuseMaterial();
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
            //Calculate Tangent
            if (vertices.size() % (VERTEX_ELEMENT_COUNT * 3) == 0)
            {
                Vector3f pos1, pos2, pos3;
                if (vertices.size() <= 33)
                {
                    pos1 = new Vector3f(vertices.get(vertices.size() - (8 + 2 * VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (7 + 2 * VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (6 + 2 * VERTEX_ELEMENT_COUNT)));
                    pos2 = new Vector3f(vertices.get(vertices.size() - (8 + VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (7 + VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (6 + VERTEX_ELEMENT_COUNT)));
                    pos3 = new Vector3f(vertices.get(vertices.size() - 8), vertices.get(vertices.size() - 7), vertices.get(vertices.size() - 6));
                }else
                {
                    pos1 = new Vector3f(vertices.get(vertices.size() - (11 + 2 * VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (10 + 2 * VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (9 + 2 * VERTEX_ELEMENT_COUNT)));
                    pos2 = new Vector3f(vertices.get(vertices.size() - (11 + VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (10 + VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (9 + VERTEX_ELEMENT_COUNT)));
                    pos3 = new Vector3f(vertices.get(vertices.size() - 11), vertices.get(vertices.size() - 10), vertices.get(vertices.size() - 9));
                }
                Vector2f uv1 = new Vector2f(vertices.get(vertices.size() - (2 + 2 * VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (1 + 2 * VERTEX_ELEMENT_COUNT)));
                Vector2f uv2 = new Vector2f(vertices.get(vertices.size() - (2 + VERTEX_ELEMENT_COUNT)), vertices.get(vertices.size() - (1 + VERTEX_ELEMENT_COUNT)));
                Vector2f uv3 = new Vector2f(vertices.get(vertices.size() - 2), vertices.get(vertices.size() - 1));
                Vector3f edge1 = Vector3f.subtract(pos2, pos1);
                Vector3f edge2 = Vector3f.subtract(pos3, pos1);
                Vector2f dUv1 = Vector2f.subtract(uv2, uv1);
                Vector2f dUv2 = Vector2f.subtract(uv3, uv1);
                float determinant = 1.0f / (dUv1.getX() * dUv2.getY() - dUv2.getX() * dUv1.getY());
                Vector3f tangent = new Vector3f(
                        determinant * (dUv2.getY() * edge1.getX() - dUv1.getY() * edge2.getX()),
                        determinant * (dUv2.getY() * edge1.getY() - dUv1.getY() * edge2.getY()),
                        determinant * (dUv2.getY() * edge1.getZ() - dUv1.getY() * edge2.getZ())
                );

                if (vertices.size() <= 33)
                {
                    vertices.add(vertices.size() - 8, tangent.getX());
                    vertices.add(vertices.size() - 7, tangent.getY());
                    vertices.add(vertices.size() - 6, tangent.getZ());

                    vertices.add(vertices.size() - 19, tangent.getX());
                    vertices.add(vertices.size() - 18, tangent.getY());
                    vertices.add(vertices.size() - 17, tangent.getZ());

                    vertices.add(vertices.size() - 30, tangent.getX());
                    vertices.add(vertices.size() - 29, tangent.getY());
                    vertices.add(vertices.size() - 28, tangent.getZ());
                }else
                {
                    vertices.add(vertices.size() - 11, tangent.getX());
                    vertices.add(vertices.size() - 10, tangent.getY());
                    vertices.add(vertices.size() - 9, tangent.getZ());

                    vertices.add(vertices.size() - 22, tangent.getX());
                    vertices.add(vertices.size() - 21, tangent.getY());
                    vertices.add(vertices.size() - 20, tangent.getZ());

                    vertices.add(vertices.size() - 33, tangent.getX());
                    vertices.add(vertices.size() - 32, tangent.getY());
                    vertices.add(vertices.size() - 31, tangent.getZ());
                }

                tangent.normalize();
                tangent.print();
            }
        }
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