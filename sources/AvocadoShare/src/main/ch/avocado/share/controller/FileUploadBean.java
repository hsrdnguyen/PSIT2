package ch.avocado.share.controller;

import ch.avocado.share.common.HexEncoder;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.FileConstants;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.ISecurityHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by bergm on 21/03/2016.
 */
public class FileUploadBean extends ResourceBean<File> {

    private static final String ERROR_NO_TITLE = "Bitte einen Titel eingeben.";
    private static final String ERROR_NO_DESCRIPTION = "Bitte eine Beschreibung angeben.";
    private static final String ERROR_NO_CATEGORY_NAME = "Bitte einen Kategorien Namen eingeben.";
    private static final String ERROR_NO_AUTHOR = "Bitte einen Author eingeben.";
    private static final String ERROR_INTERNAL_SERVER = "Interner Server-Fehler.";
    private static final String ERROR_FILE_TITLE_ALREADY_EXISTS = "Ein File mit diesem Titel existiert bereits.";
    private static final String ERROR_CATEGORY_ALREADY_ADDED = "Diese Kategorie wurde schon hinzugefügt.";
    public static final String ERROR_NO_SUCH_FILE = "File existiert nicht.";
    public static final String ERROR_DATABASE = "File konnte nicht in der Datenbank gespeichert werden.";

    private String title;
    private String description;
    private String author;   //TODO @kunzlio1: Sascha fragen für was author? eg. ersteller?
    private List<Category> categories;
    private FileItem fileItem;

    private final int FILE_READ_BUFFER_SIZE = 512;

    @Override
    protected boolean hasIdentifier() {
        return title != null || getId() != null;
    }

    @Override
    public File create() throws HttpBeanException {
        IFileDataHandler fileDataHandler = getFileDataHandler();
        checkParameterTitle();
        checkParameterDescription();
        //checkParameterAuthor();
        if (!hasErrors()) {
            File file = createFile(fileItem);
            uploadFile(fileItem, file);
            String newFileId = fileDataHandler.addFile(file);
            if (newFileId == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_DATABASE);
            }
            file = fileDataHandler.getFileById(newFileId);
            if (file == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_DATABASE);
            }
            return file;
        }
        return null;
    }

    @Override
    public File get() throws HttpBeanException {
        if(!hasIdentifier()) throw new IllegalStateException("get() without identifier");
        IFileDataHandler fileDataHandler = getFileDataHandler();
        File file = null;
        if(getId() != null) {
            file = fileDataHandler.getFileById(getId());
        }else if (title != null) {
            file = fileDataHandler.getFileByTitle(title);
        }

        if (file == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ERROR_NO_SUCH_FILE);
        }
        return file;
    }

    @Override
    public void update() throws HttpBeanException {
        IFileDataHandler fileDataHandler = getFileDataHandler();
        checkParameterTitle();
        checkParameterDescription();
        //checkParameterAuthor();
        if (!hasErrors()) {
            getObject().setTitle(title);
            getObject().setDescription(description);
            getObject().setCategories(categories);
            if (!fileDataHandler.updateFile(getObject())) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_DATABASE);
            }
        }
    }

    @Override
    public void destroy() throws HttpBeanException {
        IFileDataHandler fileDataHandler = getFileDataHandler();
        if (!fileDataHandler.deleteFile(getObject())) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_DATABASE);
        }
    }

    //TODO @kunzlio1: Fragen für was index()....
    @Override
    public File[] index() throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        return securityHandler.getObjectsOnWhichIdentityHasAccessLevel(File.class, getAccessingUser(), AccessLevelEnum.READ);
    }

    @Override
    public String getAttributeName() {
        return "File";
    }

    public void saveFile(FileItem fileItem) {

        File file = createFile(fileItem);

        uploadFile(fileItem, file);
        saveFileToDatabase();
    }

    public void uploadFile(FileItem fileItem, ch.avocado.share.model.data.File fileData) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(FileConstants.MAX_MEM_SIZE);
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new java.io.File(FileConstants.FILE_TEMP_DESTINATION_ON_SERVER));

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

    public FileItem getFileItem(){ return fileItem;}

    public void setFileItem(FileItem fileItem){ this.fileItem = fileItem;}

    public List<Category> getCategories(){
        return categories;
    }

    public void setCategories(List<Category> categories){
        this.categories = categories;
    }

    public void addCategory(String name){
        if (name == null || name.trim().isEmpty()){
            addFormError("category", ERROR_NO_CATEGORY_NAME);
        }else if (!categories.contains(new Category(name))){
            categories.add(new Category(name.trim()));
        }else {
            addFormError("category", ERROR_CATEGORY_ALREADY_ADDED);
        }
    }

    private void saveFileToDatabase() {
        //ServiceLocator.getService(IFileDataHandler.class);
    }

    private File createFile(FileItem fileItem) {
        File file = FileFactory.getDefaultFile();
        file.setTitle(title);
        file.setVersion("1.0");
        file.setCategories(categories);
        String filename = null;
        try {
            filename = createFileHashName(fileItem);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DigestException e) {
            e.printStackTrace();
        }
        file.setFilename(filename);
        file.setPath(FileConstants.FILE_DESTINATION_ON_SERVER + "\\" + filename);
        file.setDescription(description);
        //file.setOwnerId(getAccessingUser().getId());

        return file;
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

    private void checkParameterTitle() throws HttpBeanException {
        if (title == null || title.trim().isEmpty()) {
            addFormError("title", ERROR_NO_TITLE);
        } else {
            title = title.trim();
            IFileDataHandler fileDataHandler = getFileDataHandler();
            if (fileDataHandler.getFileByTitle(title) != null){
                addFormError("title", ERROR_FILE_TITLE_ALREADY_EXISTS);
            }
            //TODO @kunzlio1: noch implementieren dass auch auf Modul geschaut wird, weil titel nur in modul eindeutig
        }
    }

    private void checkParameterDescription() {
        if (description == null || description.trim().isEmpty()) {
            addFormError("description", ERROR_NO_DESCRIPTION);
        } else {
            description = description.trim();
        }
    }

//    private void checkParameterAuthor() {
//        if (author == null || author.trim().isEmpty()) {
//            addFormError("description", ERROR_NO_AUTHOR);
//        } else {
//            author = author.trim();
//        }
//    }

    private IFileDataHandler getFileDataHandler() throws HttpBeanException {
        IFileDataHandler fileDataHandler = null;
        try {
            fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_INTERNAL_SERVER);
        }
        return fileDataHandler;
    }
}
