package editor;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlSequence;
import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.ComponentVisitor;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.TagComponent;
import haraldr.ecs.TransformComponent;
import org.jetbrains.annotations.Contract;

public class ComponentSerializer implements ComponentVisitor
{
    private YamlMappingBuilder componentDataStore;

    @Contract(pure = true)
    public ComponentSerializer(YamlMappingBuilder componentDataStore)
    {
        this.componentDataStore = componentDataStore;
    }

    public YamlMappingBuilder getComponentDataStore()
    {
        return componentDataStore;
    }

    @Override
    public void visit(BoundingSphereComponent boundingSphereComponent)
    {
        componentDataStore = componentDataStore.add("radius", Float.toString(boundingSphereComponent.radius));
    }

    @Override
    public void visit(ModelComponent modelComponent)
    {
        componentDataStore = componentDataStore.add("model_path", modelComponent.model.getPath());
    }

    @Override
    public void visit(TagComponent tagComponent)
    {
        componentDataStore = componentDataStore.add("tag", tagComponent.tag);
    }

    @Override
    public void visit(TransformComponent transformComponent)
    {
        YamlSequence position = Yaml.createYamlSequenceBuilder()
                .add(Float.toString(transformComponent.position.getX()))
                .add(Float.toString(transformComponent.position.getY()))
                .add(Float.toString(transformComponent.position.getZ()))
        .build();
        YamlSequence scale = Yaml.createYamlSequenceBuilder()
                .add(Float.toString(transformComponent.scale.getX()))
                .add(Float.toString(transformComponent.scale.getY()))
                .add(Float.toString(transformComponent.scale.getZ()))
        .build();
        YamlSequence rotation = Yaml.createYamlSequenceBuilder()
                .add(Float.toString(transformComponent.rotation.getX()))
                .add(Float.toString(transformComponent.rotation.getY()))
                .add(Float.toString(transformComponent.rotation.getZ()))
                .build();
        componentDataStore = componentDataStore.add("position", position);
        componentDataStore = componentDataStore.add("scale", scale);
        componentDataStore = componentDataStore.add("rotation", rotation);
    }
}
