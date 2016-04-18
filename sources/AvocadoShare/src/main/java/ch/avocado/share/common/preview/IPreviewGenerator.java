package ch.avocado.share.common.preview;

/**
 * Objects of this interface are able to generate a preview of something.
 */
public interface IPreviewGenerator {
    /**
     * @return HTML-Code to display the preview.
     */
    String getPreview();
}
