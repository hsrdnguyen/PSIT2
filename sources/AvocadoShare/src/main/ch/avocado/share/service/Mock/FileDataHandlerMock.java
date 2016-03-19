package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IFileDataHandler;

/**
 * Created by bergm on 19/03/2016.
 */
public class FileDataHandlerMock implements IFileDataHandler {
    @Override
    public String saveFile(File file) {
        return null;
    }

    @Override
    public File getFile(String path, String fileName) {
        return null;
    }

    @Override
    public boolean fileExists(String path, String fileName) {
        return false;
    }
}
