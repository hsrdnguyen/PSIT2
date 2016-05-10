/**
 *
 */
package ch.avocado.share.servlet;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author coffeemakr
 */

@WebServlet(value = "/login", name = "Login")
public class LoginServlet extends HttpServlet {

    static final private String LOGIN_FORM_URL = "/includes/login_form.jsp";

    static final public String LOGIN_ERROR = "login_error";
    static final public String FIELD_EMAIL = "email";
    static final public String FIELD_PASSWORD = "password";
    static final public String FIELD_REDIRECT_TO = "redirect_to";

    static private final String COOKIE_CHECK_NAME = "are_cookies_enabled";
    static private final String COOKIE_CHECK_VALUE = "yes! :)";

    private static final long serialVersionUID = 5348852043943606854L;
    public static final String ATTRIBUTE_EMAIL = "ch.avocado.share.servlet.email";


    private User getUserWithLogin(IUserDataHandler userDataHandler, String email, String password) throws DataHandlerException {
        User user = null;
        try {
            user = userDataHandler.getUserByEmailAddress(email);
        } catch (ObjectNotFoundException e) {
            return null;
        }
        if (user.getPassword().matchesPassword(password)) {
            return user;
        }
        return null;
    }

    private void renderLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(LOGIN_FORM_URL);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        renderLogin(request, response);
    }


    private String getValidRedirectUrl(String url) {
        if(url.isEmpty()) {
            url = "/";
        }
        if(url.charAt(0) == '/') {
            return url;
        }
        return null;
    }

    private void redirectIfUrlIsValid(String url, HttpServletResponse response) {
        if(url == null) throw  new IllegalArgumentException("url is null");
        if(response == null) throw new NullPointerException("response is null");
        url = getValidRedirectUrl(url);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loginAndGetError(HttpServletRequest request, String email, String password) {
        IUserDataHandler userDataHandler;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
            return ErrorMessageConstants.SERVICE_NOT_FOUND + e.getService();
        }
        if (email == null) {
            return ErrorMessageConstants.ERROR_NO_EMAIL;
        } else if (password == null) {
            return ErrorMessageConstants.ERROR_NO_PASSWORD;
        } else {
            User user = null;
            try {
                user = getUserWithLogin(userDataHandler, email, password);
            } catch (DataHandlerException e) {
                e.printStackTrace();
                return ErrorMessageConstants.DATAHANDLER_EXPCEPTION;
            }
            if (user != null) {
                if(user.getMail().isVerified()) {
                    UserSession userSession = new UserSession(request);
                    userSession.authenticate(user);
                    return null;
                }else {
                    return ErrorMessageConstants.ERROR_EMAIL_NOT_VERIFIED;
                }
            } else {
                return ErrorMessageConstants.ERROR_WRONG_PASSWORD;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        String email = request.getParameter(FIELD_EMAIL);
        String password = request.getParameter(FIELD_PASSWORD);
        String redirectUrl = request.getParameter(FIELD_REDIRECT_TO);

        if(email != null) {
            request.setAttribute(ATTRIBUTE_EMAIL, email);
        }
        String error = loginAndGetError(request, email, password);
        if(error != null) {
            request.setAttribute(LOGIN_ERROR, error);
            renderLogin(request, response);
        } else {
            if(redirectUrl == null) {
                redirectUrl = request.getServletContext().getContextPath();
            }
            redirectIfUrlIsValid(redirectUrl, response);
        }
    }
}
