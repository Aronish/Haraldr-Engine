package haraldr.ecs;

import haraldr.debug.Logger;
import haraldr.math.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class EntityRegistry
{
    public static final List<Class<? extends Component>> registeredComponents = new ArrayList<>();

    private Map<Class<? extends Component>, ComponentStorage<? extends Component>> activeComponents = new HashMap<>();
    private List<Integer> activeEntities = new ArrayList<>();
    private List<Integer> freeIds = new ArrayList<>();
    private int entityCount;

    static
    {
        registeredComponents.add(TagComponent.class);
        registeredComponents.add(ModelComponent.class);
        registeredComponents.add(TransformComponent.class);
        registeredComponents.add(BoundingSphereComponent.class);
    }

    public static Class<? extends Component> getRegisteredComponentByName(String simpleName)
    {
        for (Class<? extends Component> registeredComponent : registeredComponents)
        {
            if (registeredComponent.getSimpleName().equals(simpleName)) return registeredComponent;
        }
        return null;
    }

    public EntityRegistry()
    {
        for (Class<? extends Component> registeredComponent : registeredComponents)
        {
            activeComponents.putIfAbsent(registeredComponent, new ComponentStorage<>());
        }
    }

    public void addEntity(Entity entity)
    {
        if (activeEntities.contains(entity.id))
        {
            Logger.error("Duplicated entity ids!");
        } else
        {
            if (freeIds.contains(entity.id))
            {
                freeIds.remove(entity.id);
            }
            activeEntities.add(entity.id);
        }
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
        for (ComponentStorage<?> storage : activeComponents.values())
        {
            storage.remove(entity);
        }
        freeIds.add(entity.id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> void addComponent(T component, Entity entity)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) activeComponents.get(component.getClass());
        if (storage == null)
        {
            Logger.error("Component of type " + component.getClass().getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        storage.add(component, entity);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> boolean hasComponent(Class<T> componentType, Entity entity)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) activeComponents.get(componentType);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return storage.getEntities().contains(entity.id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentType, Entity entity)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) activeComponents.get(componentType);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return storage.get(entity.id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> ComponentStorage<T> getStorage(Class<T> componentType)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) activeComponents.get(componentType);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return storage;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> Entity getEntityOf(T component)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) activeComponents.get(component.getClass());
        if (storage == null)
        {
            Logger.error("Component of type " + component.getClass().getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        int entityKey = storage.getEntityKey(component);
        return entityKey == -1 ? Entity.INVALID : new Entity(entityKey);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> View<T> view(Class<T> componentType)
    {
        ComponentStorage<T> storage = (ComponentStorage<T>) activeComponents.get(componentType);
        ComponentStorage<TransformComponent> transforms = (ComponentStorage<TransformComponent>) activeComponents.get(TransformComponent.class);
        if (storage == null)
        {
            Logger.error("Component of type " + componentType.getName() + " has not been registered!");
            throw new IllegalStateException();
        }
        return new View<>(transforms.getComponents(), storage.getComponents());
    }

    @SuppressWarnings("unchecked")
    public <A extends Component, B extends Component> Group<A, B> group(Class<A> firstType, Class<B> secondType)
    {
        ComponentStorage<A> firstStorage = (ComponentStorage<A>) activeComponents.get(firstType);
        ComponentStorage<B> secondStorage = (ComponentStorage<B>) activeComponents.get(secondType);
        ComponentStorage<TransformComponent> transforms = (ComponentStorage<TransformComponent>) activeComponents.get(TransformComponent.class);
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
        return activeComponents.keySet();
    }

    public List<Integer> getActiveEntities()
    {
        return activeEntities;
    }
}
