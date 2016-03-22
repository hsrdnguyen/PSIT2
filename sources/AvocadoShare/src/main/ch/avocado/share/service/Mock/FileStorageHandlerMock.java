package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IFileStorageHandler;
import org.apache.commons.fileupload.FileItem;

/**
 * Created by bergm on 19/03/2016.
 */
public class FileStorageHandlerMock implements IFileStorageHandler {
    @Override
    public String saveFile(FileItem fi, File dbFIle) {
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
