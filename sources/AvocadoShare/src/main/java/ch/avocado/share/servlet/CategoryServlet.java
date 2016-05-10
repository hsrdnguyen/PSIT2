package ch.avocado.share.servlet;

import ch.avocado.share.common.ResponseHelper;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpServletException;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.ServiceException;
import ch.avocado.share.servlet.resources.base.ExtendedHttpServlet;
import ch.avocado.share.servlet.resources.base.Parameter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ch.avocado.share.common.HttpStatusCode.*;
import static ch.avocado.share.common.constants.ErrorMessageConstants.*;


@WebServlet("/category")
public class CategoryServlet extends ExtendedHttpServlet {

    public static final String PARAMETER_ID = "id";
    public static final String PARAMETER_CATEGORY = "category";

    private void ensureHasWriteAccess(User user, File file) throws HttpServletException {
        if(user == null) throw new NullPointerException("user is null");
        if(file == null) throw new NullPointerException("file is null");
        ISecurityHandler securityHandler;
        AccessLevelEnum level;
        try {
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
            level = securityHandler.getAccessLevel(user, file);
        } catch (ServiceException e) {
            throw new HttpServletException(e);
        }
        if(!level.containsLevel(AccessLevelEnum.WRITE)) {
            throw new HttpServletException(FORBIDDEN, NO_RIGHTS_TO_ADD_CATEGORY);
        }
    }

    private void ensureRequesterHasWriteAccess(UserSession session, File file) throws HttpServletException {
        User accessingUser = session.getUser();
        if(accessingUser == null) {
            throw new HttpServletException(UNAUTHORIZED, NOT_LOGGED_IN);
        }
        ensureHasWriteAccess(session.getUser(), file);
    }

    private File getFileFromRequestParameter(Parameter parameter) throws HttpServletException {
        String identifier = parameter.getRequiredParameter(PARAMETER_ID);
        try {
            IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            return fileDataHandler.getFile(identifier);
        } catch (ServiceException e) {
            throw new HttpServletException(e);
        }
    }

    private void updateFile(File file) throws HttpServletException {
        try {
            ServiceLocator.getService(IFileDataHandler.class).updateFile(file);
        } catch (ServiceException e) {
            throw new HttpServletException(e);
        }
    }

    private Category getCategoryFromRequestParameter(Parameter parameter) throws HttpServletException {
        String categoryName = parameter.getRequiredParameter(PARAMETER_CATEGORY);
        if(categoryName == null) throw new HttpServletException(BAD_REQUEST, MISSING_PARAMETER);
        return new Category(categoryName);
    }


    @Override
    protected void doCreate(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws IOException, HttpServletException {
        File file = getFileFromRequestParameter(parameter);
        Category category = getCategoryFromRequestParameter(parameter);
        if(file.getCategoryList().contains(category)) {
            // Nothing to do
            return;
        }
        file.getCategoryList().add(category);
        updateFile(file);
        ResponseHelper.redirectToOrigin(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws IOException, HttpServletException {
        File file = getFileFromRequestParameter(parameter);
        ensureRequesterHasWriteAccess(session, file);
        Category category = getCategoryFromRequestParameter(parameter);
        if(!file.getCategoryList().contains(category)) {
            // Nothing to do
            return;
        }
        file.getCategoryList().remove(category);
        updateFile(file);
        ResponseHelper.redirectToOrigin(request, response);
    }
}
