package haraldr.ecs;

import haraldr.debug.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRegistry
{
    private static int entityCount;

    private Map<Class<?>, ComponentStorage<?>> registeredComponents = new HashMap<>();
    private List<Integer> activeEntities = new ArrayList<>();
    private List<Integer> freeIds = new ArrayList<>();

    public EntityRegistry()
    {
        registerComponent(ModelComponent.class);
        registerComponent(TransformComponent.class);
    }

    public Entity createEntity()
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

    public <T> void registerComponent(Class<T> componentType)
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
    public <T> View<T> view(Class<T> componentType)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) registeredComponents.get(componentType);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return new View<>(storage.getComponents());
    }

    @SuppressWarnings("unchecked")
    public <A, B> Group<A, B> group(Class<A> firstType, Class<B> secondType)
    {
        ComponentStorage<A> firstStorage = (ComponentStorage<A>) registeredComponents.get(firstType);
        ComponentStorage<B> secondStorage = (ComponentStorage<B>) registeredComponents.get(secondType);
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
                group.add(firstStorage.get(entity), secondStorage.get(entity));
            }
        }
        return group;
    }
}
