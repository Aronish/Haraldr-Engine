package engine.graphics;

import engine.debug.Logger;
import engine.main.IOUtils;
import engine.math.Matrix4f;
import jsonparser.JSONException;
import jsonparser.JSONObject;

public class JsonModel
{
    private String path;
    private VertexArray mesh;
    private JsonMaterial material;
    private Matrix4f transformationMatrix;

    public JsonModel(String path)
    {
        this(path, Matrix4f.IDENTITY);
    }

    public JsonModel(String path, Matrix4f transformationMatrix)
    {
        this.path = path;
        this.transformationMatrix = transformationMatrix;
        refresh();
    }

    public void refresh()
    {
        JSONObject modelDefinition = new JSONObject(IOUtils.readResource(path, IOUtils::resourceToString));
        try
        {
            mesh = ResourceManager.getMesh(modelDefinition.getString("mesh"));
            material = JsonMaterial.create(modelDefinition.getJSONObject("material"));
        } catch (JSONException e)
        {
            mesh = ResourceManager.getMesh("default_meshes/plane.obj");
            material = JsonMaterial.create(new JSONObject(IOUtils.readResource("default_models/error.json", IOUtils::resourceToString)));
            Logger.error(e.getMessage());
        }
    }

    public void render()
    {
        material.bind();
        material.getShader().setMatrix4f("model", transformationMatrix);
        mesh.bind();
        mesh.drawElements();
    }

    public void delete()
    {
        mesh.delete();
    }
}
