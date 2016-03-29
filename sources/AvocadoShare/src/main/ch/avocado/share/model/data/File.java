package ch.avocado.share.model.data;

import java.util.Date;
import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public class File extends AccessControlObjectBase {

    private String title;
    private String filename;
    private String path;
    private Date lastChanged;
    private String type;
    private String version;
    private String moduleId;

    public File(String id, List<Category> categories, Date creationDate, float rating, String ownerId, String description, String title, String filename, String path, Date lastChanged, String type, String version, String moduleId) {
        super(id, categories, creationDate, rating, ownerId, description);
        setFilename(filename);
        setLastChanged(lastChanged);
        setPath(path);
        setTitle(title);
        setType(type);
        setVersion(version);
        setModuleId(moduleId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) throw new IllegalArgumentException("title is null");
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        if (filename == null) throw new IllegalArgumentException("filename is null");
        this.filename = filename;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null) throw new IllegalArgumentException("type is null");
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (version == null) throw new IllegalArgumentException("version is null");
        this.version = version;
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
}
