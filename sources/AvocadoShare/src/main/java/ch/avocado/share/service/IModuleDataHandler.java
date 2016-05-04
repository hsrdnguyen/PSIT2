package ch.avocado.share.service;

import ch.avocado.share.model.data.Module;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * Created by coffeemakr on 29.03.16.
 */
public interface IModuleDataHandler {
    String addModule(Module module) throws DataHandlerException;

    void deleteModule(Module module) throws DataHandlerException, ObjectNotFoundException;

    Module getModule(String moduleId) throws DataHandlerException, ObjectNotFoundException;

    List<Module> getModules(Collection<String> ids) throws DataHandlerException;

    void updateModule(Module module) throws DataHandlerException, ObjectNotFoundException;
}
