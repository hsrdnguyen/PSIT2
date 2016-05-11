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

/**
 * Created by bergm on 15/04/2016.
 */
public class SearchBean implements Serializable {

    private String searchString = "";

    public List<AccessControlObjectBase> search() {
        if (!searchString.equals("")) {
            try {
                IFileDataHandler fileService = ServiceLocator.getService(IFileDataHandler.class);
                IModuleDataHandler moduleService = ServiceLocator.getService(IModuleDataHandler.class);
                IGroupDataHandler groupService = ServiceLocator.getService(IGroupDataHandler.class);

                final List<AccessControlObjectBase> results = new LinkedList<>();

                List<File> files = fileService.searchFiles(searchString);
                List<Module> modules = moduleService.searchModules(searchString);
                List<Group> groups = groupService.searchGroups(searchString);

                results.addAll(files);
                results.addAll(modules);
                results.addAll(groups);

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
