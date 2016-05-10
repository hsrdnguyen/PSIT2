package ch.avocado.share.servlet;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpServletException;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.servlet.resources.base.ExtendedHttpServlet;
import ch.avocado.share.servlet.resources.base.Parameter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/access/*")
public class AccessServlet extends ExtendedHttpServlet {

    String getIdentifierFromRequest(HttpServletRequest request) throws HttpServletException {
        System.out.println(request.getRequestURI());
        String uri = request.getRequestURI();
        if(!uri.contains("access/")) {
            throw new HttpServletException(HttpStatusCode.BAD_REQUEST, ErrorMessageConstants.MISSING_PARAMETER);
        }
        return uri.split("access/", 2)[1];
    }

    @Override
    protected void doView(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpServletException, IOException {
        ISecurityHandler securityHandler;
        try {
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpServletException(e);
        }
        String id = getIdentifierFromRequest(request);
        long objectIdAsLong;
        try {
            objectIdAsLong = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new HttpServletException(HttpStatusCode.BAD_REQUEST, ErrorMessageConstants.INVALID_ID);
        }
        AccessLevelEnum accessLevel;
        try {
            accessLevel = securityHandler.getAccessLevel(session.getUserId(), id);
        } catch (DataHandlerException e) {
            throw new HttpServletException(e);
        }

        String jsonResponse = "{\"id\": \"" + objectIdAsLong + "\", \"access\": \"" + accessLevel + "\"}";
        response.setContentType("application/json");
        response.getWriter().print(jsonResponse);
    }
}
