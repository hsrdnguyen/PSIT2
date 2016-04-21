package ch.avocado.share.servlet;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/members")
public class MemberServlet extends HttpServlet {

    public static final String TARGET_ID = "targetId";
    public static final String OWNER_ID = "ownerId";
    public static final String ACCESS_LEVEL = "level";


    private User getAccessingUser(HttpServletRequest request) {
        return new UserSession(request).getUser();
    }

    private void setAccessLevel(User accessingUser, String ownerId, String targetId, String level) throws HttpBeanException, DataHandlerException {
        if (ownerId == null || ownerId.isEmpty() || targetId == null || targetId.isEmpty() || level == null || ownerId.equals(targetId)) {
            throw new HttpBeanException(HttpStatusCode.BAD_REQUEST, ErrorMessageConstants.MISSING_PARAMETER);
        }

        AccessLevelEnum accessLevel = getAccessLevelFromString(level);
        if(accessLevel == null) {
            throw new HttpBeanException(HttpStatusCode.BAD_REQUEST, ErrorMessageConstants.INVALID_REQUEST);
        }
        ISecurityHandler securityHandler;
        try {
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpStatusCode.INTERNAL_SERVER_ERROR,
                                        ErrorMessageConstants.SERVICE_NOT_FOUND + e.getService());
        }
        if(accessingUser == null) {
            throw new HttpBeanException(HttpStatusCode.UNAUTHORIZED, ErrorMessageConstants.NOT_LOGGED_IN);
        }
        if (!securityHandler.getAccessLevel(accessingUser.getId(), targetId).containsLevel(AccessLevelEnum.MANAGE)) {
            throw new HttpBeanException(HttpStatusCode.FORBIDDEN, ErrorMessageConstants.ACCESS_DENIED);
        }
        if(!securityHandler.setAccessLevel(ownerId, targetId, accessLevel)) {
            throw new HttpBeanException(HttpStatusCode.NOT_FOUND, ErrorMessageConstants.OBJECT_NOT_FOUND);
        }
    }

    private AccessLevelEnum getAccessLevelFromString(String levelAsString) {
        for(AccessLevelEnum level:  AccessLevelEnum.values()) {
            if(level.name().equalsIgnoreCase(levelAsString)) {
                return level;
            }
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String targetId = request.getParameter(TARGET_ID);
        String ownerId = request.getParameter(OWNER_ID);
        String level = request.getParameter(ACCESS_LEVEL);
        User accessingUser = getAccessingUser(request);
        try {
            setAccessLevel(accessingUser, targetId, ownerId, level);
        } catch (HttpBeanException e) {
            response.sendError(e.getStatusCode(), e.getDescription());
        } catch (DataHandlerException e) {
            e.printStackTrace();
            response.sendError(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), ErrorMessageConstants.DATAHANDLER_EXPCEPTION);
        }
    }
}
