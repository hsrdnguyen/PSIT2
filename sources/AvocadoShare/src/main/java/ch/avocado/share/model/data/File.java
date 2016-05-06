package ch.avocado.share.model.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * File model.
 */
public class File extends AccessControlObjectBase {

    private String title;
    private String path;
    private Date lastChanged;
    private String extension;
    private String mimeType;
    //private String version;
    private String moduleId;

    public File(String id, List<Category> categories, Date creationDate, float rating, String ownerId, String description, String title, String path, Date lastChanged, String extension, String moduleId, String mimeType) {
        super(id, categories, creationDate, rating, ownerId, description);
        setLastChanged(lastChanged);
        setPath(path);
        setTitle(title);
        setExtension(extension);
        //setVersion(version);
        setModuleId(moduleId);
        setMimeType(mimeType);
    }

    public File(String ownerId, String description, String title, String path, Date lastChanged, String extension, String moduleId, String mimeType) {
        this(null, new ArrayList<Category>(), new Date(), 0.0f, ownerId, description, title, path, lastChanged, extension, moduleId, mimeType);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) throw new IllegalArgumentException("title is null");
        this.title = title;
    }

    /**
     * The path is a string. It references a file stored on the disk
     * by {@link ch.avocado.share.service.Impl.FileStorageHandler}
     * @return the path.
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (path == null) throw new IllegalArgumentException("path is null");
        this.path = path;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        if (lastChanged == null) throw new IllegalArgumentException("lastChanged is null");
        this.lastChanged = lastChanged;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        if (extension == null) throw new IllegalArgumentException("extension is null");
        this.extension = extension;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        if(moduleId == null) {
            throw new IllegalArgumentException("moduleId is null");
        }
        this.moduleId = moduleId;
    }

    @Override
    public String getReadableName() {
        return getTitle();
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
