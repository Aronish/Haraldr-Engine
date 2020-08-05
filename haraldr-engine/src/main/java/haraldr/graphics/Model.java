package haraldr.graphics;

import haraldr.debug.Logger;
import haraldr.ecs.TransformComponent;
import haraldr.main.IOUtils;
import haraldr.math.Matrix4f;
import jsonparser.JSONException;
import jsonparser.JSONObject;

public class Model
{
    private String path;
    private VertexArray mesh;
    private Material material;

    public Model(String path)
    {
        this.path = path;
        refresh();
    }

    public void refresh()
    {
        JSONObject modelDefinition = new JSONObject(IOUtils.readResource(path, IOUtils::resourceToString));
        if (material != null)
        {
            material.unbind();
        }
        try
        {
            mesh = ResourceManager.getMesh(modelDefinition.getString("mesh"));
            material = Material.create(modelDefinition.getJSONObject("material"));
        } catch (JSONException e)
        {
            mesh = ResourceManager.getMesh("default_meshes/plane.obj");
            material = Material.create(new JSONObject(IOUtils.readResource("default_models/error.json", IOUtils::resourceToString)));
            Logger.error(e.getMessage());
        }
    }

    public void render(TransformComponent transform)
    {
        material.bind();
        material.getShader().setMatrix4f("model", Matrix4f.identity().translate(transform.position).scale(transform.scale));
        mesh.bind();
        mesh.drawElements();
    }
}
