package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.FileConstants;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.IFileStorageHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bergm on 21/03/2016.
 */
public class FileUploadBean implements Serializable {

    private String title;
    private String description;
    private String author;

    public void saveFile(FileItem fileItem) {

        ch.avocado.share.model.data.File file = createFile(fileItem);

        uploadFile(fileItem, file);
        saveFileToDatabase();
    }

    private void saveFileToDatabase() {
        //ServiceLocator.getService(IFileDataHandler.class);
    }

    private ch.avocado.share.model.data.File createFile(FileItem fileItem) {
        ch.avocado.share.model.data.File file = FileFactory.getDefaultFile();
        file.setTitle(title);
        file.setVersion("1.0");
        String filename = createFileHashName(fileItem);
        file.setFilename(filename);
        file.setPath(FileConstants.FILE_DESTINATION_ON_SERVER + "\\" + filename);
        file.setDescription(description);

        return file;
    }

    public void uploadFile(FileItem fileItem, ch.avocado.share.model.data.File fileData) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(FileConstants.MAX_MEM_SIZE);
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File(FileConstants.FILE_TEMP_DESTINATION_ON_SERVER));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax(FileConstants.MAX_FILE_SIZE);
        try {
                ServiceLocator.getService(IFileStorageHandler.class).saveFile(fileItem, fileData);
        } catch (Exception ex) {
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private String createFileHashName(FileItem fileItem) {
        //TODO Cyril do your magic

        return String.valueOf(Math.round(Math.random() * 10000)) + fileItem.getName().substring(fileItem.getName().lastIndexOf("."));
    }
}
