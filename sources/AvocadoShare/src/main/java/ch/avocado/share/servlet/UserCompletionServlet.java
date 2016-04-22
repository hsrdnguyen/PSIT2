package ch.avocado.share.servlet;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Service;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/user_completion")
public class UserCompletionServlet extends HttpServlet {

    public static final String PARAM_QUERY = "q";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSession userSession = new UserSession(req);
        String query = req.getParameter(PARAM_QUERY);
        List<User> users;
        /*if(!userSession.isAuthenticated() || query == null || query.isEmpty()) {
            users = new ArrayList<>();
        } else {*/
            users = getUsers(userSession.getUser(), query);
        //}
        resp.setContentType("application/json; charset=UTF-8");
        OutputStream out = resp.getOutputStream();
        renderResult(users, out);
    }

    private void renderResult(List<User> users, OutputStream out) throws IOException {
        out.write("{\"users\":[".getBytes());
        for(User user: users) {
            writeUser(user, out);
        }
        out.write("]}".getBytes());
    }

    private void writeUser(User user, OutputStream out) throws IOException {
        String value = "{\"name\": \"%s\", \"id\": %s}";
        value = String.format(value, user.getFullName(), user.getId().toString());
        out.write(value.getBytes("UTF-8"));
    }

    private List<User> getUsers(User user, String query) {
        try {
            IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
            Set<String> terms = new HashSet<>();
            for(String term: query.split(" ")) {
                terms.add(term);
            }
            return userDataHandler.search(terms);
        } catch (DataHandlerException | ServiceNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
