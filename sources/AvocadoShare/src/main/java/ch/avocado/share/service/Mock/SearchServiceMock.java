package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.File;
import ch.avocado.share.service.ISearchEngineService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bergm on 10/05/2016.
 */
public class SearchServiceMock implements ISearchEngineService {

    private List<String> ids = new ArrayList<>();

    @Override
    public List<String> search(String searchString) {
        return ids;
    }

    @Override
    public boolean indexFile(File file) {

        ids.add(file.getId());

        return true;
    }

    @Override
    public void reloadSearchIndex() {
        ids = new ArrayList<>();
    }
}
