package ch.avocado.share.model.data;

/**
 * This class describes a "real" disk file.
 * Objects of this class are immutable.
 */
public class DiskFile {
    private final String extension;
    private final String mimeType;
    private final String path;

    /**
     * Create a new diskfile
     * @param path The path to the file
     * @param mimeType The mime type of the file
     * @param extension The extension of the file without a leading dot.
     */
    public DiskFile(String path, String mimeType, String extension) {
        if(path == null) throw new NullPointerException("path is null");
        if(path.isEmpty()) throw new IllegalArgumentException("path is empty");
        if(mimeType == null) throw new NullPointerException("mimetype is null");
        if(mimeType.isEmpty()) throw new IllegalArgumentException("mimetype is empty");
        if(extension == null) throw new NullPointerException("extension is null");
        this.path = path;
        this.mimeType = mimeType;
        this.extension = extension;
    }

    /**
     * @return The extension without the leading dot (e.g. "png")
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return The mime-type. (e.g. "image/png")
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return A reference to the path of the of the file
     */
    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        DiskFile diskFile = (DiskFile) other;

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
