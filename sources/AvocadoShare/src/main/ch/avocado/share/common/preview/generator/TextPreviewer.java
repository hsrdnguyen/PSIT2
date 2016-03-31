package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.preview.IPreviewGenerator;


public class TextPreviewer implements IPreviewGenerator {

    private final String content;

    private static final String BEFORE = "<pre>";
    private static final String AFTER = "</pre>";

    public TextPreviewer(String content) {
        this.content = content;
    }

    @Override
    public String getPreview() {
        StringBuffer buffer = new StringBuffer(content.length() + BEFORE.length() + AFTER.length());
        buffer.append(BEFORE);
        buffer.append(Encoder.forHtml(content));
        buffer.append(AFTER);
        return buffer.toString();
    }
}
