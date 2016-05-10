package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by bergm on 15/04/2016.
 */
public class SearchBean implements Serializable {

    private String searchString = "";

    public List<AccessControlObjectBase> search() throws ServiceNotFoundException {
        if (!searchString.equals("")) {
            try {
                IFileDataHandler fileService = ServiceLocator.getService(IFileDataHandler.class);
                IModuleDataHandler moduleService = ServiceLocator.getService(IModuleDataHandler.class);
                IGroupDataHandler groupService = ServiceLocator.getService(IGroupDataHandler.class);

                final List<AccessControlObjectBase> results = new LinkedList<>();

                fileService.searchFiles(searchString).forEach(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        results.add(file);
                    }
                });
                moduleService.searchModules(searchString).forEach(new Consumer<Module>() {
                    @Override
                    public void accept(Module file) {
                        results.add(file);
                    }
                });
                groupService.searchGroups(searchString).forEach(new Consumer<Group>() {
                    @Override
                    public void accept(Group file) {
                        results.add(file);
                    }
                });

                return results;
            } catch (ServiceNotFoundException | DataHandlerException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
