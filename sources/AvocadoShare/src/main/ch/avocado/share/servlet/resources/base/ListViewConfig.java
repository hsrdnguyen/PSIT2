package ch.avocado.share.servlet.resources.base;

import ch.avocado.share.model.data.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by coffeemakr on 14.04.16.
 */
class ListViewConfig extends ViewConfig {

    private final List<Model> objects;

    public ListViewConfig(View view, HttpServletRequest request, HttpServletResponse response, List<Model> objects) {
        super(view, request, response);
        this.objects = objects;
    }

    public List<Model> getObjects() {
        return objects;
    }

    public <E> List<E> getObjects(Class<E> modelClass) {
        if(modelClass == null) throw new IllegalArgumentException("modelClass is null");
        List<E> list = new ArrayList<>(objects.size());
        for(Model object: objects) {
            E castedObject = modelClass.cast(object);
            list.add(castedObject);
        }
        return list;
    }
}
