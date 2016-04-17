package ch.avocado.share.servlet.resources.base;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.controller.UserBean;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.Mock.UserDataHandlerMock;
import ch.avocado.share.servlet.resources.UserServlet;
import ch.avocado.share.test.UserArgumentMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link UserServlet} but also for {@link ResourceServlet}.
 */
public class UserServletTest {

    private UserServlet servlet;
    private MockHttpServletResponse response;
    private User dummyUser;
    private HtmlRenderer mockHtmlRenderer;
    private UserBean mockBean;
    private MockHttpServletRequest request;
    private ISecurityHandler mockSecurityHandler;
    private User accessingUser;
    private UserArgumentMatcher isAccessingUser;
    @Before
    public void setUp() throws Exception {
        resetServlet();

    }


    @After
    public void restoreServices() throws Exception {
        ServiceLocatorModifier.restore();
    }

    private void resetServlet() throws Exception {
        servlet = spy(new UserServlet());
        mockBean = mock(UserBean.class);
        mockHtmlRenderer = mock(HtmlRenderer.class);
        mockSecurityHandler = mock(ISecurityHandler.class);
        UserDataHandlerMock userDataHandlerMock = new UserDataHandlerMock();
        ServiceLocatorModifier.setService(IUserDataHandler.class, userDataHandlerMock);

        request = new MockHttpServletRequest();
        when(servlet.getBean()).thenReturn(mockBean);
        when(servlet.getHtmlRenderer()).thenReturn(mockHtmlRenderer);
        when(mockSecurityHandler.getAnonymousAccessLevel(any(User.class))).thenReturn(AccessLevelEnum.NONE);
        when(mockSecurityHandler.getAnonymousAccessLevel(any(String.class))).thenReturn(AccessLevelEnum.NONE);
        ServiceLocatorModifier.setService(ISecurityHandler.class, mockSecurityHandler);

        response = new MockHttpServletResponse();
        dummyUser = new User(null, new ArrayList<Category>(), new Date(), 0.0f, "Description", UserPassword.EMPTY_PASSWORD, "Prename", "Surname", "1234", new EmailAddress(false, "asomething@zhaw.ch", new EmailAddressVerification(new Date())));
        accessingUser = new User(null, new ArrayList<Category>(), new Date(), 0.0f, "Description", UserPassword.EMPTY_PASSWORD, "Prename", "Surname", "1234", new EmailAddress(false, "asomething@zhaw.ch", new EmailAddressVerification(new Date())));

        ServiceLocator.getService(IUserDataHandler.class).addUser(dummyUser);
        ServiceLocator.getService(IUserDataHandler.class).addUser(accessingUser);
        assertNotNull(accessingUser.getId());
        assertNotNull(dummyUser.getId());

        isAccessingUser = new UserArgumentMatcher(accessingUser);

    }

    private List<MockHttpServletRequest> getRealAndSimulateHtmlRequestForMethod(String method) {
        method = method.toUpperCase();
        ArrayList<MockHttpServletRequest> requests = new ArrayList<>(3);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setMethod(method);
        requests.add(request);

        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setParameter(ResourceServlet.PARAMETER_METHOD, method);
        requests.add(request);

        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setParameter(ResourceServlet.PARAMETER_METHOD, method.toLowerCase());
        requests.add(request);

        return requests;
    }

    @Test
    public void testCreate() throws Exception {
        for (HttpServletRequest request : getRealAndSimulateHtmlRequestForMethod("post")) {
            resetServlet();
            when(mockBean.create()).thenReturn(dummyUser);
            servlet.service(request, response);
            verify(mockBean, times(1)).create();

            // Renderer not invoked because its redirected on success
            verify(servlet.getHtmlRenderer(), never()).renderView(any(ViewConfig.class));
            // Check redirection
            assertNotNull(response.getRedirectedUrl());
            assertTrue(response.getRedirectedUrl().contains(dummyUser.getId()));
        }
    }

    @Test
    public void testCreateFails() throws Exception {
        for (HttpServletRequest request : getRealAndSimulateHtmlRequestForMethod("post")) {
            resetServlet();
            // make user invalid
            dummyUser = spy(dummyUser);
            when(dummyUser.hasErrors()).thenReturn(true);
            when(dummyUser.isValid()).thenReturn(false);
            assertTrue(dummyUser.hasErrors());
            assertFalse(dummyUser.isValid());

            // Check that create is called and return user
            when(mockBean.create()).thenReturn(dummyUser);
            servlet.service(request, response);
            verify(mockBean, times(1)).create();
            assertEquals(response.getErrorMessage(), 200, response.getStatus());
            // Renderer not invoked because its redirected on success
            verify(mockHtmlRenderer, times(1)).renderView(any(ViewConfig.class));
        }
    }

    @Test
    public void testParameterSetting() throws Exception {
        String id, prename, surname, mail, password, passwordConfirmation;
        id = "12345";
        prename = "Prename";
        surname = "Surname";
        mail = "email@zhaw.ch";
        password = "123456789";
        passwordConfirmation = "1234567890";
        request.setParameter("id", id);
        request.setParameter("prename", prename);
        request.setParameter("surname", surname);
        request.setParameter("mail", mail);
        request.setParameter("password", password);
        request.setParameter("passwordConfirmation", passwordConfirmation);
        request.setMethod("POST");

        when(mockBean.create()).thenReturn(dummyUser);
        servlet.service(request, response);

        verify(mockBean, times(1)).setPrename(eq(prename));
        verify(mockBean, times(1)).setSurname(eq(surname));
        verify(mockBean, times(1)).setMail(eq(mail));
        verify(mockBean, times(1)).setPassword(eq(password));
        verify(mockBean, times(1)).setPasswordConfirmation(eq(passwordConfirmation));

    }

