package ch.avocado.share.controller;

import ch.avocado.share.model.data.Module;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

public class ModuleMemberControlBean extends MemberControlBean<Module> {
    @Override
    protected Module getTargetById(String id) throws HttpBeanException {
        IModuleDataHandler moduleDataHandler = getService(IModuleDataHandler.class);
        try{
            return moduleDataHandler.getModule(id);
        } catch (DataHandlerException e) {
            throw new HttpBeanDatabaseException();
        }
    }
}
