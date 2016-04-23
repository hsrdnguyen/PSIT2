package ch.avocado.share.common.preview.generator;

import ch.avocado.share.common.preview.IPreviewGenerator;

public class ImagePreviewer implements IPreviewGenerator {

    private String streamUrl;

    public ImagePreviewer(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    @Override
    public String getPreview() {
        return "<img src=\"" + streamUrl + "\" alt=\"Bildvorschau\" />";
    }
}
