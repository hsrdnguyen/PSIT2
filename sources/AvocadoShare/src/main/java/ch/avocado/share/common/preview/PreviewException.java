package ch.avocado.share.common.preview;

/**
 * Exception thrown by {@link ch.avocado.share.common.preview.factory.PreviewFactory}.
 */
public class PreviewException extends Exception {
    /**
     * @param message The message to display
     */
    public PreviewException(String message)  {
        super(message);
    }
}
