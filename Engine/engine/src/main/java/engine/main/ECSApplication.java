package engine.main;

import engine.component.MeshComponent;
import engine.component.TransformComponent;
import engine.event.Event;
import engine.event.EventDispatcher;
import engine.event.EventType;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.event.WindowFocusEvent;
import engine.event.WindowResizedEvent;
import engine.graphics.CubeMap;
import engine.graphics.Renderer3D;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.material.DiffuseMaterial;
import engine.graphics.material.PBRMaterial;
import engine.input.Input;
import engine.input.Key;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.scenegraph.Scene;
import engine.scenegraph.SceneObject;
import engine.system.RenderSystem;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public abstract class ECSApplication extends Application
{
    private RenderSystem renderSystem = new RenderSystem();
    private Scene scene = new Scene();
    private TransformComponent transformComponent;
    private CubeMap envMap;

    @Override
    protected void init(Window.WindowProperties windowProperties)
    {
        /////WINDOW///////////////////////////////////////////////////////
        window = new Window(windowProperties);
        initWidth = window.getInitWidth();
        initHeight = window.getInitHeight();
        /////OPENGL CODE WON'T WORK BEFORE THIS///////////////////////////
        EventDispatcher.addCallback(new EventCallback());
        Matrix4f.init(window.getWidth(), window.getHeight());

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (EntryPoint.DEBUG)
        {
            glEnable(GL_DEBUG_OUTPUT);
            glDebugMessageCallback((source, type, id, severity, length, message, userparam) ->
            {
                if (severity != GL_DEBUG_SEVERITY_NOTIFICATION)
                {
                    System.out.println();
                    MAIN_LOGGER.info(String.format("[OPENGL] (Source: %s, Type: %s, Severity: %s):", Integer.toHexString(source), Integer.toHexString(type), Integer.toHexString(severity)));
                    MAIN_LOGGER.info(memUTF8(message) + "\n");
                }
                //if (type == GL_DEBUG_TYPE_ERROR || type == GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR) stop(new WindowClosedEvent());
            }, 0);
        }

        envMap = CubeMap.createEnvironmentMap("default_hdris/TexturesCom_NorwayForest_4K_hdri_sphere.hdr");
        MeshComponent meshComponent = new MeshComponent("models/cube.obj",
                new PBRMaterial(
                        "default_textures/Tiles_Glass_1K_albedo.png",
                        "default_textures/Tiles_Glass_1K_normal.png",
                        "default_textures/Tiles_Glass_1K_metallic.png",
                        "default_textures/Tiles_Glass_1K_roughness.png",
                        envMap
                ));
        transformComponent = new TransformComponent().setRotation(new Vector3f(1f, 0f, 0f), 35f);

        SceneObject obj1 = new SceneObject(scene);
        obj1.setMeshComponent(renderSystem, meshComponent);
        obj1.setTransformComponent(renderSystem, transformComponent);

        SceneObject obj2 = new SceneObject(scene, obj1);
        obj2.setMeshComponent(renderSystem, new MeshComponent("models/sphere.obj", new DiffuseMaterial("default_textures/brickwall.jpg")));
        obj2.setTransformComponent(renderSystem, new TransformComponent().setPosition(new Vector3f(0f, 0.3f, 0f)));

        PointLight pl = new PointLight(new Vector3f(3f), new Vector3f(1f));
        SceneLights sl = new SceneLights();
        sl.addLight(pl);
        Renderer3D.setSceneLights(sl);

        glfwShowWindow(window.getWindowHandle());
        initialized = true;
    }

    public class EventCallback implements engine.event.EventCallback
    {
        @Override
        public void onEvent(@NotNull Event event, Window window)
        {
            if (event.eventType == EventType.WINDOW_CLOSED) stop(event);
            if (event.eventType == EventType.WINDOW_RESIZED) Matrix4f.onResize((WindowResizedEvent) event);
            if (event.eventType == EventType.KEY_PRESSED)
            {
                if (Input.isKeyPressed(window, Key.KEY_ESCAPE)) stop(event);
                if (Input.isKeyPressed(window, Key.KEY_F)) window.changeFullscreen();
                if (Input.isKeyPressed(window, Key.KEY_E))
                {
                    window.setFocus(!window.isFocused());
                    Renderer3D.getCamera().onFocus(new WindowFocusEvent(window.isFocused()));
                }
            }
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                Renderer3D.getCamera().handleRotation((MouseMovedEvent) event);
            }
            if (event.eventType == EventType.MOUSE_SCROLLED)
            {
                Renderer3D.getCamera().handleScroll((MouseScrolledEvent) event);
            }
            if (event.eventType == EventType.WINDOW_FOCUS)
            {
                Renderer3D.getCamera().onFocus((WindowFocusEvent) event);
            }
        }
    }

    private float rotation;

    @Override
    protected void update(float deltaTime)
    {
        time = glfwGetTime();
        rotation += 10f * deltaTime;
        if (window.isFocused())
        {
            Renderer3D.getCamera().handleMovement(window, deltaTime);
            if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
            if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
        }
        transformComponent.setRotation(new Vector3f(1f, 0f, 0f), rotation);
        glfwPollEvents();
    }

    @Override
    protected void render()
    {
        Renderer3D.begin(window);
        renderSystem.render();
        envMap.renderSkyBox();
        Renderer3D.end(window);
        glfwSwapBuffers(window.getWindowHandle());
    }
}
