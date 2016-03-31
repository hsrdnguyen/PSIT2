package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.preview.IPreviewGenerator;

public class VideoPreviewer implements IPreviewGenerator {

    private static final String VIDEO_TAG = "<video><source src=\"%s\" type=\"%s\">" + ErrorMessageConstants.ERROR_VIDEO_TYPE_NOT_SUPPORTED_IN_BROWSER + "</video>";

    private String streamUrl;
    private String contentType;

    public VideoPreviewer(String streamUrl, String contentType) {
        this.streamUrl = streamUrl;
        this.contentType = contentType;
    }

    @Override
    public String getPreview() {
        return String.format(VIDEO_TAG, Encoder.forHtmlAttribute(this.streamUrl), Encoder.forHtmlAttribute(this.contentType));
    }
}
