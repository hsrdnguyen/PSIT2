package ch.avocado.share.controller;


import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.Assert.*;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Date;


class MockObject extends AccessControlObjectBase {
    public MockObject(String id) {
        super(id, new ArrayList<Category>(), new Date(System.currentTimeMillis()), 0, "User1", "Description");
    }
}

class MockObjectBean extends ResourceBean<MockObject> {

    private String id;


    @Override
    protected boolean hasIdentifier() {
        return id != null;
    }

    @Override
    public MockObject create() throws HttpBeanException {
        return new MockObject("creat");
    }

    @Override
    public MockObject get() throws HttpBeanException {
        if(id == null) {
            throw new RuntimeException("Get with id == null");
        }
        return new MockObject(id);
    }

    @Override
    public MockObject[] index() throws HttpBeanException {
        MockObject[] list = new MockObject[2];
        list[0] = new MockObject("0");
        list[1] = new MockObject("1");
        return list;
    }

    @Override
    public void update() throws HttpBeanException {
        MockObject object = getObject();
        object.setId(id);
    }

    @Override
    public void destroy() throws HttpBeanException {

    }

    @Override
    public String getAttributeName() {
        return "MockObject";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

public class ResourceBeanTest {
    private MockObjectBean bean;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private User user;

    @Before
    public void setUp() {
        bean = new MockObjectBean();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        user = new User(null, new ArrayList<Category>(), new Date(System.currentTimeMillis()), 0, null, "User", UserPassword.fromPassword("1234"), "Felix", "Muster", "????", new EmailAddress(true, "fmuster@students.zhaw.ch", null));
        UserSession session = new UserSession(request);
        session.authenticate(user);
    }

    @Test
    public void test_index_when_no_identifier() throws ServletException {
        request.setMethod("GET");
        assertFalse(bean.hasIdentifier());
        bean.renderRequest(request, response);
        System.out.println(response.getStatus());
        System.out.println(response.getErrorMessage());
    }

    @Test
    public void test_index_when_identifier() throws ServletException {
        request.setMethod("GET");
        bean.setId("1");
        assertTrue(bean.hasIdentifier());
    }
}
