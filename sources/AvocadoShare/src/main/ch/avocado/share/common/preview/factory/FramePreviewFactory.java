package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.common.preview.generator.FramePreview;
import ch.avocado.share.model.data.File;


public class FramePreviewFactory extends PreviewFactory{
    @Override
    public IPreviewGenerator getInstance(File file) throws PreviewException {
        return new FramePreview(getDownloadUrl(file));
    }
}
