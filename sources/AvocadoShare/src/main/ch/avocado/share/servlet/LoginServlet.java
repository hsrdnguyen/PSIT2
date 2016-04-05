/**
 *
 */
package ch.avocado.share.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

/**
 * @author coffeemakr
 */

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    static final String LOGIN_FORM_URL = "/includes/login_form.jsp";

    static final public String LOGIN_ERROR = "login_error";
    static final public String FIELD_EMAIL = "email";
    static final public String FIELD_PASSWORD = "password";
    static final public String FIELD_REDIRECT_TO = "redirect_to";

    static private final String COOKIE_CHECK_NAME = "are_cookies_enabled";
    static private final String COOKIE_CHECK_VALUE = "yes! :)";

    private static final long serialVersionUID = 5348852043943606854L;


    private User getUserWithLogin(IUserDataHandler userDataHandler, String email, String password) throws DataHandlerException {
        User user = userDataHandler.getUserByEmailAddress(email);
        if (user != null && user.getPassword().matchesPassword(password)) {
            return user;
        }
        return null;
    }

    private void renderLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(LOGIN_FORM_URL);
        dispatcher.forward(request, response);
    }


    private void addTestCookie(HttpServletResponse response) {
        Cookie checkCookie = new Cookie(COOKIE_CHECK_NAME, COOKIE_CHECK_VALUE);
        response.addCookie(checkCookie);
    }

    public boolean checkTestCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            if (name.equals(COOKIE_CHECK_NAME) && value.equals(COOKIE_CHECK_VALUE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        addTestCookie(response);
        renderLogin(request, response);
    }

    private void redirectTo(String url, HttpServletResponse response) {
        if(url == null) throw  new IllegalArgumentException("url is null");
        if(response == null) throw new IllegalArgumentException("response is null");
        if(url.isEmpty()) {
            url = "/";
        }
        if(url.charAt(0) == '/') {
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        String email = request.getParameter(FIELD_EMAIL);
        String password = request.getParameter(FIELD_PASSWORD);
        IUserDataHandler userDataHandler = null;
        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        } catch (ServiceNotFoundException e) {
            request.setAttribute(LOGIN_ERROR, ErrorMessageConstants.ERROR_SECURITY_HANDLER);
            renderLogin(request, response);
        }
        if (userDataHandler != null) {
            if (email == null) {
                request.setAttribute(LOGIN_ERROR, ErrorMessageConstants.ERROR_NO_EMAIL);
                renderLogin(request, response);
            } else if (password == null) {
                request.setAttribute(LOGIN_ERROR, ErrorMessageConstants.ERROR_NO_PASSWORD);
                renderLogin(request, response);
            } else {
                if (!checkTestCookie(request)) {
                    request.setAttribute(LOGIN_ERROR, ErrorMessageConstants.ERROR_COOKIES_DISABLED);
                }
                User user = null;
                try {
                    user = getUserWithLogin(userDataHandler, email, password);
                } catch (DataHandlerException e) {
                    request.setAttribute(LOGIN_ERROR, ErrorMessageConstants.ERROR_DATABASE);
                    renderLogin(request, response);
                    return;
                }
                if (user != null) {
                    if(user.getMail().isVerified()) {
                        UserSession userSession = new UserSession(request);
                        userSession.authenticate(user);
                        String redirectUrl= request.getParameter(FIELD_REDIRECT_TO);
                        if(redirectUrl == null) {
                            redirectUrl = request.getContextPath();
                        }
                        redirectTo(redirectUrl, response);
                    }else {
                        request.setAttribute(LOGIN_ERROR, ErrorMessageConstants.ERROR_EMAIL_NOT_VERIFIED);
                    }
                } else {
                    request.setAttribute(LOGIN_ERROR, ErrorMessageConstants.ERROR_WRONG_PASSWORD);
                    renderLogin(request, response);
                }
            }
        }
    }
}
