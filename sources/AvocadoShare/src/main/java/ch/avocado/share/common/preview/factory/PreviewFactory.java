package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.model.data.File;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.servlet.DownloadServlet;

/**
 * Base class for all preview factories
 */
public abstract class PreviewFactory {

    public abstract IPreviewGenerator getInstance(File file) throws PreviewException;

    /**
     * @param file
     * @return The link to stream the file. Can be used e.g. for a video element.
     * @throws PreviewException
     */
    protected String getStreamUrl(File file) throws PreviewException {
        if(file == null) throw new NullPointerException("file is null");

        return DownloadServlet.getStreamUrl(file) + "&cache=" + file.getPath();

    }

    IFileStorageHandler getFileStorageHandler() throws PreviewException {
        IFileStorageHandler fileStorageHandler;
        try{
            fileStorageHandler = ServiceLocator.getService(IFileStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new PreviewException(ErrorMessageConstants.SERVICE_NOT_FOUND + e.getService());
        }
        return fileStorageHandler;
    }
}
