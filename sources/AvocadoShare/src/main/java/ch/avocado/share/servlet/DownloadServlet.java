package ch.avocado.share.servlet;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.controller.FileBean;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.HttpBeanDatabaseException;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileStorageHandler;
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

@WebServlet("/download")
public class DownloadServlet extends HttpServlet{

    private static final String PARAMETER_ID = "id";
    private static final String PARAMETER_ATTACHMENT = "download";
    private static int DOWNLOAD_BUFFER_SIZE = 512;

    static public String getStreamUrl(File file) throws UnsupportedEncodingException {
        if(file == null) throw new IllegalArgumentException("file is null");
        return "/download?id=" + Encoder.forUrl(file.getId());
    }

    public static void download(FileBean fileBean, File file, HttpServletResponse response, boolean attached) throws HttpBeanException {
        byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
        InputStream stream;
        IFileStorageHandler storageHandler;
        try{
            storageHandler = ServiceLocator.getService(IFileStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessageConstants.SERVICE_NOT_FOUND + e.getService());
        }

        try {
            stream = storageHandler.readFile(file.getPath());
        } catch (FileStorageException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        String filename = file.getTitle() + "." + file.getExtension();

        if (attached) {
            response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
        }
        try {
            OutputStream outputStream = response.getOutputStream();
            int bytesRead;
            while ((bytesRead = stream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
        } catch (IOException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FileBean fileBean = new FileBean();
        fileBean.setId(request.getParameter(PARAMETER_ID));
        UserSession userSession = new UserSession(request);
        String attachmentParameter = request.getParameter(PARAMETER_ATTACHMENT);
        boolean attached = attachmentParameter != null && !attachmentParameter.isEmpty();
        fileBean.setAccessingUser(userSession.getUser());
        try {
            File file;
            try {
                file = fileBean.get();
            } catch (DataHandlerException e) {
                throw new HttpBeanDatabaseException();
            }
            download(fileBean, file, response, attached);
        } catch (HttpBeanException e) {
            response.sendError(e.getStatusCode(), e.getDescription());
        }
    }
}
