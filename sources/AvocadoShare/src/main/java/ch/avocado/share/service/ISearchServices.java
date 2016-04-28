package ch.avocado.share.service;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.File;

import java.util.List;

/**
 * Created by bergm on 28/04/2016.
 */
public interface ISearchServices {

    /**
     * Search data for matches
     * @param searchString
     * @return list of AccessControlObjects that can be Files, Modules, Groups ...
     */
    List<AccessControlObjectBase> search(String searchString);

}
