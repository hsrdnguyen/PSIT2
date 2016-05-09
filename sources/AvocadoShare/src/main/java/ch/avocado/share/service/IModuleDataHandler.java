package ch.avocado.share.service;

import ch.avocado.share.model.data.Module;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.Collection;
import java.util.List;

/**
 * Created by coffeemakr on 29.03.16.
 */
public interface IModuleDataHandler {
    String addModule(Module module) throws DataHandlerException;

    boolean deleteModule(Module module) throws DataHandlerException;

    Module getModule(String moduleId) throws DataHandlerException;

    List<Module> getModules(Collection<String> ids) throws DataHandlerException;

    boolean updateModule(Module module) throws DataHandlerException;

    List<Module> searchModules(String searchString) throws DataHandlerException;
}
