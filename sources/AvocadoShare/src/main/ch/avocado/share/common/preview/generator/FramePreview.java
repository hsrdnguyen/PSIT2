package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.preview.IPreviewGenerator;

public class FramePreview implements IPreviewGenerator {

    private final String streamUrl;

    public FramePreview(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    @Override
    public String getPreview() {
        return "<iframe src=\"" + Encoder.forHtmlAttribute(streamUrl) + "\"></iframe>";
    }
}
