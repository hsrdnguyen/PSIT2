package ch.avocado.share.model.data;

import java.util.*;

/**
 * File model.
 */
public class File extends AccessControlObjectBase {

    private String title;
    private Date lastChanged;
    //private String version;
    private String moduleId;
    private DiskFile diskFile;

    private File(String id, Collection<Category> categories, Date creationDate, float rating,
                 String ownerId, String description, String title, Date lastChanged,
                 String moduleId, DiskFile diskFile) {
        super(id, categories, creationDate, rating, ownerId, description);
        setLastChanged(lastChanged);
        setTitle(title);
        setModuleId(moduleId);
        setDirty(false);
        this.diskFile = diskFile;
    }

    public File(String id, List<Category> categories, Date creationDate, float rating,
                String ownerId, String description, String title, String path, Date lastChanged,
                String extension, String moduleId, String mimeType) {
        this(id, categories, creationDate, rating, ownerId, description, title, lastChanged, moduleId,
                new DiskFile(path, mimeType, extension));
    }

    public File(String ownerId, String description, String title, Date lastChanged, String moduleId, DiskFile diskFile) {
        this(null, new ArrayList<Category>(), new Date(), 0.0f, ownerId, description, title, lastChanged, moduleId, diskFile);
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) throw new IllegalArgumentException("title is null");
        if (title.isEmpty()) throw new IllegalArgumentException("title is empty");
        this.title = title;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        if (lastChanged == null) throw new IllegalArgumentException("lastChanged is null");
        this.lastChanged = lastChanged;
    }


    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        if (moduleId == null) throw new IllegalArgumentException("moduleId is null");
        if (moduleId.isEmpty()) throw new IllegalArgumentException("moduleId is empty");
        this.moduleId = moduleId;
    }

    @Override
    public String getReadableName() {
        return getTitle();
    }


    public void setDiskFile(DiskFile diskFile) {
        if (diskFile == null) throw new IllegalArgumentException("diskFile is null");
        if (!Objects.equals(this.diskFile, diskFile)) {
            this.diskFile = diskFile;
            setDirty(true);
        }
    }

    public String getExtension() {
        if(this.diskFile == null) throw new IllegalStateException("diskfile not added yet");
        return this.diskFile.getExtension();
    }

    public String getMimeType() {
        if(this.diskFile == null) throw new IllegalStateException("diskfile not added yet");
        return this.diskFile.getMimeType();
    }

    /**
     * The path is a string. It references a file stored on the disk
     * by {@link ch.avocado.share.service.Impl.FileStorageHandler}
     *
     * @return the path.
     */
    public String getPath() {
        if(this.diskFile == null) throw new IllegalStateException("diskfile not added yet");
        return this.diskFile.getPath();
    }
}
