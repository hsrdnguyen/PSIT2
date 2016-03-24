package ch.avocado.share.service.Mock;

/**
 * Created by coffeemakr on 23.03.16.
 */

import ch.avocado.share.model.data.AccessControlObjectBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class DataHandlerMockBase<E extends AccessControlObjectBase>{
    protected List<E> objects;

    public DataHandlerMockBase() {
        objects = new ArrayList<>();
    }

    protected E get(String id) {
        if(id == null) throw new IllegalArgumentException("id is null");
        for (E object : objects) {
            if(object.getId().equals(id)) {
                return object;
            }
        }
        return null;
    }

    protected String add(E object) {
        if(object == null) throw new IllegalArgumentException("object is null");
        if(object.getId() != null) {
            throw new RuntimeException("can't add object with non-null id");
        }
        Random random = new Random();
        object.setId("object" + random.nextLong() + random.nextLong());
        objects.add(object);
        return object.getId();
    }

    protected boolean delete(E object) {
        Iterator<E> groupIterator = objects.iterator();
        while (groupIterator.hasNext()) {
            E currentGroup = groupIterator.next();
            if (currentGroup.getId().equals(object.getId())) {
                groupIterator.remove();
                return true;
            }
        }
        return false;
    }

    protected boolean update(E object) {
        if(object == null) throw new IllegalArgumentException("object is null");
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getId().equals(object.getId())) {
                objects.set(i, object);
                return true;
            }
        }
        return false;
    }

}
