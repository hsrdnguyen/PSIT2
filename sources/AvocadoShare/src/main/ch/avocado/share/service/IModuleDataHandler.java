package ch.avocado.share.service;

import ch.avocado.share.model.data.Module;

import java.util.Collection;
import java.util.List;

/**
 * Created by coffeemakr on 29.03.16.
 */
public interface IModuleDataHandler {
    String addModule(Module module);

    boolean deleteModule(Module module);

    Module getModule(String moduleId);

    List<Module> getModules(Collection<String> ids);

    boolean updateModule(Module module);
}
