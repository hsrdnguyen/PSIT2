package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.common.preview.generator.VideoPreviewer;
import ch.avocado.share.model.data.File;

/**
 * Created by coffeemakr on 30.03.16.
 */
public class VideoPreviewerFactory extends PreviewFactory {

    @Override
    public IPreviewGenerator getInstance(File file) throws PreviewException {
        String url = getDownloadUrl(file);
        String mimeType = getMimeType(file);
        return new VideoPreviewer(mimeType, url);
    }
}
