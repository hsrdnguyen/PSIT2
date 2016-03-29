package ch.avocado.share.service;

import ch.avocado.share.model.data.Module;

/**
 * Created by coffeemakr on 29.03.16.
 */
public interface IModuleDataHandler {
    String addModule(Module module);

    boolean deleteModule(Module module);

    Module getModule(String moduleId);

    boolean updateModule(Module module);
}
