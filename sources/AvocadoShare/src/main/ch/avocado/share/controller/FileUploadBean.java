package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.FileConstants;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.IFileDataHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bergm on 21/03/2016.
 */
public class FileUploadBean implements Serializable {


    private String title;
    private String filename;
    private String description;
    private String author;

    public void saveFile(HttpServletRequest request) {
        uploadFile(request);
        saveFileToDatabase();
    }

    private void saveFileToDatabase() {

        ch.avocado.share.model.data.File file = FileFactory.getDefaultFile();
        file.setTitle(title);
        file.setVersion("1.0");
        file.setFilename(title);
        file.setPath(FileConstants.FILE_DESTINATION_ON_SERVER + filename);
        file.setDescription(description);

        //ServiceLocator.getService(IFileDataHandler.class);
    }

    public void uploadFile(HttpServletRequest request) {
        File file;

        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(FileConstants.MAX_MEM_SIZE);
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File("C:\\avocadoshare\\TMP"));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax(FileConstants.MAX_FILE_SIZE);
        try {
            // Parse the request to get file items.
            List fileItems = upload.parseRequest(request);

            // Process the uploaded file items
            Iterator i = fileItems.iterator();
            while (i.hasNext()) {
                FileItem fi = (FileItem) i.next();
                if (!fi.isFormField()) {
                    // Get the uploaded file parameters
                    String fileName = fi.getName();


                    // Write the file
                    if (fileName.lastIndexOf("\\") >= 0) {
                        filename = fileName.substring(fileName.lastIndexOf("\\"));
                        file = new File(FileConstants.FILE_DESTINATION_ON_SERVER +
                                filename);
                    } else {
                        filename = fileName.substring(fileName.lastIndexOf("\\") + 1);
                        file = new File(FileConstants.FILE_DESTINATION_ON_SERVER + fileName);
                    }
                    fi.write(file);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
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
}
