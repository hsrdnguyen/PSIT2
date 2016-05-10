package ch.avocado.share.servlet;

import ch.avocado.share.common.ResponseHelper;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpServletException;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.FileStorageException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IAvatarStorageHandler;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import static ch.avocado.share.common.HttpStatusCode.NOT_FOUND;

@WebServlet("/avatar")
public class AvatarServlet extends HttpServlet {
    public static String PARAMETER_USER_ID = "id";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter(PARAMETER_USER_ID);
        if(userId == null) {
            resp.sendError(NOT_FOUND.getCode());
        } else {
            // TODO: check if modified
            HttpServletException error = writeAvatar(userId, resp);
            if(error != null) {
                ResponseHelper.sendErrorFromHttpBeanException(error, req, resp);
            }
        }

    }

    /**
     * Writes the avatar to the output stream
     * @param userId The identifier of the user to which the avatar belong
     * @param resp The response to write the data into
     * @throws IOException The image data could not be written
     * @return The error object if an error occurs.
     */
    private HttpServletException writeAvatar(String userId, HttpServletResponse resp) throws IOException {
        if(userId == null) throw new NullPointerException("userId is null");
        User user;
        try {
            IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
            user = userDataHandler.getUser(userId);
        } catch (ServiceNotFoundException | DataHandlerException | ObjectNotFoundException e) {
            return new HttpServletException(e);
        }
        String avatarId = user.getAvatar();

        String imageType;
        InputStream inputStream;
        IAvatarStorageHandler avatarStorageHandler;
        try {
            avatarStorageHandler = ServiceLocator.getService(IAvatarStorageHandler.class);
            inputStream = avatarStorageHandler.readImages(avatarId);
        } catch (ServiceNotFoundException | FileStorageException e) {
            return new HttpServletException(e);
        }
        imageType = avatarStorageHandler.getImageType(avatarId);
        resp.setHeader("Content-Type", imageType);
        resp.setHeader("ETag", avatarId);
        try {
            IOUtils.copy(inputStream, resp.getOutputStream());
        } finally {
            inputStream.close();
        }
        return null;
    }
}