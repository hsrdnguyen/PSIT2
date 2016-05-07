package ch.avocado.share.servlet.resources.base;

import ch.avocado.share.model.data.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * View configuration to render a list of resources.
 */
public class ListViewConfig extends ViewConfig {

    private final List<Model> objects;

    /**
     *
     * @param view The view (should be normally be {@link View#LIST})
     * @param request the request
     * @param response the response
     * @param objects A list of objects
     */
    public ListViewConfig(View view, HttpServletRequest request, HttpServletResponse response, List<Model> objects) {
        super(view, request, response);
        this.objects = objects;
    }

    /**
     * @return An uncasted list of objects.
     */
    public List<Model> getObjects() {
        return objects;
    }

    /**
     * Returns a list of objects casted to the required modelClass.
     * @param modelClass The model class to cast the objects into.
     * @param <E> The model type.
     * @return A list of casted objects.
     */
    public <E> List<E> getObjects(Class<E> modelClass) {
        if(modelClass == null) throw new NullPointerException("modelClass is null");
        List<E> list = new ArrayList<>(objects.size());
        for(Model object: objects) {
            E castedObject = modelClass.cast(object);
            list.add(castedObject);
        }
        return list;
    }
}
