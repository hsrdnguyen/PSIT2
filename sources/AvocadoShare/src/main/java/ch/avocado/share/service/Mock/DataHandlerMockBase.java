package ch.avocado.share.service.Mock;



import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Base class for MockDataHandler
 * @param <E> The class to handle
 */
public abstract class DataHandlerMockBase<E extends AccessControlObjectBase>{
    protected final Map<String, E> objects;


    public DataHandlerMockBase() {
        objects = new HashMap<>();
    }

    protected E get(String id) {
        if(id == null) throw new NullPointerException("id is null");
        return objects.get(id);
    }

    protected String add(E object) {
        if(object == null) throw new NullPointerException("object is null");
        if(object.getId() != null) throw new IllegalArgumentException("object.getId() is not null");
        Random random = new Random();
        object.setId("" + random.nextLong() + random.nextLong());
        objects.put(object.getId(), object);
        return object.getId();
    }

    protected void delete(E object) throws ObjectNotFoundException {
        if(objects.containsKey(object.getId())) {
            objects.remove(object.getId());
        } else {
            throw new ObjectNotFoundException(object.getClass(), object.getId());
        }
    }

    protected void update(E object) throws ObjectNotFoundException {
        if(object == null) throw new NullPointerException("object is null");
        if(objects.containsKey(object.getId())) {
            objects.put(object.getId(), object);
        } else {
            throw new ObjectNotFoundException(object.getClass(), object.getId());
        }
    }

    protected E[] getAll(Class<E> type) {
        E[] objectArray = (E[]) Array.newInstance(type, objects.size());
        return objects.values().toArray(objectArray);
    }
}
