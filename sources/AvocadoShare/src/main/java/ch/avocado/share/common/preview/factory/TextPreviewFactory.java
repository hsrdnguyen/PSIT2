package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.constants.ErrorMessageConstants;
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

/**
 * Factory for {@link TextPreviewer}
 */
public class TextPreviewFactory extends PreviewFactory{

    private static final int MAX_FILE_SIZE = 1024 * 1024;

    private String readStream(InputStream stream) throws PreviewException {
        if(stream == null) throw new IllegalArgumentException("stream is null");
        try {
            return new String(Streams.readAllLimited(stream, MAX_FILE_SIZE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PreviewException(ErrorMessageConstants.ERROR_READ_FILE_FAILED);
        }
    }

    @Override
    public IPreviewGenerator getInstance(File file) throws PreviewException {
        if(file == null) throw new IllegalArgumentException("file is null");
        IFileStorageHandler storageHandler = getFileStorageHandler();
        InputStream stream;
        long fileSize;
        try {
            fileSize = storageHandler.getFileSize(file.getPath());
            if(fileSize > MAX_FILE_SIZE) {
                throw new PreviewException(ErrorMessageConstants.ERROR_FILE_TO_BIG_FOR_PREVIEW);
            }
            stream = storageHandler.readFile(file.getPath());
        } catch (FileStorageException e) {
            throw new PreviewException(e.getMessage());
        }
        String content = readStream(stream);
        return new TextPreviewer(content);
    }
}
