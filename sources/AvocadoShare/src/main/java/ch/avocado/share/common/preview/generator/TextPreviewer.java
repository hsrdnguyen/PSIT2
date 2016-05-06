package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.preview.IPreviewGenerator;

/**
 * Generates a preview for plain text encoded in UTF-8.
 */
public class TextPreviewer implements IPreviewGenerator {

    private final String content;

    private static final String HEAD = "<pre>";
    private static final String TAIL = "</pre>";

    public TextPreviewer(String content) {
        if(content == null) throw new NullPointerException("content is null");
        this.content = content;
    }

    @Override
    public String getPreview() {
        return HEAD + Encoder.forHtml(content) + TAIL;
    }
}
