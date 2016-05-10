package ch.avocado.share.service;

import ch.avocado.share.model.data.File;

import java.util.List;

/**
 * Created by bergm on 28/04/2016.
 */
public interface ISearchEngineService {

    /**
     * Search data for matches
     * @param searchString
     * @return list of filepaths that can be Files, Modules, Groups ...
     */
    List<String> search(String searchString);

    /**
     * Indexes the file given, so it can be searched
     * @param file file to be indexed
     * @return true if file could be indexed
     */
    boolean indexFile(File file);

    /**
     * Deletes the whole index and reloads all data in the search-index
     */
    void reloadSearchIndex();
}
