package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.common.preview.generator.VideoPreviewer;
import ch.avocado.share.model.data.File;

/**
 * Factory for {@link VideoPreviewer}
 */
public class VideoPreviewerFactory extends PreviewFactory {
    @Override
    public IPreviewGenerator getInstance(File file) throws PreviewException {
        if(file == null) throw new IllegalArgumentException("file is null");
        String url = getStreamUrl(file);
        String mimeType = getMimeType(file);
        return new VideoPreviewer(mimeType, url);
    }
}
