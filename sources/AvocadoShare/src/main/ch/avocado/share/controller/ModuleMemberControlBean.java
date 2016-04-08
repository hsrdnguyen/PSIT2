package ch.avocado.share.controller;

import ch.avocado.share.model.data.Module;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IModuleDataHandler;

public class ModuleMemberControlBean extends MemberControlBean<Module> {
    @Override
    protected Module getTargetById(String id) throws HttpBeanException {
        IModuleDataHandler moduleDataHandler = getService(IModuleDataHandler.class);
        return moduleDataHandler.getModule(id);
    }
}
