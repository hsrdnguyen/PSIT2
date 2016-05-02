package ch.avocado.share.controller;

import ch.avocado.share.common.Filename;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.constants.FileStorageConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.*;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.FileStorageException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileBean extends ResourceBean<File> {

    private String title;
    private FileItem fileItem;
    private String moduleId;
    private Rating rating;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected boolean hasMembers() {
        return true;
    }

    @Override
    public boolean hasIdentifier() {
        return (getTitle() != null && getModuleId() != null) || getId() != null;
    }


    private File newFile() {
        return new File(getAccessingUser().getId(),
                getDescription(),
                getTitle(),
                new Date() /* last changed */,
                getModuleId(),
                null /* disk file */);
    }

    @Override
    public File create() throws HttpBeanException, DataHandlerException {
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        IModuleDataHandler moduleDataHandler = getService(IModuleDataHandler.class);
        File file = newFile();
        checkParameterTitle(file);
        checkParameterDescription(file);
        checkParameterModuleId(file);
        checkUploadedFile(file);
        if (!file.hasErrors()) {
            Module module = moduleDataHandler.getModule(getModuleId());
            if (module == null) {
                file.addFieldError("module", "Modul existiert nicht.");
                return null;
            }
            ensureAccessingUserHasAccess(module, AccessLevelEnum.WRITE);
            DiskFile diskFile = uploadFile(getFileItem());
            file.setDiskFile(diskFile);
            fileDataHandler.addFile(file);
        }
        return file;
    }

    private void checkUploadedFile(File file) {
        if (getFileItem() == null) {
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
            modules = moduleDataHandler.getModules(securityHandler.getIdsOfObjectsOnWhichIdentityHasAccess(user, AccessLevelEnum.WRITE));
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
        ISecurityHandler securityHandler = getService(ISecurityHandler.class);
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        if (getAccessingUser() != null) {
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
    public void update(File file) throws HttpBeanException {
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
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
            DiskFile diskFile = uploadFile(getFileItem());
            file.setDiskFile(diskFile);
            changed = true;
        }

        if (!file.hasErrors() && changed) {
            try {
                if (!fileDataHandler.updateFile(file)) {
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


    /**
     * Uploads the file to the server and stores it using {@link IFileStorageHandler}.
     *
     * @param fileItem The file
     * @return The reference to the file on the disk
     * @throws HttpBeanException
     */
    private DiskFile uploadFile(FileItem fileItem) throws HttpBeanException {
        if (fileItem == null) throw new IllegalArgumentException("fileItem is null");
        IFileStorageHandler fileStorageHandler = getService(IFileStorageHandler.class);
        String mimeType, path, extension;
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(FileStorageConstants.MAX_MEM_SIZE);
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new java.io.File(FileStorageConstants.FILE_TEMP_DESTINATION_ON_SERVER));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax(FileStorageConstants.MAX_FILE_SIZE);
        try {
            path = fileStorageHandler.saveFile(fileItem);
            mimeType = fileStorageHandler.getContentType(path, fileItem.getName());
        } catch (FileStorageException e) {
            e.printStackTrace();
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Datei konnte nicht gespeichert werden.");
        }
        extension = Filename.getExtension(fileItem.getName());
        return new DiskFile(path, mimeType, extension);
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
     * Gets the current rating value of the file.
     *
     * @return the rating value.
     */
    public float getRatingForObject() {
        return rating.getRating();
    }

    /**
     * Gets the current number of ratings of the file.
     *
     * @return the number of ratings.
     */
    public int getNumberOfRatingsForObject() {
        return rating.getNumberOfRatings();
    }

    /**
     * Set's the rating object for the file.
     *
     * @param rating the rating object to been set.
     */
    public void setRating(Rating rating) {
        this.rating = rating;
    }

    /**
     * Checks if the accessing user had already rated the file.
     *
     * @return true if the user had already rated the file, false if not.
     */
    public boolean hasAccessingUserRated() {
        return this.rating.hasUserRated(Long.parseLong(getAccessingUser().getId()));
    }

    /**
     * Adds a category to the file, by handing over the name of the category.
     *
     * @param categoryName The name of the category which you want to add to the file.
     */
    public void addCategory(String categoryName) {
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
     *
     * @param category The category which you want to remove from the file
     */
    public void removeCategory(Category category) {
        if (category == null) throw new IllegalArgumentException("category is null");
        categories.remove(category); //TODO @kunzlio1: fragen was wir aus dem jsp heraus bekommen? (Category Obj oder name)
    }

    /**
     * Gets the rating which the accessing user gave to the file
     *
     * @return the rating of the user for the file
     * @throws HttpBeanException is thrown, if there is an error while getting the rating.
     */
    public int getRatingForAccessingUser() throws HttpBeanException {
        if (!rating.hasUserRated(Long.parseLong(getAccessingUser().getId())))
            throw new HttpBeanException(0, "User hadn't rated yet");
        IRatingDataHandler ratingDataHandler = getService(IRatingDataHandler.class);
        try {
            return ratingDataHandler.getRatingForUserAndObject(Long.parseLong(getAccessingUser().getId()), Long.parseLong(moduleId));
        } catch (DataHandlerException e) {
            throw new HttpBeanException(e);
        }
    }

    /**
     * Adds a ratin which a user gave to the file.
     *
     * @param rating       the rating the user gave to the file.
     * @param ratingUserId the id of the user which had rated
     * @throws HttpBeanException is thrown, if there is an error while adding the rating.
     */
    public void addRatingForAccessingUser(int rating, long ratingUserId) throws HttpBeanException {
        IRatingDataHandler ratingDataHandler = getService(IRatingDataHandler.class);
        try {
            if (this.rating.hasUserRated(Long.parseLong(getAccessingUser().getId()))) {
                ratingDataHandler.updateRating(Long.parseLong(moduleId), Long.parseLong(getAccessingUser().getId()), rating);
                this.rating.addRating(rating, ratingUserId);
            } else {
                ratingDataHandler.addRating(Long.parseLong(moduleId), Long.parseLong(getAccessingUser().getId()), rating);
                this.rating.addRating(rating, ratingUserId);
            }
        } catch (DataHandlerException e) {
            throw new HttpBeanException(0, "Couldn't add rating"); //TODO @kunzlio1: ErrorsMessges => Constants
        }
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

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

}
