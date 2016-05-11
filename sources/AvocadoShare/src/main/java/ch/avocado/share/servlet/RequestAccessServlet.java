package ch.avocado.share.servlet;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
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

    private AccessControlObjectBase getObject(String type, String id) throws ServiceNotFoundException, DataHandlerException, ObjectNotFoundException {
        if (type == null) throw new NullPointerException("type is null");
        if (id == null) throw new NullPointerException("id is null");
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
        return null;
    }

    private User getOwner(AccessControlObjectBase object) throws HttpServletException {
        if(object == null) throw new NullPointerException("object is null");
        try {
            IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
            return userDataHandler.getUser(object.getOwnerId());
        } catch (ObjectNotFoundException e) {
            throw new HttpServletException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Besitzer konnte nicht gefunden werden.");
        } catch (ServiceException e) {
            throw new HttpServletException(e);
        }
    }

    /**
     * @return
     */
    public boolean requestAccess(String fileId, User requester, String objectType) throws DataHandlerException, ObjectNotFoundException, ServiceNotFoundException, HttpServletException, MailingServiceException {
        if (fileId == null) throw new NullPointerException("fileId is null");
        if (requester == null) throw new NullPointerException("requester is null");
        if (objectType == null) throw new NullPointerException("objectType is null");
        IMailingService mailingService;
        ISecurityHandler securityHandler;
        try {
            mailingService = ServiceLocator.getService(IMailingService.class);
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        AccessControlObjectBase object;
        User owningUser;
        AccessLevelEnum currentLevel;
        object = getObject(objectType, fileId);
        if (object == null) return false; // unknown type
        if (object.getOwnerId() == null) {
            return false;
        }
        currentLevel = securityHandler.getAccessLevel(requester, object);
        owningUser = getOwner(object);
        if (currentLevel.containsLevel(AccessLevelEnum.READ)) {
            return true;
        }
        return mailingService.sendRequestAccessEmail(requester, owningUser, object);
    }


    @Override
    protected void doCreate(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpServletException, IOException {
        String id = parameter.getRequiredParameter("id");
        String type = parameter.getRequiredParameter("type");
        try {
            requestAccess(id, session.getUser(), type);
        } catch (DataHandlerException | ObjectNotFoundException | MailingServiceException | ServiceNotFoundException e) {
            throw new HttpServletException(e);
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
        AccessControlObjectBase object;
        try {
            object = getObject(type, id);
        } catch (ServiceNotFoundException | DataHandlerException | ObjectNotFoundException e) {
            throw new HttpServletException(e);
        }
        if (object == null) {
            throw new HttpServletException(HttpStatusCode.NOT_FOUND, ErrorMessageConstants.OBJECT_NOT_FOUND);
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
