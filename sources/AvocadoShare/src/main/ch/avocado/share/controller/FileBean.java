package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.constants.FileConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.*;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.FileStorageException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class FileBean extends ResourceBean<File> {

    private String title;
    // private String author;   //TODO @kunzlio1: Sascha fragen für was author? eg. ersteller?
    private List<Category> categories;
    private FileItem fileItem;
    private String moduleId;

    @Override
    protected String getTemplateFolder() {
        return "file_templates/";
    }

    @Override
    protected boolean hasMembers() {
        return true;
    }

    @Override
    public boolean hasIdentifier() {
        return (getTitle() != null && getModuleId() != null) || getId() != null;
    }


    @Override
    public File create() throws HttpBeanException, DataHandlerException {
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        IModuleDataHandler moduleDataHandler = getService(IModuleDataHandler.class);
        File file = FileFactory.getDefaultFile();
        checkParameterTitle(file);
        checkParameterDescription(file);
        checkParameterModuleId(file);
        checkUploadedFile(file);
        //checkParameterAuthor();
        if (!file.hasErrors()) {
            Module module = moduleDataHandler.getModule(getModuleId());
            if(module == null) {
                file.addFieldError("module", "Modul existiert nicht.");
                return null;
            }
            ensureAccessingUserHasAccess(module, AccessLevelEnum.WRITE);
            String path = uploadFile(getFileItem());
            file.setTitle(getTitle());
            file.setCategories(getCategories());
            file.setDescription(getDescription());
            file.setPath(path);
            file.setOwnerId(getAccessingUser().getId());
            String fileId;
            fileId = fileDataHandler.addFile(file);
            file = fileDataHandler.getFile(fileId);
            if (file == null) {
                throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.DATAHANDLER_EXPCEPTION);
            }
        }
        return file;
    }

    private void checkUploadedFile(File file) {
        if(getFileItem() == null) {
            file.addFieldError("fileItem", "Keine Datei ausgewählt.");
        }
    }

    /**
     * @param user the accessing user
     * @return A list of all modules a user can upload files into.
     * @throws HttpBeanException
     */
    static public List<Module> getModulesToUpload(User user) throws HttpBeanException {
        if (user == null) {
            throw new HttpBeanException(HttpServletResponse.SC_FORBIDDEN, ErrorMessageConstants.ACCESS_DENIED);
        }
        ISecurityHandler securityHandler = getService(ISecurityHandler.class);
        IModuleDataHandler moduleDataHandler = getService(IModuleDataHandler.class);
        List<Module> modules = null;
        try {
            modules = moduleDataHandler.getModules(securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.READ));
        } catch (DataHandlerException e) {
            throw new HttpBeanDatabaseException();
        }
        return modules;
    }

    /**
     * @return A list of all files the user owns.
     * @throws HttpBeanException
     */
    @Override
    public List<File> index() throws HttpBeanException {
        ISecurityHandler securityHandler = getSecurityHandler();
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        if(getAccessingUser() != null) {
            try {
                List<String> fileIds = securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(getAccessingUser(), AccessLevelEnum.READ);
                return fileDataHandler.getFiles(fileIds);
            } catch (DataHandlerException e) {
                throw new HttpBeanDatabaseException();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public File get() throws HttpBeanException, DataHandlerException {
        if (!hasIdentifier()) throw new IllegalStateException("get() without identifier");
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        File file = null;
        if (getId() != null) {
            file = fileDataHandler.getFile(getId());
        } else if (getTitle() != null && getModuleId() != null) {
            file = fileDataHandler.getFileByTitleAndModule(getTitle(), getModuleId());
        }
        if (file == null) {
            throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_FILE);
        }
        return file;
    }


    @Override
    public void update(File object) throws HttpBeanException {
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        File file = object;
        boolean changed = false;
        if (getTitle() != null && !file.getTitle().equals(getTitle())) {
            checkParameterTitle(file);
            if (!file.hasErrors()) {
                file.setTitle(getTitle());
                changed = true;
            }
        }

        changed |= updateDescription(file);

        if (getModuleId() != null && !file.getModuleId().equals(getModuleId())) {
            checkParameterModuleId(file);
            if (!file.hasErrors()) {
                file.setModuleId(getModuleId());
                changed = true;
            }
        }

        if (getCategories() != null) {
            if (!file.hasErrors()) {
                file.setCategories(getCategories());
                changed = true;
            }
        }
        if (getFileItem() != null && !file.hasErrors()) {
            String path = uploadFile(getFileItem());
            file.setPath(path);
            changed = true;
        }

        if (!file.hasErrors() && changed) {
            try{
                if(!fileDataHandler.updateFile(file)) {
                    throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_FILE);
                }
            } catch (DataHandlerException e) {
                e.printStackTrace();
                throw new HttpBeanDatabaseException();
            }
        }
    }

    @Override
    public void destroy(File object) throws HttpBeanException {
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        try {
            if (!fileDataHandler.deleteFile(object)) {
                throw new HttpBeanException(HttpServletResponse.SC_NOT_FOUND, ErrorMessageConstants.ERROR_NO_SUCH_FILE);
            }
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanDatabaseException();
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
        if (fileItem == null) throw new IllegalArgumentException("fileItem is null");
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
     * @return The uploaded file
     */
    private FileItem getFileItem() {
        return fileItem;
    }

    /**
     * @param fileItem The uploaded file
     */
    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    /**
     * @return A list of categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Adds a category to the file, by handing over the name of the category
     * @param categoryName The name of the category which you want to add to the file
     */
    public void addCategory(String categoryName){
        if (categoryName == null || categoryName.trim().isEmpty())
            throw new IllegalArgumentException("categoryName is null or emty");
        Category category = new Category(categoryName.trim());
        if (categories.contains(category))
            throw new IllegalArgumentException("Category was allready added to File");
        //TODO @kunzlio1: fragen wie das mit ErrorMessages ausgeben genau gehandelt wird.
        categories.add(category);
    }

    /**
     * Remove a category from the file
     * @param category The category which you want to remove from the file
     */
    public void removeCategory(Category category){
        if (category == null) throw new IllegalArgumentException("category is null");
        categories.remove(category); //TODO @kunzlio1: fragen was wir aus dem jsp heraus bekommen? (Category Obj oder name)
    }


    private void checkParameterTitle(File file) throws HttpBeanException {
        checkParameterModuleId(file);
        if (file.hasErrors()) {
            return;
        }
        if (getTitle() == null || getTitle().trim().isEmpty()) {
            file.addFieldError("title", ErrorMessageConstants.ERROR_NO_TITLE);
        } else {
            setTitle(getTitle().trim());
            IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);

            try {
                if (fileDataHandler.getFileByTitleAndModule(getTitle(), getModuleId()) != null) {
                    file.addFieldError("title", ErrorMessageConstants.ERROR_FILE_TITLE_ALREADY_EXISTS);
                }
            } catch (DataHandlerException e) {
                file.addFieldError("title", ErrorMessageConstants.DATAHANDLER_EXPCEPTION);
            }
        }
    }

    private void checkParameterModuleId(File file) {
        if (getModuleId() == null || getModuleId().trim().isEmpty()) {
            file.addFieldError("moduleId", ErrorMessageConstants.ERROR_NO_MODULE_ID);
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
