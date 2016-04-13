package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Module;
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

    private String name;

    private void checkParameterName() {
        if (getName() == null || getName().isEmpty()) {
            addFormError("name", ErrorMessageConstants.ERROR_NO_NAME);
        }
    }

    @Override
    protected String getTemplateFolder() {
        return "module_templates/";
    }

    @Override
    public Module create() throws HttpBeanException, DataHandlerException {
        checkParameterDescription();
        checkParameterName();
        if (!hasErrors()) {
            IModuleDataHandler moduleDataHandler = getService(IModuleDataHandler.class);
            ISecurityHandler securityHandler = getService(ISecurityHandler.class);
            Module module = new Module(null, new ArrayList<Category>(), new Date(), 0.0f, getAccessingUser().getId(), getDescription(), getName());
            module.setId(moduleDataHandler.addModule(module));
            securityHandler.setAccessLevel(getAccessingUser(), module, AccessLevelEnum.MANAGE);
            return module;
        }
        return null;
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
    public void update() throws HttpBeanException, DataHandlerException {
        Module module = getObject();
        boolean updated = false;
        if (getName() != null && !getName().equals(module.getName())) {
            checkParameterName();
            if (!hasErrors()) {
                module.setName(getName());
                updated = true;
            }
        }

        updated |= updateDescription(module);

        if(!hasErrors() && updated) {
            if(!getService(IModuleDataHandler.class).updateModule(module)) {
                throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, "Modul konnte nicht gefunden werden.");
            }
        }
    }

    @Override
    public void destroy() throws HttpBeanException, DataHandlerException {
        getService(IModuleDataHandler.class).deleteModule(getObject());
    }

    @Override
    public String getAttributeName() {
        return "Module";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
