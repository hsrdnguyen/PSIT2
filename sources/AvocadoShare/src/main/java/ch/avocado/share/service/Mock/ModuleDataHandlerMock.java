package ch.avocado.share.service.Mock;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.service.IModuleDataHandler;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by muellcy1 on 29.03.16.
 */
public class ModuleDataHandlerMock extends DataHandlerMockBase<Module> implements IModuleDataHandler {

    private final int NUMBER_OF_MODULES = 100;

    public ModuleDataHandlerMock() {
        super();
        reset();
    }

    private void reset() {
        objects.clear();
        for (int i = 0; i < NUMBER_OF_MODULES; i++) {
            Module module = new Module("module" + i, new ArrayList<Category>(), new Date(System.currentTimeMillis()), new Rating(), "owner" + i, "Module description " + i, "Module " + i, new ArrayList<>());
            objects.put(module.getId(), module);
        }
    }

    public Module[] getAllModules() {
        return getAll(Module.class);
    }


    @Override
    public String addModule(Module module) {
        return add(module);
    }

    @Override
    public boolean deleteModule(Module module) {
        return delete(module);
    }

    @Override
    public Module getModule(String moduleId) {
        return get(moduleId);
    }

    @Override
    public List<Module> getModules(Collection<String> ids) {
        ArrayList<Module> modules = new ArrayList<>(ids.size());
        for(String id: ids) {
            Module module = getModule(id);
            if(module != null) {
                modules.add(module);
            }
        }
        return modules;
    }

    @Override
    public boolean updateModule(Module module) {
        return update(module);
    }

    public static void use() throws Exception {
        if(!ServiceLocator.getService(IModuleDataHandler.class).getClass().equals(ModuleDataHandlerMock.class)) {
            ServiceLocatorModifier.setService(IModuleDataHandler.class, new ModuleDataHandlerMock());
        }
    }
}
