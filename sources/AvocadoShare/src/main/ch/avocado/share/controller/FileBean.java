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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class FileBean extends ResourceBean<File> {

    private String title;
    private String description;
    // private String author;   //TODO @kunzlio1: Sascha fragen für was author? eg. ersteller?
    private List<Category> categories;
    private FileItem uploadedFileItem;
    private String moduleId;

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
                switch (item.getFieldName()) {
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
        if (request.getMethod().equalsIgnoreCase("POST") && request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
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
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
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

    /**
     * @param user the accessing user
     * @return A list of all modules a user can upload files into.
     * @throws HttpBeanException
     */
    static public Module[] getModulesToUpload(User user) throws HttpBeanException {
        if (user == null) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ErrorMessageConstants.ERROR_ACCESS_DENIED);
        }
        ISecurityHandler securityHandler;
        try {
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_SECURITY_HANDLER);
        }
        Module[] modules = securityHandler.getObjectsOnWhichIdentityHasAccessLevel(Module.class, user, AccessLevelEnum.READ);
        if (modules == null) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
        }
        return modules;
    }

    /**
     * @return A list of all files the user owns.
     * @throws HttpBeanException
     */
    @Override
    public File[] index() throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        if(getAccessingUser() != null) {
            return securityHandler.getObjectsOnWhichIdentityHasAccessLevel(File.class, getAccessingUser(), AccessLevelEnum.READ);
        }
        return new File[0];
    }

    @Override
    public File get() throws HttpBeanException {
        if (!hasIdentifier()) throw new IllegalStateException("get() without identifier");
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
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
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        File file = getObject();
        boolean changed = false;
        if (getTitle() != null && !file.getTitle().equals(getTitle())) {
            checkParameterTitle();
            if (!hasErrors()) {
                file.setTitle(getTitle());
                changed = true;
            }
        }
        if (getDescription() != null && !file.getDescription().equals(getDescription())) {
            checkParameterDescription();
            if (!hasErrors()) {
                file.setDescription(getDescription());
                changed = true;
            }
        }

        if (getModuleId() != null && !file.getModuleId().equals(getModuleId())) {
            checkParameterModuleId();
            if (!hasErrors()) {
                file.setModuleId(getModuleId());
                changed = true;
            }
        }

        if (getCategories() != null) {
            if (!hasErrors()) {
                file.setCategories(getCategories());
                changed = true;
            }
        }
        if (getUploadedFileItem() != null && !hasErrors()) {
            String path = uploadFile(getUploadedFileItem());
            file.setPath(path);
            changed = true;
        }
        if (!hasErrors() && changed) {
            if (!fileDataHandler.updateFile(file)) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
            }
        }
    }

    @Override
    public void destroy() throws HttpBeanException {
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        if (!fileDataHandler.deleteFile(getObject())) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
        }
    }

    @Override
    public String getAttributeName() {
        return "File";
    }


    /**
     * Uploads the file to the server and stores it using {@link IFileStorageHandler}.
     *
     * @param fileItem The file
     * @return The reference to the file on the disk
     * @throws HttpBeanException
     */
    public String uploadFile(FileItem fileItem) throws HttpBeanException {
        if (fileItem == null) throw new IllegalArgumentException("uploadedFileItem is null");
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
            return getService(IFileStorageHandler.class).saveFile(fileItem);
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


    private void checkParameterTitle() throws HttpBeanException {
        checkParameterModuleId();
        if (hasErrors()) {
            return;
        }
        if (getTitle() == null || getTitle().trim().isEmpty()) {
            addFormError("title", ErrorMessageConstants.ERROR_NO_TITLE);
        } else {
            setTitle(getTitle().trim());
            IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
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

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

}
