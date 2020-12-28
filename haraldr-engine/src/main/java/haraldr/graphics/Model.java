package haraldr.graphics;

import haraldr.debug.Logger;
import haraldr.ecs.TransformComponent;
import haraldr.main.IOUtils;
import haraldr.math.Matrix4f;
import jsonparser.JSONException;
import jsonparser.JSONObject;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_NOTEQUAL;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilMask;

public class Model
{
    private static final Shader OUTLINE_SHADER = Shader.create("default_shaders/outline.glsl");

    private String path;
    private JSONObject modelDefinition;
    private VertexArray mesh;
    private Material material;
    private boolean outlined;

    public Model(String path)
    {
        this.path = path;
        refresh();
    }

    public void setOutlined(boolean outlined)
    {
        this.outlined = outlined;
    }

    public void toggleOutline()
    {
        outlined = !outlined;
    }

    public boolean isOutlined()
    {
        return outlined;
    }

    public void refresh()
    {
        refresh(new JSONObject(IOUtils.readResource(path, IOUtils::resourceToString)));
    }

    public void refresh(JSONObject modelDefinition)
    {
        this.modelDefinition = modelDefinition;
        if (material != null) material.unbind();
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
        Matrix4f transformationMatrix = Matrix4f.identity().translate(transform.position).rotate(transform.rotationQuaternion).scale(transform.scale);
        if (outlined) //TODO: Not the best
        {
            glEnable(GL_STENCIL_TEST);
            glStencilFunc(GL_ALWAYS, 1, 0xFF);
            glStencilMask(0xFF);
            material.bind();
            material.getShader().setMatrix4f("model", transformationMatrix);
            mesh.bind();
            mesh.drawElements();

            glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
            glStencilMask(0x00);
            OUTLINE_SHADER.bind();
            OUTLINE_SHADER.setMatrix4f("model", transformationMatrix);
            OUTLINE_SHADER.setFloat("u_Outline_Size", 0.02f);
            OUTLINE_SHADER.setBoolean("u_Expand_Normals", true);
            mesh.drawElements();
            glStencilFunc(GL_ALWAYS, 1, 0xFF);
            glStencilMask(0xFF);
        } else
        {
            glDisable(GL_STENCIL_TEST);
            material.bind();
            material.getShader().setMatrix4f("model", transformationMatrix);
            mesh.bind();
            mesh.drawElements();
        }
    }

    public JSONObject getModelDefinition()
    {
        return modelDefinition;
    }
}
