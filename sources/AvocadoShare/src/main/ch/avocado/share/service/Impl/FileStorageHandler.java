package ch.avocado.share.service.Impl;

import ch.avocado.share.common.constants.FileConstants;
import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IFileStorageHandler;
import org.apache.commons.fileupload.FileItem;

/**
 * Created by bergm on 22/03/2016.
 */
public class FileStorageHandler implements IFileStorageHandler {
    @Override
    public String saveFile(FileItem fi, File dbFIle) {
        java.io.File file;
        if (!fi.isFormField()) {
            // Get the uploaded file parameters
            String fileName = fi.getName();

            try {
                file = new java.io.File(dbFIle.getPath());
                fi.write(file);
            }catch (Exception e)
            {
                return "";
            }
        }

        return FileConstants.FILE_DESTINATION_ON_SERVER + dbFIle.getFilename();
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
