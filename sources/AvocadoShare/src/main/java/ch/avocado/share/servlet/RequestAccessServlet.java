package ch.avocado.share.servlet;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.UrlHelper;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpServletException;
import ch.avocado.share.service.*;
import ch.avocado.share.service.exceptions.*;
import ch.avocado.share.servlet.resources.base.ExtendedHttpServlet;
import ch.avocado.share.servlet.resources.base.Parameter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/requestAccess")
public class RequestAccessServlet extends ExtendedHttpServlet {

    private AccessControlObjectBase getObject(String type, String id) throws HttpServletException {
        if (type == null) throw new NullPointerException("type is null");
        if (id == null) throw new NullPointerException("id is null");
        try {
            IModuleDataHandler moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);
            IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
            switch (type.toLowerCase()) {
                case "file":
                    return fileDataHandler.getFile(id);
                case "module":
                    return moduleDataHandler.getModule(id);
                case "group":
                    return groupDataHandler.getGroup(id);
            }
        } catch (ServiceException e) {
            throw new HttpServletException(e);
        }
        return null;
    }

    private User getOwner(AccessControlObjectBase object) throws HttpServletException {
        if (object == null) throw new NullPointerException("object is null");
        try {
            IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
            return userDataHandler.getUser(object.getOwnerId());
        } catch (ObjectNotFoundException e) {
            throw new HttpServletException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Besitzer konnte nicht gefunden werden.");
        } catch (ServiceException e) {
            throw new HttpServletException(e);
        }
    }


    private void checkAuthentication(UserSession session) throws HttpServletException {
        if (!session.isAuthenticated()) {
            throw new HttpServletException(HttpStatusCode.UNAUTHORIZED, ErrorMessageConstants.NOT_LOGGED_IN);
        }
    }

    @Override
    protected void doCreate(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpServletException, IOException {
        String id = parameter.getRequiredParameter("id");
        String type = parameter.getRequiredParameter("type");
        checkAuthentication(session);

        User requester = session.getUser();
        if (id == null) throw new NullPointerException("fileId is null");
        if (requester == null) throw new NullPointerException("requester is null");
        if (type == null) throw new NullPointerException("objectType is null");
        IMailingService mailingService;
        ISecurityHandler securityHandler;
        try {
            mailingService = ServiceLocator.getService(IMailingService.class);
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e1) {
            throw new HttpServletException(e1);
        }

        AccessControlObjectBase object;
        User owningUser;
        AccessLevelEnum requesterAccess;
        object = getObject(type, id);

        try {
            requesterAccess = securityHandler.getAccessLevel(requester, object);
        } catch (DataHandlerException e1) {
            throw new HttpServletException(e1);
        }
        owningUser = getOwner(object);
        if (requesterAccess.containsLevel(AccessLevelEnum.READ)) {
            String url = new UrlHelper(request).getUrlFor(object);
            response.sendRedirect(url);
            return;
        }
        try {
            mailingService.sendRequestAccessEmail(requester, owningUser, object);
        } catch (MailingServiceException e1) {
            throw new HttpServletException(e1);
        }

        request.setAttribute("success", true);
        response.setCharacterEncoding("UTF-8");
        try {
            request.getRequestDispatcher("/includes/request_access.jsp").include(request, response);
        } catch (ServletException e) {
            throw new HttpServletException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doView(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpServletException, IOException {
        String id = parameter.getRequiredParameter("id");
        String type = parameter.getRequiredParameter("type");
        checkAuthentication(session);
        AccessControlObjectBase object;

        object = getObject(type, id);

        if (object == null) {
            throw new HttpServletException(HttpStatusCode.NOT_FOUND, ErrorMessageConstants.OBJECT_NOT_FOUND);
        }

        if (session.getUser() == null) {
            throw new HttpServletException(HttpStatusCode.UNAUTHORIZED, ErrorMessageConstants.NOT_LOGGED_IN);
        }

        AccessLevelEnum requesterAccess;
        try {
            ISecurityHandler securityHandler = ServiceLocator.getService(ISecurityHandler.class);
            requesterAccess = securityHandler.getAccessLevel(session.getUser(), object);
        } catch (DataHandlerException | ServiceNotFoundException e) {
            throw new HttpServletException(e);
        }

        if (requesterAccess.containsLevel(AccessLevelEnum.READ)) {
            String url = new UrlHelper(request).getUrlFor(object);
            response.sendRedirect(url);
            return;
        }

        User owner = getOwner(object);
        request.setAttribute("owner", owner);
        request.setAttribute("object", object);
        request.setAttribute("type", type);
        response.setCharacterEncoding("UTF-8");
        try {
            request.getRequestDispatcher("/includes/request_access.jsp").include(request, response);
        } catch (ServletException e) {
            throw new HttpServletException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
