package ch.avocado.share.model.exceptions;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by coffeemakr on 03.04.16.
 */
public class HttpBeanDatabaseException extends HttpBeanException{
    public HttpBeanDatabaseException() {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.ERROR_DATABASE);
    }

}
