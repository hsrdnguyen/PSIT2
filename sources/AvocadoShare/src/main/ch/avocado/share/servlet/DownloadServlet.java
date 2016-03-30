package ch.avocado.share.servlet;

import ch.avocado.share.controller.FileBean;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.HttpBeanException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FileBean fileBean = new FileBean();
        fileBean.setId(request.getParameter("id"));
        UserSession userSession = new UserSession(request);
        fileBean.setAccessingUser(userSession.getUser());
        try {
            File file = fileBean.get();
            fileBean.download(file, response);
        } catch (HttpBeanException e) {
            response.sendError(e.getStatusCode(), e.getDescription());
        }
    }
}
