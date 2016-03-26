package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.GroupDataHandlerMock;
import ch.avocado.share.service.Mock.SecurityHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.Mock.UserDataHandlerMock;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class BeanTestBase {
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_OK = 200;

    protected static void assertStatusCodeEquals(int status, MockHttpServletResponse response) {
        assertEquals(response.getErrorMessage(), status, response.getStatus());
    }

    @Before
    public void setUpMockServicesAndSession() throws Exception{
        ServiceLocatorModifier.setService(IUserDataHandler.class, new UserDataHandlerMock());
        ServiceLocatorModifier.setService(IGroupDataHandler.class, new GroupDataHandlerMock());
        securityHandlerMock = new SecurityHandlerMock();
        ServiceLocatorModifier.setService(ISecurityHandler.class, securityHandlerMock);
        securityHandlerMock.setAnonymousAccess(AccessLevelEnum.NONE);
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        session = new UserSession(request);
        response = new MockHttpServletResponse();
    }

    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    protected SecurityHandlerMock securityHandlerMock;
    protected UserSession session;

    protected void authenticateAccess(AccessLevelEnum accessLevel) throws Exception {
        User user = securityHandlerMock.getUserWithAccess(accessLevel);
        assertNotNull(user);
        assertNotNull(user.getId());
        session.authenticate(user);
        assertTrue(session.isAuthenticated());
    }

    protected ISecurityHandler getSecurityHandler() {
        return securityHandlerMock;
    }

    protected IUserDataHandler getUserDataHandler() throws ServiceNotFoundException {
        return ServiceLocator.getService(IUserDataHandler.class);
    }

    protected IGroupDataHandler getGroupDataHandler() throws ServiceNotFoundException {
        return  ServiceLocator.getService(IGroupDataHandler.class);
    }

}
