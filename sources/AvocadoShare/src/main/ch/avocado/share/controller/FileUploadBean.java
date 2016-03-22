package ch.avocado.share.controller;

import ch.avocado.share.common.HexEncoder;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.FileConstants;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.IFileStorageHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bergm on 21/03/2016.
 */
public class FileUploadBean implements Serializable {

    private String title;
    private String description;
    private String author;

    private final int FILE_READ_BUFFER_SIZE = 512;

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

    private String createFileHashName(FileItem fileItem) throws IOException, DigestException{
        byte[] buffer = new byte[FILE_READ_BUFFER_SIZE];
        int readBytes = 0;
        MessageDigest messageDigest;
        try{
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 digest doesn't exist");
        }
        InputStream inputStream = fileItem.getInputStream();
        while(readBytes >= 0) {
            readBytes = inputStream.read(buffer);
            if(readBytes >= 0) {
                messageDigest.update(buffer, 0, readBytes);
            }
        }
        // finalize digest (padding etc.)
        byte[] digest = messageDigest.digest();
        return HexEncoder.bytesToHex(digest);
    }
}
