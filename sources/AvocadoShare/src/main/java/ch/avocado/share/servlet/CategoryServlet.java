package ch.avocado.share.servlet;

import ch.avocado.share.common.ResponseHelper;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
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

    protected static final String PARAMETER_ID = "id";

    private void ensureHasWriteAccess(User user, File file) throws HttpBeanException {
        if(user == null) throw new IllegalArgumentException("user is null");
        if(file == null) throw new IllegalArgumentException("file is null");
        ISecurityHandler securityHandler;
        AccessLevelEnum level;
        try {
            securityHandler = getService(ISecurityHandler.class);
            level = securityHandler.getAccessLevel(user, file);
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanException(INTERNAL_SERVER_ERROR, DATAHANDLER_EXPCEPTION);
        }
        if(!level.containsLevel(AccessLevelEnum.WRITE)) {
            throw new HttpBeanException(FORBIDDEN, NO_RIGHTS_TO_ADD_CATEGORY);
        }
    }

    private void ensureRequesterHasWriteAccess(UserSession session, File file) throws HttpBeanException {
        User accessingUser = session.getUser();
        if(accessingUser == null) {
            throw new HttpBeanException(UNAUTHORIZED, NOT_LOGGED_IN);
        }
        ensureHasWriteAccess(session.getUser(), file);
    }

    private File getFileFromRequestParameter(Parameter parameter) throws HttpBeanException{
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        String identifier = parameter.getRequiredParameter(PARAMETER_ID);
        File file;
        try {
            file = fileDataHandler.getFile(identifier);
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanException(INTERNAL_SERVER_ERROR, DATAHANDLER_EXPCEPTION);
        }
        return file;
    }

    private <E> E getService(Class<E> clazz) throws HttpBeanException {
        try {
            return ServiceLocator.getService(clazz);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(INTERNAL_SERVER_ERROR, SERVICE_NOT_FOUND);
        }
    }

    private void updateFile(File file) throws HttpBeanException {
        try {
            getService(IFileDataHandler.class).updateFile(file);
        }catch (DataHandlerException e) {
            throw new HttpBeanException(INTERNAL_SERVER_ERROR, DATAHANDLER_EXPCEPTION);
        }
    }

    private Category getCategoryFromRequestParameter(Parameter parameter) throws HttpBeanException {
        String categoryName = parameter.getRequiredParameter("category");
        if(categoryName == null) throw new HttpBeanException(BAD_REQUEST, MISSING_PARAMETER);
        return new Category(categoryName);
    }


    @Override
    protected void doCreate(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws IOException, HttpBeanException {
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws IOException, HttpBeanException {
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
