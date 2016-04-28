package ch.avocado.share.service.Impl;

import ch.avocado.share.common.HexEncoder;
import ch.avocado.share.common.constants.FileConstants;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.exceptions.FileStorageException;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bergm on 22/03/2016.
 */
public class FileStorageHandler implements IFileStorageHandler {

    private final int FILE_READ_BUFFER_SIZE = 512;
    private final Tika tika;

    public FileStorageHandler() {
        tika = new Tika();
    }

    @Override
    public String getContentType(String filepath, String name) throws FileStorageException {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, name);
        metadata.set(Metadata.CONTENT_LENGTH, Long.toString(getFileSize(filepath)));
        InputStream inputStream = readFile(filepath);
        InputStream bufferedIn = new BufferedInputStream(inputStream);
        try {
            return tika.detect(bufferedIn, metadata);
        } catch (IOException e) {
            throw new FileStorageException("Unable to determinate type: " + e.getMessage());
        }
    }

    @Override
    public long getFileSize(String reference) {
        File file = getFileByReference(reference);
        return file.length();
    }

    /**
     * @param inputStream The input stream to the file. The input stream will be closed after calling this method.
     * @return The hash of the file.
     * @throws FileStorageException
     */
    private String createFileHashName(InputStream inputStream) throws FileStorageException {
        byte[] buffer = new byte[FILE_READ_BUFFER_SIZE];
        int readBytes = 0;
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new FileStorageException("SHA-256 digest doesn't exist");
        }
        try {
            while (readBytes >= 0) {
                readBytes = inputStream.read(buffer);
                if (readBytes >= 0) {
                    messageDigest.update(buffer, 0, readBytes);
                }
            }
        } catch (IOException e) {
            throw new FileStorageException("Unable to hash file: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
        // finalize digest (padding etc.)
        byte[] digest = messageDigest.digest();
        // Convert to string
        return HexEncoder.bytesToHex(digest);
    }


    /**
     * @return The directory in which files can be stored.
     */
    protected String getStoreDirectory() {
        return FileConstants.FILE_DESTINATION_ON_SERVER;
    }

    @Override
    public String saveFile(FileItem tempUploadedFile) throws FileStorageException {
        if (tempUploadedFile == null) throw new IllegalArgumentException("tempUploadedFile is null");
        if (tempUploadedFile.isFormField()) {
            throw new IllegalArgumentException("tempUploadedFile isFormField and not an unploaded file");
        }
        File storedFile;
        String filename;
        try {
            InputStream inputStream = tempUploadedFile.getInputStream();
            filename = createFileHashName(inputStream);
            storedFile = new File(getStoreDirectory(), filename);
            if (!storedFile.exists()) {
                tempUploadedFile.write(storedFile);
            }
        } catch (Exception e) {
            throw new FileStorageException("Failed to store uploaded file: " + e.getMessage());
        }
        return filename;
    }

    @Override
    public String saveFile(File fileToSave) throws FileStorageException {
        String filename;
        try {
            InputStream inputStream = new FileInputStream(fileToSave);
            filename = createFileHashName(inputStream);
            inputStream = new FileInputStream(fileToSave);
            File outputFile = new File(getStoreDirectory(), filename);
            if (!outputFile.exists()) {
                try {
                    try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                        IOUtils.copy(inputStream, outputStream);
                    }
                } catch (Exception e){
                    //noinspection ResultOfMethodCallIgnored
                    outputFile.delete();
                    throw e;
                }
            }
        } catch (IOException e) {
            throw new FileStorageException(e.getMessage());
        }
        return filename;
    }


    /**
     * @param reference Reference to the file
     * @return A file object
     */
    private File getFileByReference(String reference) {
        return new File(getStoreDirectory(), reference);
    }

    @Override
    public InputStream readFile(String reference) throws FileStorageException {
        File file = getFileByReference(reference);
        InputStream stream = null;
        if (file.exists() && file.canRead()) {
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException ignored) {
            }
        }
        if (stream == null) {
            throw new FileStorageException("File " + reference + " doesn't exist");
        }
        return stream;
    }

    @Override
    public boolean fileExists(String reference) {
        File file = getFileByReference(reference);
        return file.exists();
    }
}
