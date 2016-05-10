package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.preview.IPreviewGenerator;

public class VideoPreviewer implements IPreviewGenerator {

    private static final String VIDEO_TAG = "<video controls src=\"%s\">" + ErrorMessageConstants.ERROR_VIDEO_TYPE_NOT_SUPPORTED_IN_BROWSER + "</video>";

    private String streamUrl;

    public VideoPreviewer(String streamUrl) {
        if(streamUrl == null) throw new NullPointerException("streamUrl is null");
        this.streamUrl = streamUrl;
    }

    @Override
    public String getPreview() {
        return String.format(VIDEO_TAG, Encoder.forHtmlAttribute(this.streamUrl));
    }
}
