package engine.graphics;

import engine.main.EntryPoint;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static engine.main.Application.MAIN_LOGGER;

public class ObjParser
{
    @Nullable
    public static void parseObj(String path)
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
                        read(inputStreamClient);
                    }
                }
            }
            else
            {
                read(inputStream);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void read(InputStream file)
    {
        List<Float> vertices            = new ArrayList<>();
        List<Float> textureCoordinates  = new ArrayList<>();
        List<Float> normals             = new ArrayList<>();
        List<Integer> vertexIndices     = new ArrayList<>();
        List<Integer> textureIndices    = new ArrayList<>();
        List<Integer> normalIndices     = new ArrayList<>();

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
                        vertices.add(Float.parseFloat(split[1]));
                        vertices.add(Float.parseFloat(split[2]));
                        vertices.add(Float.parseFloat(split[3]));
                        break;
                    case "vt":
                        textureCoordinates.add(Float.parseFloat(split[1]));
                        textureCoordinates.add(Float.parseFloat(split[2]));
                        break;
                    case "vn":
                        normals.add(Float.parseFloat(split[1]));
                        normals.add(Float.parseFloat(split[2]));
                        normals.add(Float.parseFloat(split[3]));
                        break;
                    case "f":
                        String[] components = line.split("\\s+");
                        for (int comp = 1; comp < components.length; ++comp)
                        {
                            String[] indices = components[comp].split("/");
                            vertexIndices.add(Integer.parseInt(indices[0]));
                            textureIndices.add(Integer.parseInt(indices[1]));
                            normalIndices.add(Integer.parseInt(indices[2]));
                        }
                        System.out.println();
                        break;
                }
            }
            reader.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}