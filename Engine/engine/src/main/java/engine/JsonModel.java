package engine;

import engine.graphics.CubeMap;
import engine.graphics.ResourceManager;
import engine.graphics.Texture;
import engine.graphics.VertexArray;
import engine.main.IOUtils;
import engine.math.Matrix4f;
import main.JSONObject;
import org.jetbrains.annotations.NotNull;

public class JsonModel
{
    private VertexArray mesh;
    private JsonMaterial material;
    private Matrix4f transformationMatrix;

    public JsonModel(String path)
    {
        this(path, Matrix4f.IDENTITY);
    }

    public JsonModel(String path, Matrix4f transformationMatrix)
    {
        String source = IOUtils.readResource(path, IOUtils::resourceToString);
        JSONObject modelDefinition = new JSONObject(source);
        mesh = ResourceManager.getMesh(modelDefinition.getString("mesh"));

        material = JsonMaterial.parseJsonMaterial(modelDefinition.getJSONObject("material"));

        this.transformationMatrix = transformationMatrix;
    }

    public void render(@NotNull CubeMap diffIrr, @NotNull CubeMap pref)
    {
        material.bind();
        material.getShader().setMatrix4f("model", transformationMatrix);
        diffIrr.bind(5);
        pref.bind(6);
        Texture.BRDF_LUT.bind(7);
        mesh.bind();
        mesh.drawElements();
    }

    public void delete()
    {
        mesh.delete();
    }
}
