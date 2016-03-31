package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.constants.FileConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.FileStorageException;
import jdk.internal.util.xml.impl.Input;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by bergm on 21/03/2016.
 */
public class FileBean extends ResourceBean<File> {

    private String title;
    private String description;
    // private String author;   //TODO @kunzlio1: Sascha fragen für was author? eg. ersteller?
    private List<Category> categories;
    private FileItem uploadedFileItem;
    private String moduleId;

    private static int DOWNLOAD_BUFFER_SIZE = 512;

    @Override
    protected boolean hasIdentifier() {
        return (getTitle() != null && getModuleId() != null) || getId() != null;
    }

    private void parseMultipartFormData(HttpServletRequest request) throws HttpBeanException {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (request.getContentType() == null || !request.getContentType().contains("multipart/form-data")) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, ErrorMessageConstants.ERROR_CONTENT_TYPE_NOT_ALLOWED);
        }
        List<FileItem> items;
        try {
            items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        } catch (FileUploadException e) {
            throw new HttpBeanException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
        for (FileItem item : items) {
            if (item.isFormField()) {
                switch (item.getFieldName()){
                    case "description":
                        setDescription(item.getString());
                        break;
                    case "title":
                        setTitle(item.getString());
                        break;
                    case "moduleId":
                        setModuleId(item.getString());
                        break;
                    case "method":
                        setMethod(item.getString());
                        break;
                    case "id":
                        setId(item.getString());
                        break;
                    case "action":
                        setAction(item.getString());
                        break;
                }
            } else {
                setUploadedFileItem(item);
            }
        }
    }

    @Override
    public TemplateType executeRequest(HttpServletRequest request, HttpServletResponse response) {
        if(request.getMethod().equalsIgnoreCase("POST") && request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
            try {
                parseMultipartFormData(request);
            } catch (HttpBeanException e) {
                sendErrorFromHttpBeanException(e, response);
                return null;
            }
        }
        return super.executeRequest(request, response);
    }


    private File newFileModel(String path) {
        File file = FileFactory.getDefaultFile();
        file.setTitle(getTitle());
        file.setCategories(getCategories());
        file.setDescription(getDescription());
        file.setPath(path);
        return file;
    }

    @Override
    public File create() throws HttpBeanException {
        IFileDataHandler fileDataHandler = getFileDataHandler();
        checkParameterTitle();
        checkParameterDescription();
        checkParameterModuleId();
        //checkParameterAuthor();
        if (!hasErrors()) {
            String path = uploadFile(getUploadedFileItem());
            File file = newFileModel(path);
            String newFileId = fileDataHandler.addFile(file);
            if (newFileId == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
            }
            file = fileDataHandler.getFileById(newFileId);
            if (file == null) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
            }
            return file;
        }
        return null;
    }

    static public Module[] getModuleForAccessingUser(HttpServletRequest request) throws HttpBeanException {
        ISecurityHandler securityHandler;
        try {
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        }catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_SECURITY_HANDLER);
        }
        UserSession session = new UserSession(request);
        User user = session.getUser();
        if (user == null) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ErrorMessageConstants.ERROR_ACCESS_DENIED);
        }
        Module[] modules = securityHandler.getObjectsOnWhichIdentityHasAccessLevel(Module.class, user, AccessLevelEnum.READ);
        if(modules == null) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
        }
        return modules;
    }

    @Override
    public File get() throws HttpBeanException {
        if (!hasIdentifier()) throw new IllegalStateException("get() without identifier");
        IFileDataHandler fileDataHandler = getFileDataHandler();
        File file = null;
        if (getId() != null) {
            file = fileDataHandler.getFileById(getId());
        } else if (getTitle() != null && getModuleId() != null) {
            file = fileDataHandler.getFileByTitleAndModule(getTitle(), getModuleId());
        }
        if (file == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_FILE);
        }
        return file;
    }


    @Override
    public void update() throws HttpBeanException {
        IFileDataHandler fileDataHandler = getFileDataHandler();
        File file = getObject();
        if(getTitle() != null) {
            checkParameterTitle();
            if(!hasErrors()) {
                file.setTitle(getTitle());
            }
        }
        if(getDescription() != null) {
            checkParameterDescription();
            if(!hasErrors()) {
                file.setDescription(getDescription());
            }
        }

        if(getModuleId() != null) {
            checkParameterModuleId();
            if(!hasErrors()) {
                file.setModuleId(getModuleId());
            }
        }

        if(getCategories() != null) {
            if(!hasErrors()) {
                file.setCategories(getCategories());
            }
        }
        if(getUploadedFileItem() != null && !hasErrors()) {
            String path = uploadFile(getUploadedFileItem());
            file.setPath(path);
        }
        if(!hasErrors()) {
            if (!fileDataHandler.updateFile(getObject())) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
            }
        }
    }

    @Override
    public void destroy() throws HttpBeanException {
        IFileDataHandler fileDataHandler = getFileDataHandler();
        if (!fileDataHandler.deleteFile(getObject())) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
        }
    }

    /**
     * @return A list of all files for the module.
     * @throws HttpBeanException
     */
    @Override
    public File[] index() throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        // TODO: what files are to list when user is not authenticated?
        if(getAccessingUser() == null) {
            return new File[0];
        }
        return securityHandler.getObjectsOnWhichIdentityHasAccessLevel(File.class, getAccessingUser(), AccessLevelEnum.READ);
    }

    @Override
    public String getAttributeName() {
        return "File";
    }


    private IFileStorageHandler getStorageHandler() throws HttpBeanException {
        try {
            return ServiceLocator.getService(IFileStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_STORAGE_HANDLER_NOT_FOUND);
        }
    }

    /**
     * Uploads the file to the server and stores it using {@link IFileStorageHandler}.
     * @param fileItem The file
     * @return The reference to the file on the disk
     * @throws HttpBeanException
     */
    public String uploadFile(FileItem fileItem) throws HttpBeanException {
        if(fileItem == null) throw new IllegalArgumentException("uploadedFileItem is null");
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
            return getStorageHandler().saveFile(fileItem);
        } catch (FileStorageException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @return THe title of the file
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title of the file
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The description of the file
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description of the file
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The uploaded file
     */
    private FileItem getUploadedFileItem() {
        return uploadedFileItem;
    }

    /**
     * @param uploadedFileItem The uploaded file
     */
    private void setUploadedFileItem(FileItem uploadedFileItem) {
        this.uploadedFileItem = uploadedFileItem;
    }

    /**
     * @return A list of categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * @param categories  A list of categories
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }


    public void addCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            addFormError("category", ErrorMessageConstants.ERROR_NO_CATEGORY_NAME);
        } else if (!getCategories().contains(new Category(name))) {
            getCategories().add(new Category(name.trim()));
        } else {
            addFormError("category", ErrorMessageConstants.ERROR_CATEGORY_ALREADY_ADDED);
        }
    }

    private void checkParameterTitle() throws HttpBeanException {
        checkParameterModuleId();
        if(hasErrors()) {
            return;
        }
        if (getTitle() == null || getTitle().trim().isEmpty()) {
            addFormError("title", ErrorMessageConstants.ERROR_NO_TITLE);
        } else {
            setTitle(getTitle().trim());
            IFileDataHandler fileDataHandler = getFileDataHandler();
            if (fileDataHandler.getFileByTitleAndModule(getTitle(), getModuleId()) != null) {
                addFormError("title", ErrorMessageConstants.ERROR_FILE_TITLE_ALREADY_EXISTS);
            }
            //TODO @kunzlio1: noch implementieren dass auch auf Modul geschaut wird, weil titel nur in modul eindeutig
        }
    }

    private void checkParameterDescription() {
        if (getDescription() == null || getDescription().trim().isEmpty()) {
            addFormError("description", ErrorMessageConstants.ERROR_NO_DESCRIPTION);
        } else {
            setDescription(getDescription().trim());
        }
    }

    private void checkParameterModuleId() {
        if (getModuleId() == null || getModuleId().trim().isEmpty()) {
            addFormError("moduleId", ErrorMessageConstants.ERROR_NO_MODULE_ID);
        } else {
            setModuleId(getModuleId().trim());
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
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_INTERNAL_SERVER);
        }
        return fileDataHandler;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public void download(File file, HttpServletResponse response) throws HttpBeanException{
        byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
        if(!hasIdentifier()) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_FILE);
        } else {
            InputStream stream;
            IFileStorageHandler storageHandler;
            storageHandler = getStorageHandler();
            String mimeType;
            try {
                stream = storageHandler.readFile(file.getPath());
                mimeType = storageHandler.getContentType(file.getPath());
            } catch (FileStorageException e) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }

            response.setHeader("Content-Type", mimeType);

            try {
                OutputStream outputStream = response.getOutputStream();
                int bytesRead;
                while((bytesRead = stream.read(buffer, 0, buffer.length)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
            } catch (IOException e) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }

        }
    }
}
