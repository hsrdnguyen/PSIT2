package ch.avocado.share.servlet.resources.base;

import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Members;
import ch.avocado.share.model.data.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Configuration to render a single resource.
 */
public class DetailViewConfig extends ViewConfig {

    private final Model object;
    private final FormError formErrors;
    private Members members;
    private AccessLevelEnum access;

    public DetailViewConfig(View view, HttpServletRequest request, HttpServletResponse response, Model object,
                            Members members, AccessLevelEnum access) {
        super(view, request, response);
        this.object = object;

        if(object != null) {
            this.formErrors = new FormError(object.getFieldErrors());
            if(access == null) {
                throw new IllegalArgumentException("access is null");
            }
        } else {
            this.formErrors = new FormError(new HashMap<String, String>());
        }
        this.members = members;
        this.access = access;
    }

    public Model getObject() {
        return object;
    }

    public <E> E getObject(Class<E> modelClass) {
        if(modelClass == null) throw new NullPointerException("modelClass is null");
        return modelClass.cast(object);
    }

    public FormError getFormErrors() {
        return formErrors;
    }

    public Members getMembers() {
        return members;
    }

    public AccessLevelEnum getAccess() {
        return access;
    }
}
