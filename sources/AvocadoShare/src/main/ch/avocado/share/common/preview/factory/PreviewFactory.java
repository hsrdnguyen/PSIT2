package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.exceptions.FileStorageException;

import java.io.UnsupportedEncodingException;

public abstract class PreviewFactory {

    public abstract IPreviewGenerator getInstance(File file) throws PreviewException;

    protected String getDownloadUrl(File file) throws PreviewException {
        // TODO: fix link
        try {
            return "/download?id=" + Encoder.forUrl(file.getId());
        } catch (UnsupportedEncodingException e) {
            throw new PreviewException("Failed to encode an URL");
        }
    }

    protected String getMimeType(File file) throws PreviewException {
        try {
            return getFileStorageHandler().getContentType(file.getPath());
        } catch (FileStorageException e) {
            throw new PreviewException(e.getMessage());
        }
    }

    IFileStorageHandler getFileStorageHandler() throws PreviewException {
        IFileStorageHandler fileStorageHandler;
        try{
            fileStorageHandler = ServiceLocator.getService(IFileStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new PreviewException(e.getMessage());
        }
        return fileStorageHandler;
    }
}
