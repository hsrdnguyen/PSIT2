package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.common.preview.generator.ImagePreviewer;
import ch.avocado.share.model.data.File;

/**
 * Created by coffeemakr on 23.04.16.
 */
public class ImagePreviewerFactory extends PreviewFactory{

    @Override
    public IPreviewGenerator getInstance(File file) throws PreviewException {
        return new ImagePreviewer(getStreamUrl(file));
    }
}
