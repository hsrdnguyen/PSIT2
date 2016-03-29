package ch.avocado.share.service.Mock;

import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.Impl.FileStorageHandler;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorageHandlerMock extends FileStorageHandler implements IFileStorageHandler {

    private String tempDirectory;

    private void createTempDirectory() {
        Path path = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"));
        try {
            setTempDirectory(Files.createTempDirectory(path, "AvocadoShare").toAbsolutePath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(this.getClass().getName() + ": Temporary folder is: " + getTempDirectory());
    }

    public FileStorageHandlerMock() {
        createTempDirectory();
    }

    @Override
    public String getStoreDirectory() {
        return tempDirectory;
    }

    /**
     * @return The temporary directory where files are stored
     */
    public String getTempDirectory() {
        return tempDirectory;
    }

    /**
     * @param tempDirectory The temporary directory where files are stored
     */
    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }
}
