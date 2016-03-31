package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;

/**
 * Created by coffeemakr on 31.03.16.
 */
public class PreviewerExceptionPreviewer implements IPreviewGenerator {

    private final PreviewException exception;

    public PreviewerExceptionPreviewer(PreviewException exception) {
        if(exception == null) throw new IllegalArgumentException("exception is null");
        this.exception = exception;
    }

    @Override
    public String getPreview() {
        return "<div class=\"alert alert-danger\">" + Encoder.forHtml(exception.getMessage()) + "</div>";
    }
}
