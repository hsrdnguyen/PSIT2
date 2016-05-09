package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.ISearchEngineService;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bergm on 15/04/2016.
 */
public class SearchBean implements Serializable {

    private String searchString = "";

    public List<AccessControlObjectBase> search()
    {
        if (!searchString.equals("")) {
            try {
                IFileDataHandler fileService = ServiceLocator.getService(IFileDataHandler.class);
                IModuleDataHandler moduleService = ServiceLocator.getService(IModuleDataHandler.class);
                IGroupDataHandler groupService = ServiceLocator.getService(IGroupDataHandler.class);

                List<AccessControlObjectBase> results = new LinkedList<>();

                fileService.searchFiles(searchString).forEach(file -> results.add(file));
                moduleService.searchModules(searchString).forEach(file -> results.add(file));
                groupService.searchGroups(searchString).forEach(file -> results.add(file));

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
