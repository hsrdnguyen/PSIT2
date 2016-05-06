package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by coffeemakr on 06.04.16.
 */
public class ModuleBean extends ResourceBean<Module> {

    public static final String ERROR_MODULE_NOT_FOUND = "Modul konnte nicht gefunden werden.";
    private static final int NAME_MAX_LENGTH = 32;
    private String name;

    private void checkParameterName(Module module) {
        if (getName() == null || getName().isEmpty()) {
            module.addFieldError("name", ErrorMessageConstants.ERROR_NO_NAME);
        } else if(getName().length() > NAME_MAX_LENGTH) {
            module.addFieldError("name", ErrorMessageConstants.ERROR_NAME_TOO_LONG);
        }
    }

    @Override
    protected boolean hasMembers() {
        return true;
    }


    @Override
    public Module create() throws HttpBeanException, DataHandlerException {
        Module module = new Module(null, new ArrayList<Category>(), new Date(), new Rating(), getAccessingUser().getId(), "", "", new ArrayList<>());
        checkParameterDescription(module);
        checkParameterName(module);
        if (module.isValid()) {
            IModuleDataHandler moduleDataHandler = getService(IModuleDataHandler.class);
            module.setName(getName());
            module.setDescription(getDescription());
            moduleDataHandler.addModule(module);
        }
        return module;
    }

    @Override
    public Module get() throws HttpBeanException, DataHandlerException {
        return getService(IModuleDataHandler.class).getModule(getId());
    }

    @Override
    public List<Module> index() throws HttpBeanException, DataHandlerException {
        if (getAccessingUser() == null) {
            return new ArrayList<>();
        }
        List<String> moduleIds = getService(ISecurityHandler.class).getIdsOfObjectsOnWhichIdentityHasAccess(getAccessingUser(), AccessLevelEnum.READ);
        return getService(IModuleDataHandler.class).getModules(moduleIds);
    }

    @Override
    public void update(Module module) throws HttpBeanException, DataHandlerException {
        boolean updated = false;
        if (getName() != null && !getName().equals(module.getName())) {
            checkParameterName(module);
            if (module.isValid()) {
                module.setName(getName());
                updated = true;
            }
        }

        updated |= updateDescription(module);

        if(module.isValid() && updated) {
            if(!getService(IModuleDataHandler.class).updateModule(module)) {
                throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ERROR_MODULE_NOT_FOUND);
            }
        }
    }

    @Override
    public void destroy(Module module) throws HttpBeanException, DataHandlerException {
        getService(IModuleDataHandler.class).deleteModule(module);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
