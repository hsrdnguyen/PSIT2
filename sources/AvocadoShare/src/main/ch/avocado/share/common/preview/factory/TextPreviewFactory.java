package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.common.preview.generator.TextPreviewer;
import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.exceptions.FileStorageException;
import org.bouncycastle.util.io.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TextPreviewFactory extends PreviewFactory{

    static int MAX_FILE_SIZE = 1024 * 1024;

    String readStream(InputStream stream) throws PreviewException {
        try {
            return new String(Streams.readAllLimited(stream, MAX_FILE_SIZE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PreviewException(e.getMessage());
        }
    }

    @Override
    public IPreviewGenerator getInstance(File file) throws PreviewException {
        IFileStorageHandler storageHandler = getFileStorageHandler();
        InputStream stream = null;
        long fileSize;
        try {
            fileSize = storageHandler.getFileSize(file.getPath());
            if(fileSize > MAX_FILE_SIZE) {
                throw new PreviewException("Die Datei ist zu gross.");
            }
            stream = storageHandler.readFile(file.getPath());
        } catch (FileStorageException e) {
            throw new PreviewException(e.getMessage());
        }
        String content = readStream(stream);
        return new TextPreviewer(content);
    }
}
