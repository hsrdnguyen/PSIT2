package ch.avocado.share.test;

import ch.avocado.share.model.data.Module;
import org.mockito.ArgumentMatcher;

/**
 * Argument matcher for Modules
 */
public class ModuleArgumentMatcher implements ArgumentMatcher<Module>{
    private Module module;

    public ModuleArgumentMatcher(Module module) {
        this.module = module;
    }

    @Override
    public boolean matches(Object o) {
        if(!(o instanceof Module)) {
            return false;
        }
        Module otherModule = (Module) o;
        return otherModule.getId().equals(module.getId());
    }
}
