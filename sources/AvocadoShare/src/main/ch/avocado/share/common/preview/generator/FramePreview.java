package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.form.FormBuilder;
import ch.avocado.share.common.preview.IPreviewGenerator;

/**
 * Generates a preview by using the {@link #streamUrl} to create an iframe.
 * This can be used to display e.g. PDF-Documents which can be rendered by
 * the browser.
 */
public class FramePreview implements IPreviewGenerator {

    private final String streamUrl;

    /**
     * Constructor
     * @param streamUrl The url from which the content can be streamed.
     */
    public FramePreview(String streamUrl) {
        if(streamUrl == null) throw new IllegalArgumentException("streamUrl is null");
        this.streamUrl = streamUrl;
    }

    @Override
    public String getPreview() {
        return "<iframe " + FormBuilder.formatAttribute("src", streamUrl) + "></iframe>";
    }
}
