package ch.avocado.share.common.form;

/**
 * Created by coffeemakr on 28.04.16.
 */
public enum FormEncoding {
    MULTIPART("multipart/form-data"), URLENCODED("application/x-www-form-urlencoded");

    private final String contentType;

    FormEncoding(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
