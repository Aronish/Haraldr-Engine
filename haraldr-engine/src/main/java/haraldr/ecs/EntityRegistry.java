package haraldr.ecs;

import haraldr.debug.Logger;
import haraldr.math.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class EntityRegistry
{
    private static int entityCount;

    private Map<Class<? extends Component>, ComponentStorage<?>> registeredComponents = new HashMap<>();
    private List<Integer> activeEntities = new ArrayList<>();
    private List<Integer> freeIds = new ArrayList<>();

    public EntityRegistry()
    {
        registerComponent(TagComponent.class);
        registerComponent(ModelComponent.class);
        registerComponent(TransformComponent.class);
        registerComponent(BoundingSphereComponent.class);
    }

    public Entity createEntity(Vector3f position, Vector3f scale, Vector3f rotation)
    {
        Entity entity;
        if (freeIds.size() > 0)
        {
            entity = new Entity(freeIds.remove(freeIds.size() - 1));
        } else
        {
            entity = new Entity(entityCount++);
        }
        activeEntities.add(entity.id);
        addComponent(new TransformComponent(position, scale, rotation), entity);
        return entity;
    }

    public void destroyEntity(Entity entity)
    {
        activeEntities.remove(entity.id);
        for (ComponentStorage<?> storage : registeredComponents.values())
        {
            storage.remove(entity);
        }
        freeIds.add(entity.id);
    }

    public void registerComponent(Class<? extends Component> componentType)
    {
        registeredComponents.putIfAbsent(componentType, new ComponentStorage<>());
    }

    @SuppressWarnings("unchecked")
    public <T> void addComponent(T component, Entity entity)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) registeredComponents.get(component.getClass());
        if (storage == null)
        {
            Logger.error("Component of type " + component.getClass().getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        storage.add(component, entity);
    }

    @SuppressWarnings("unchecked")
    public <T> boolean hasComponent(Class<T> componentType, Entity entity)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) registeredComponents.get(componentType);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return storage.getEntities().contains(entity.id);
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> componentType, Entity entity)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) registeredComponents.get(componentType);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return storage.get(entity.id);
    }

    @SuppressWarnings("unchecked")
    public <T> ComponentStorage<T> getStorage(Class<T> componentType)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) registeredComponents.get(componentType);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return storage;
    }

    @SuppressWarnings("unchecked")
    public <T> Entity getEntityOf(T component)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) registeredComponents.get(component.getClass());
        if (storage == null)
        {
            Logger.error("Component of type " + component.getClass().getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        int entityKey = storage.getEntityKey(component);
        return entityKey == -1 ? Entity.INVALID : new Entity(entityKey);
    }

    @SuppressWarnings("unchecked")
    public <T> View<T> view(Class<T> componentType)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) registeredComponents.get(componentType);
        ComponentStorage<TransformComponent> transforms = (ComponentStorage<TransformComponent>) registeredComponents.get(TransformComponent.class);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return new View<>(transforms.getComponents(), storage.getComponents());
    }

    @SuppressWarnings("unchecked")
    public <A, B> Group<A, B> group(Class<A> firstType, Class<B> secondType)
    {
        ComponentStorage<A> firstStorage = (ComponentStorage<A>) registeredComponents.get(firstType);
        ComponentStorage<B> secondStorage = (ComponentStorage<B>) registeredComponents.get(secondType);
        ComponentStorage<TransformComponent> transforms = (ComponentStorage<TransformComponent>) registeredComponents.get(TransformComponent.class);
        if (firstStorage == null || secondStorage == null)
        {
            Logger.error("Component of type " + firstType.getName() + " or " + secondType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        List<Integer> firstEntities = firstStorage.getEntities();
        List<Integer> secondEntities = secondStorage.getEntities();

        Group<A, B> group = new Group<>();
        for (int entity : activeEntities)
        {
            if (firstEntities.contains(entity) && secondEntities.contains(entity))
            {
                group.add(transforms.get(entity), firstStorage.get(entity), secondStorage.get(entity));
            }
        }
        return group;
    }

    public Set<Class<? extends Component>> getRegisteredComponentTypes()
    {
        return registeredComponents.keySet();
    }
}
