package engine.graphics;

import engine.graphics.material.Material;
import engine.graphics.material.PBRMaterial;
import engine.main.IOUtils;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import main.JSONObject;

@SuppressWarnings("unused")
public class Model
{
    private String path;
    private VertexArray mesh;
    private Material material;
    private Matrix4f transformationMatrix;

    public Model(String modelPath, Material material)
    {
        this(ResourceManager.getMesh(modelPath), material, Matrix4f.IDENTITY);
    }

    public Model(String modelPath, Material material, Matrix4f transformationMatrix)
    {
        this(ResourceManager.getMesh(modelPath), material, transformationMatrix);
    }

    private Model(VertexArray mesh, Material material, Matrix4f transformationMatrix)
    {
        this.mesh = mesh;
        this.material = material;
        this.transformationMatrix = transformationMatrix;
    }

    public Model(String path)
    {
        this.path = path;
        load(path);
    }

    public void reload()
    {
        load(path);
    }

    private void load(String path)
    {
        JSONObject modelSource = new JSONObject(IOUtils.readResource(path, IOUtils::resourceToString));
        mesh = ResourceManager.getMesh(modelSource.getString("model"));

        JSONObject materialProperties = modelSource.getJSONObject("material").getJSONObject("properties");
        switch (modelSource.getJSONObject("material").getString("type"))
        {
            case "PBR_UNTEXTURED" ->
                    {
                        Vector3f albedo = new Vector3f(materialProperties.getJSONArray("albedo"));
                        float metalness = (float) materialProperties.getDouble("metalness");
                        float roughness = (float) materialProperties.getDouble("roughness");
                        CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                        material = new PBRMaterial(albedo, metalness, roughness, environmentMap);
                    }
            case "PBR_TEXTURED" ->
                    {
                        Texture albedo = ResourceManager.getTexture(materialProperties.getString("albedo"), true);
                        Texture normal = ResourceManager.getTexture(materialProperties.getString("normal"), true);
                        Texture metalness = ResourceManager.getTexture(materialProperties.getString("metalness"), true);
                        Texture roughness = ResourceManager.getTexture(materialProperties.getString("roughness"), true);
                        CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                        material = new PBRMaterial(albedo, normal, metalness, roughness, environmentMap);
                    }
        }
        transformationMatrix = Matrix4f.createRotate(Vector3f.UP, 180f).scale(new Vector3f(0.8f));
    }

    public void setTransformationMatrix(Matrix4f transformationMatrix)
    {
        this.transformationMatrix = transformationMatrix;
    }

    public void render()
    {
        material.bind();
        material.getShader().setMatrix4f("model", transformationMatrix);
        material.getShader().setVector3f("u_ViewPosition_W", Renderer3D.getCamera().getPosition());
        mesh.bind();
        mesh.drawElements();
    }

    public void renderTransformed(Matrix4f transformationMatrix)
    {
        material.bind();
        material.getShader().setMatrix4f("model", transformationMatrix);
        material.getShader().setVector3f("u_ViewPosition_W", Renderer3D.getCamera().getPosition());
        mesh.bind();
        mesh.drawElements();
    }

    public void renderNoMaterial()
    {
        mesh.bind();
        mesh.drawElements();
    }

    public VertexArray getMesh()
    {
        return mesh;
    }

    public Material getMaterial()
    {
        return material;
    }

    public Matrix4f getTransformationMatrix()
    {
        return transformationMatrix;
    }

    public void delete()
    {
        mesh.delete();
    }
}