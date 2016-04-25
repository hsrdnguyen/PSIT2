package ch.avocado.share.servlet;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.FileStorageException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import static ch.avocado.share.common.HttpStatusCode.*;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet{

    private static final String PARAMETER_ID = "id";
    private static final String PARAMETER_ATTACHMENT = "download";
    private static final String IF_NONE_MATCH = "If-None-Match";
    private static int DOWNLOAD_BUFFER_SIZE = 512;

    static public String getStreamUrl(File file) throws UnsupportedEncodingException {
        if(file == null) throw new IllegalArgumentException("file is null");
        return "/download?" + PARAMETER_ID + "=" + Encoder.forUrl(file.getId());
    }

    static public String getDownloadUrl(File file) throws UnsupportedEncodingException {
        return getStreamUrl(file)  + "&" + PARAMETER_ATTACHMENT + "=" + "d";
    }

    private static String getEtag(File file) {
        return file.getPath();
    }

    public static void download(File file, HttpServletRequest request, HttpServletResponse response, boolean attached) throws HttpBeanException, IOException, ServiceNotFoundException, FileStorageException {
        byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
        InputStream stream;
        String etag = getEtag(file);

        String ifNoneMatch = request.getHeader(IF_NONE_MATCH);
        if(ifNoneMatch != null && etag.equals(ifNoneMatch)) {
            // Not modified
            response.sendError(NOT_MODIFIED.getCode());
            return;
        }

        IFileStorageHandler storageHandler;
        storageHandler = ServiceLocator.getService(IFileStorageHandler.class);

        long size;
        size = storageHandler.getFileSize(file.getPath());
        stream = storageHandler.readFile(file.getPath());

        response.setHeader("Content-Length", Long.toString(size));
        response.setHeader("Content-Type", file.getMimeType());
        response.setHeader("ETag", etag);
        String filename = file.getTitle() + "." + file.getExtension();

        if (attached) {
            response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
        }
        OutputStream outputStream = response.getOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String attachmentParameter = request.getParameter(PARAMETER_ATTACHMENT);
        String idParameter = request.getParameter(PARAMETER_ID);


        boolean attached = attachmentParameter != null && !attachmentParameter.isEmpty();
        if(idParameter == null) {
            response.sendRedirect(request.getServletContext().getContextPath());
            return;
        }

        UserSession userSession = new UserSession(request);
        if(!userSession.isAuthenticated()) {
            response.sendError(UNAUTHORIZED.getCode(), ErrorMessageConstants.NOT_LOGGED_IN);
            return;
        }

        File file;
        AccessLevelEnum allowedLevel;
        try {
            IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            ISecurityHandler securityHandler = ServiceLocator.getService(ISecurityHandler.class);
            file = fileDataHandler.getFile(idParameter);
            allowedLevel = securityHandler.getAccessLevel(userSession.getUser(), file);
        } catch (ServiceNotFoundException | DataHandlerException e) {
            response.sendError(INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
            return;
        }

        if(!allowedLevel.containsLevel(AccessLevelEnum.READ)) {
            response.sendError(FORBIDDEN.getCode(), "Sie haben kein Leserecht");
            return;
        }

        if(file == null) {
            response.sendError(NOT_FOUND.getCode(), "Datei existiert nicht");
            return;
        }

        try {
            download(file, request, response, attached);
        } catch (HttpBeanException e) {
            response.sendError(e.getStatusCode(), e.getDescription());
        } catch (ServiceNotFoundException | FileStorageException e) {
            e.printStackTrace();
            response.sendError(INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
    }
}