    @Test
    public void testUpdateWithoutAccess() throws Exception {
        for (HttpServletRequest request : getRealAndSimulateHtmlRequestForMethod("patch")) {
            resetServlet();
            when(mockBean.get()).thenReturn(dummyUser);
            servlet.service(request, response);
            verify(mockBean, times(1)).get();
            UserArgumentMatcher isUser = new UserArgumentMatcher(dummyUser);
            verify(mockBean, never()).update(argThat(isUser));
        }
    }

    @Test
    public void testUpdateWithNotEnoughRights() throws Exception {
        for (HttpServletRequest request : getRealAndSimulateHtmlRequestForMethod("patch")) {
            resetServlet();
            new UserSession(request).authenticate(accessingUser);
            when(mockBean.get()).thenReturn(dummyUser);
            UserArgumentMatcher isUser = new UserArgumentMatcher(dummyUser);
            // Authenticate user
            when(mockSecurityHandler.getAccessLevel(argThat(isAccessingUser), argThat(isUser))).thenReturn(AccessLevelEnum.WRITE);
            when(mockSecurityHandler.getAccessLevel(eq(accessingUser.getId()), eq(dummyUser.getId()))).thenReturn(AccessLevelEnum.WRITE);
            servlet.service(request, response);
            verify(mockBean, atMost(1)).get();
            verify(mockBean, never()).update(argThat(isUser));

            assertEquals(HttpStatusCode.FORBIDDEN.getCode(), response.getStatus());
        }
    }

    @Test
    public void testUpdateWithOwnership() throws Exception {
        for (HttpServletRequest request : getRealAndSimulateHtmlRequestForMethod("patch")) {
            resetServlet();
            new UserSession(request).authenticate(dummyUser);
            when(mockBean.get()).thenReturn(dummyUser);
            UserArgumentMatcher isUser = new UserArgumentMatcher(dummyUser);

            when(mockSecurityHandler.getAccessLevel(argThat(isUser), argThat(isUser))).thenReturn(AccessLevelEnum.OWNER);
            when(mockSecurityHandler.getAccessLevel(eq(dummyUser.getId()), eq(dummyUser.getId()))).thenReturn(AccessLevelEnum.OWNER);
            servlet.service(request, response);
            verify(mockBean, times(1)).get();
            verify(mockBean, times(1)).update(argThat(isUser));

            assertNotNull(response.getRedirectedUrl());
            assertTrue(response.getRedirectedUrl().contains(dummyUser.getId()));
        }
    }

    @Test
    public void testDeleteUserWithoutOwnership() throws Exception {
        List<AccessLevelEnum> levelsToTry = new ArrayList<>();
        levelsToTry.add(AccessLevelEnum.NONE);
        levelsToTry.add(AccessLevelEnum.READ);
        levelsToTry.add(AccessLevelEnum.WRITE);
        levelsToTry.add(AccessLevelEnum.MANAGE);

        for(AccessLevelEnum access: levelsToTry) {
            for (HttpServletRequest request : getRealAndSimulateHtmlRequestForMethod("delete")) {
                resetServlet();

                new UserSession(request).authenticate(accessingUser);
                when(mockBean.get()).thenReturn(dummyUser);
                UserArgumentMatcher isUser = new UserArgumentMatcher(dummyUser);
                // Authenticate user
                when(mockSecurityHandler.getAccessLevel(argThat(isAccessingUser), argThat(isUser))).thenReturn(access);
                when(mockSecurityHandler.getAccessLevel(eq(accessingUser.getId()), eq(dummyUser.getId()))).thenReturn(access);
                servlet.service(request, response);
                verify(mockBean, atMost(1)).get();
                verify(mockBean, never()).destroy(argThat(isUser));

                assertEquals(HttpStatusCode.FORBIDDEN.getCode(), response.getStatus());
            }
        }
    }

    @Test
    public void testDeleteUserAsOwner() throws Exception {
        for (HttpServletRequest request : getRealAndSimulateHtmlRequestForMethod("delete")) {

            resetServlet();

            new UserSession(request).authenticate(dummyUser);
            when(mockBean.get()).thenReturn(dummyUser);
            UserArgumentMatcher isUser = new UserArgumentMatcher(dummyUser);

            when(mockSecurityHandler.getAccessLevel(argThat(isUser), argThat(isUser))).thenReturn(AccessLevelEnum.OWNER);
            when(mockSecurityHandler.getAccessLevel(eq(dummyUser.getId()), eq(dummyUser.getId()))).thenReturn(AccessLevelEnum.OWNER);
            servlet.service(request, response);
            verify(mockBean, times(1)).get();
            verify(mockBean, times(1)).destroy(argThat(isUser));

            assertNotNull(response.getRedirectedUrl());
            assertFalse(response.getRedirectedUrl().contains(dummyUser.getId()));
        }
    }
}