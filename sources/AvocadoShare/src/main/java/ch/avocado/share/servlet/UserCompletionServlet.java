package ch.avocado.share.servlet;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@WebServlet(value = "/user_completion", name = "User Completion")
public class UserCompletionServlet extends HttpServlet {

    public static final String PARAM_QUERY = "q";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSession userSession = new UserSession(req);
        String query = req.getParameter(PARAM_QUERY);
        List<User> users;
        if(!userSession.isAuthenticated() || query == null || query.isEmpty()) {
            users = new ArrayList<>();
        } else {
            users = getUsers(userSession.getUser(), query);
        }
        resp.setContentType("application/json; charset=UTF-8");
        OutputStream out = resp.getOutputStream();
        renderResult(users, out);
    }

    private void renderResult(List<User> users, OutputStream out) throws IOException {
        if(users == null) throw new NullPointerException("users is null");
        if(out == null) throw new NullPointerException("out is null");

        out.write("{\"users\":[".getBytes());
        Iterator<User> i = users.iterator();
        while(i.hasNext()) {
            writeUser(i.next(), out);
            if(i.hasNext()) {
                out.write(",".getBytes());
            }
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
                if(!term.isEmpty()) {
                    terms.add(term);
                }
            }
            return userDataHandler.search(terms);
        } catch (DataHandlerException | ServiceNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
