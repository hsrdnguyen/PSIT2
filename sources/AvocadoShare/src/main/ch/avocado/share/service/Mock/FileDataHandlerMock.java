package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IFileDataHandler;

/**
 * Created by bergm on 19/03/2016.
 */
public class FileDataHandlerMock implements IFileDataHandler {
    @Override
    public boolean addFile(File file) {
        return false;
    }

    @Override
    public boolean deleteFile(File file) {
        return false;
    }

    @Override
    public File getFile(String fileId) {
        return null;
    }

    @Override
    public boolean updateFile(File file) {
        return false;
    }
}
