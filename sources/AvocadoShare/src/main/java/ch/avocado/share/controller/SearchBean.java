package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.exceptions.ServiceException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bergm on 15/04/2016.
 */
public class SearchBean implements Serializable {

    private String searchString = "";

    public List<File> search()
    {
        if (!searchString.equals("")) {
            try {
                IFileDataHandler service = ServiceLocator.getService(IFileDataHandler.class);
                String[] parts = searchString.split(" ");
                List<File> results = service.search(Arrays.asList(parts));
                return results;
            } catch (ServiceException e) {
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
