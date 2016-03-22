package ch.avocado.share.service.Impl;

import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IFileDataHandler;

/**
 * Created by bergm on 22/03/2016.
 */
public class FileDataHandler implements IFileDataHandler {
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
