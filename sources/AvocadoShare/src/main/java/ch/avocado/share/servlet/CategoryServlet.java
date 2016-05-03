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
import ch.avocado.share.service.ICategoryDataHandler;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.Impl.CategoryDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ch.avocado.share.common.HttpStatusCode.*;
import static ch.avocado.share.common.constants.ErrorMessageConstants.*;


@WebServlet("/category")
public class CategoryServlet extends HttpServlet {

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

    private void ensureRequesterHasWriteAccess(HttpServletRequest request, File file) throws HttpBeanException {
        UserSession userSession = new UserSession(request);
        User accessingUser = userSession.getUser();
        if(accessingUser == null) {
            throw new HttpBeanException(UNAUTHORIZED, NOT_LOGGED_IN);
        }
        ensureHasWriteAccess(userSession.getUser(), file);

    }

    private File getFileFromRequestParameter(HttpServletRequest request) throws HttpBeanException{
        IFileDataHandler fileDataHandler = getService(IFileDataHandler.class);
        String identifier = request.getParameter("id");
        if(identifier == null) throw new HttpBeanException(BAD_REQUEST, MISSING_PARAMETER);
        File file;
        try {
            file = fileDataHandler.getFile(identifier);
        } catch (DataHandlerException e) {
            e.printStackTrace();
            throw new HttpBeanException(INTERNAL_SERVER_ERROR, DATAHANDLER_EXPCEPTION);
        }
        ensureRequesterHasWriteAccess(request, file);
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

    private void deleteCategory(HttpServletRequest req) throws HttpBeanException {
        File file = getFileFromRequestParameter(req);
        Category category = getCategoryFromRequestPrameter(req);
        if(!file.getCategoryList().contains(category)) {
            return;
        }
        file.getCategoryList().remove(category);
        updateFile(file);
    }

    private Category getCategoryFromRequestPrameter(HttpServletRequest req) throws HttpBeanException {
        String categoryName = req.getParameter("category");
        if(categoryName == null) throw new HttpBeanException(BAD_REQUEST, MISSING_PARAMETER);
        return new Category(categoryName);
    }

    private void addNewCategory(HttpServletRequest request) throws HttpBeanException {
        File file = getFileFromRequestParameter(request);
        Category category = getCategoryFromRequestPrameter(request);
        if(file.getCategoryList().contains(category)) {
            // Nothing to do
            return;
        }
        file.getCategoryList().add(category);
        updateFile(file);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if("DELETE".equalsIgnoreCase(req.getParameter("method"))) {
            doDelete(req, resp);
            return;
        }
        try {
            addNewCategory(req);
            ResponseHelper.redirectToOrigin(req, resp);
        }catch (HttpBeanException e) {
            ResponseHelper.sendErrorFromHttpBeanException(e, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            deleteCategory(req);
            ResponseHelper.redirectToOrigin(req, resp);
        } catch (HttpBeanException e) {
            ResponseHelper.sendErrorFromHttpBeanException(e, resp);
        }
    }
}
