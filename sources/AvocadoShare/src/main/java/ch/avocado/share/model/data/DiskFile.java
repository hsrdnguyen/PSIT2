package ch.avocado.share.model.data;

/**
 * This class describes a "real" disk file.
 * Objects of this class are immutable.
 */
public class DiskFile {
    private final String extension;
    private final String mimeType;
    private final String path;

    public DiskFile(String path, String mimeType, String extension) {
        if(path == null) throw new IllegalArgumentException("path is null");
        if(path.isEmpty()) throw new IllegalArgumentException("path is empty");
        if(mimeType == null) throw new IllegalArgumentException("mimetype is null");
        if(mimeType.isEmpty()) throw new IllegalArgumentException("mimetype is empty");
        if(extension == null) throw new IllegalArgumentException("extension is null");
        this.path = path;
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiskFile diskFile = (DiskFile) o;

        if (!extension.equals(diskFile.extension)) return false;
        if (!mimeType.equals(diskFile.mimeType)) return false;
        return path.equals(diskFile.path);

    }

    @Override
    public int hashCode() {
        int result = extension.hashCode();
        result = 31 * result + mimeType.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
