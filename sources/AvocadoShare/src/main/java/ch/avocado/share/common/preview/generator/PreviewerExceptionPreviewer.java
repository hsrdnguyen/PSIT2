package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;

/**
 * Created by coffeemakr on 31.03.16.
 */
public class PreviewerExceptionPreviewer implements IPreviewGenerator {

    private static final String HEAD = "<div class=\"alert alert-danger\">";
    private static final String TAIL = "</div>";
    private final PreviewException exception;

    public PreviewerExceptionPreviewer(PreviewException exception) {
        if(exception == null) throw new IllegalArgumentException("exception is null");
        this.exception = exception;
    }

    @Override
    public String getPreview() {
        return HEAD + Encoder.forHtml(exception.getMessage()) + TAIL;
    }
}
