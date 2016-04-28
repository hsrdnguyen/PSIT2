package ch.avocado.share.servlet;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IAvatarStorageHandler;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import static ch.avocado.share.common.HttpStatusCode.INTERNAL_SERVER_ERROR;
import static ch.avocado.share.common.HttpStatusCode.NOT_FOUND;

@WebServlet("/avatar")
public class AvatarServlet extends HttpServlet {
    public static String PARAMETER_AVATAR = "id";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String avatarParameter = req.getParameter(PARAMETER_AVATAR);

        if(avatarParameter == null) {
            resp.sendError(NOT_FOUND.getCode());
        } else {
            writeAvatar(avatarParameter, resp);
        }

    }

    private void writeAvatar(String avatarParameter, HttpServletResponse resp) throws IOException {
        if(avatarParameter == null) {
            throw new IllegalArgumentException("avatarParameter is null");
        }
        String imageType;
        InputStream inputStream = null;
        IAvatarStorageHandler avatarStorageHandler = null;
        try {
            avatarStorageHandler = ServiceLocator.getService(IAvatarStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            resp.sendError(INTERNAL_SERVER_ERROR.getCode());
            return;
        }
        inputStream = avatarStorageHandler.readImages(avatarParameter);
        if(inputStream == null) {
            resp.sendError(NOT_FOUND.getCode());
            return;
        }
        imageType = avatarStorageHandler.getImageType(avatarParameter);
        resp.setHeader("Content-Type", imageType);
        resp.setHeader("ETag", avatarParameter);
        try {
            IOUtils.copy(inputStream, resp.getOutputStream());
        } finally {
            inputStream.close();
        }
    }
}