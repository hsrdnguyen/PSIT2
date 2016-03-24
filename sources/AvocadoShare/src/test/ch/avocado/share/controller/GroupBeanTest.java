package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.testhelper.GroupDataHandlerMock;
import ch.avocado.share.testhelper.SecurityHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.testhelper.UserDataHandlerMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static org.junit.Assert.*;

public class GroupBeanTest {

    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_OK = 200;

    public GroupBeanTest() throws Exception{
        ServiceLocatorModifier.setService(IGroupDataHandler.class, new GroupDataHandlerMock());
        ServiceLocatorModifier.setService(IUserDataHandler.class, new UserDataHandlerMock());
        securityHandlerMock = new SecurityHandlerMock();
        ServiceLocatorModifier.setService(ISecurityHandler.class, securityHandlerMock);
    }

    private GroupBean bean;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private SecurityHandlerMock securityHandlerMock;
    private UserSession session;
    @Before
    public void setUp() throws Exception {
        securityHandlerMock.setAnonymousAccess(AccessLevelEnum.NONE);
        bean = new GroupBean();
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        session = new UserSession(request);
        response = new MockHttpServletResponse();
        assertEquals(ServiceLocator.getService(ISecurityHandler.class), securityHandlerMock);
    }

    private static void assertStatusCodeEquals(int status, MockHttpServletResponse response) {
        assertEquals(response.getErrorMessage(), status, response.getStatus());
    }

    private void authenticateAccess(AccessLevelEnum accessLevel) throws Exception {
        User user = securityHandlerMock.getUserWithAccess(accessLevel);
        assertNotNull(user);
        assertNotNull(user.getId());
        session.authenticate(user);
        assertTrue(session.isAuthenticated());
        assertEquals(securityHandlerMock.getAccessLevel(user, user), accessLevel);
    }

    @Test
    public void testGetIndex() throws Exception {
        authenticateAccess(AccessLevelEnum.READ);
        request.setMethod("GET");
        assertFalse(bean.hasIdentifier());
        assertFalse(bean.hasErrors());
        bean.renderRequest(request, response);
        assertFalse(bean.hasErrors());
        assertStatusCodeEquals(STATUS_OK, response);
        Group[] groups = (Group[]) request.getAttribute("Groups");
        assertNotNull(groups);
        assertEquals(groups.length, GroupDataHandlerMock.NUMBER_OF_GROUPS);
    }

    @Test
    public void testGetDetailByName() throws Exception {
        authenticateAccess(AccessLevelEnum.READ);
        request.setMethod("GET");
        final String groupName = GroupDataHandlerMock.EXISTING_GROUP_NAME;
        bean.setName(groupName);
        assertFalse(bean.hasErrors());
        assertTrue(bean.hasIdentifier());
        bean.renderRequest(request, response);
        assertBeanHasNoErrors();
        assertStatusCodeEquals(STATUS_OK, response);
        // Group have to be set if the access is permitted.
        Group group = (Group) request.getAttribute("Group");
        assertNotNull(group);
        assertEquals(group.getName(), groupName);
    }

    @Test
    public void testGetDetailById() throws Exception {
        authenticateAccess(AccessLevelEnum.READ);
        request.setMethod("GET");

        // Set identifier
        final String groupId = GroupDataHandlerMock.EXISTING_GROUP0;
        bean.setId(groupId);
        assertTrue(bean.hasIdentifier());

        // Execute request
        bean.renderRequest(request, response);
        assertBeanHasNoErrors();
        assertStatusCodeEquals(STATUS_OK, response);

        // Group have to be set if the access is permitted.
        Group group = (Group) request.getAttribute("Group");
        assertNotNull(group);
        assertEquals(group.getId(), groupId);
    }

    @Test
    public void testGetDetailWithInsufficientAccess() throws Exception {
        authenticateAccess(AccessLevelEnum.NONE);
        request.setMethod("GET");
        bean.setName("groupName0");
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);
        // Group must not be set if the access is denied.
        Group group = (Group) request.getAttribute("Group");
        assertNull(group);
    }

    @Test
    public void testGetDetailEdit() throws Exception {
        authenticateAccess(AccessLevelEnum.WRITE);
        request.setMethod("GET");
        bean.setName("groupName0");
        bean.setAction("edit");
        assertTrue(bean.hasIdentifier());
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_OK, response);
        // Group must be set if the user has access.
        Group group = (Group) request.getAttribute("Group");
        assertNotNull(group);
    }

    @Test
    public void testGetDetailEditInsufficientAccess() throws Exception {
        authenticateAccess(AccessLevelEnum.READ);
        request.setMethod("GET");
        bean.setName(GroupDataHandlerMock.EXISTING_GROUP_NAME);
        bean.setAction("edit");
        assertTrue(bean.hasIdentifier());
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);
        // Group must not be set if the access is denied.
        Group group = (Group) request.getAttribute("Group");
        assertNull(group);
    }

    @Test
    public void testGetCreateForm() throws Exception {
        authenticateAccess(AccessLevelEnum.NONE);
        request.setMethod("GET");
        bean.setAction("create");
        bean.renderRequest(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        // Group must not exist prior to generation (this shows only the form to create it)
        Group group = (Group) request.getAttribute("Group");
        assertNull(group);
    }

    @Test
    public void testGetCreateNotLoggedIn() throws Exception {
        assertFalse(session.isAuthenticated());
        request.setMethod("GET");
        bean.setAction("create");
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);
        Group group = (Group) request.getAttribute("Group");
        assertNull(group);
    }

    @Test
    public void testHasIdentifierFromId() throws Exception {
        assertFalse(bean.hasIdentifier());
        bean.setId("Identifier");
        assertTrue(bean.hasIdentifier());
    }

    @Test
    public void testHasIdentifierFromName() throws Exception {
        assertFalse(bean.hasIdentifier());
        bean.setName("name");
        assertTrue(bean.hasIdentifier());
    }


    @Test
    public void testCreate() throws Exception {
        final String name = GroupDataHandlerMock.NOT_EXISTING_GROUP_NAME;
        final String description = "My group description";
        authenticateAccess(AccessLevelEnum.NONE);
        request.setMethod("POST");
        bean.setName(name);
        bean.setDescription(description);
        bean.setId(null);
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_OK, response);
        assertBeanHasNoErrors();
        // check if the group has been stored
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        Group group = groupDataHandler.getGroupByName(name);
        assertNotNull(group);
        assertEquals(group.getDescription(), description);
        assertEquals(group.getName(), name);
    }

    @Test
    public void testCreateWithDoubledName() throws Exception {
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        final String name = GroupDataHandlerMock.EXISTING_GROUP_NAME;
        final String description = "My group description";
        // ensure name already exists
        assertNotNull(groupDataHandler.getGroupByName(name));
        authenticateAccess(AccessLevelEnum.NONE);
        request.setMethod("POST");
        bean.setName(name);
        bean.setDescription(description);
        bean.setId(null);
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_OK, response);
        assertTrue(bean.hasErrors());

        // make sure the existing group wasn't overwritten
        Group existingGroup = groupDataHandler.getGroupByName(name);
        assertNotNull(existingGroup);
        assertNotEquals(existingGroup.getDescription(), description);
    }

    public void assertBeanHasNoErrors() {
        if(bean.hasErrors()) {
            String errorMessage = "";
            for(Map.Entry<String, String> error: bean.getFormErrors().entrySet()) {
                errorMessage += error.getKey() + ": " + error.getValue() + "\n";
            }
            fail(errorMessage);
        }
    }

    @Test
    public void testUpdateByName() throws Exception {
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        final String name = GroupDataHandlerMock.EXISTING_GROUP_NAME;
        final String description = "My new group description";
        // ensure name already exists
        Group existingGroup = groupDataHandler.getGroupByName(name);
        String previousId = existingGroup.getId();
        assertNotNull(existingGroup);
        // ensure description can be changed
        assertNotEquals(existingGroup.getDescription(), description);

        authenticateAccess(AccessLevelEnum.WRITE);
        request.setMethod("PATCH");
        bean.setName(name);
        bean.setDescription(description);
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_OK, response);
        assertBeanHasNoErrors();

        // make sure the changed group is in the request
        Group changedGroup = (Group) request.getAttribute("Group");
        assertNotNull(changedGroup);
        assertEquals("Description not updated", description, changedGroup.getDescription());
        assertEquals("ID changed", previousId, changedGroup.getId());

        // make sure it's also in the database
        changedGroup = groupDataHandler.getGroupByName(name);
        assertNotNull(changedGroup);
        assertEquals("Description not updated", description, changedGroup.getDescription());
        assertEquals("ID changed", previousId, changedGroup.getId());
    }

    @Test
    public void testUpdateById() throws Exception {
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        final String groupId = GroupDataHandlerMock.EXISTING_GROUP0;
        final String description = "My new group description";
        // ensure name already exists
        Group existingGroup = groupDataHandler.getGroup(groupId);
        String name = existingGroup.getName();
        name = name + " different";
        assertNotNull(existingGroup);
        // ensure description can be changed
        assertNotEquals(existingGroup.getDescription(), description);

        authenticateAccess(AccessLevelEnum.WRITE);
        request.setMethod("PATCH");
        bean.setId(groupId);
        bean.setName(name);
        bean.setDescription(description);
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_OK, response);
        assertBeanHasNoErrors();

        // make sure the changed group is in the request
        Group changedGroup = (Group) request.getAttribute("Group");
        assertNotNull(changedGroup);
        assertEquals("Description not updated in request", description, changedGroup.getDescription());
        assertEquals("name not changed in request", name, changedGroup.getName());
        assertEquals("ID changed in request", groupId, changedGroup.getId());

        // make sure it's also in the database
        changedGroup = groupDataHandler.getGroupByName(name);
        assertNotNull(changedGroup);
        assertEquals("Description not updated in DB", description, changedGroup.getDescription());
        assertEquals("name not changed in DB", name, changedGroup.getName());
        assertEquals("ID changed in DB", groupId, changedGroup.getId());
    }

    @Test
    public void testUpdateWithInsufficientRights() throws Exception {
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        final String groupId = GroupDataHandlerMock.EXISTING_GROUP0;

        // ensure name already exists
        Group existingGroup = groupDataHandler.getGroup(groupId);
        assertNotNull(existingGroup);

        final String newDescription = existingGroup.getDescription() + " different";
        final String newName = existingGroup.getName() + " different";

        authenticateAccess(AccessLevelEnum.READ);

        request.setMethod("PATCH");
        bean.setId(groupId);
        bean.setName(newName);
        bean.setDescription(newDescription);
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);
    }


    @Test
    public void testDestroy() throws Exception {
        authenticateAccess(AccessLevelEnum.OWNER);
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        String groupId = GroupDataHandlerMock.EXISTING_GROUP0;
        assertNotNull(groupDataHandler.getGroup(groupId));
        request.setMethod("DELETE");
        bean.setId(groupId);
        bean.renderRequest(request, response);
        assertBeanHasNoErrors();
        assertStatusCodeEquals(STATUS_OK, response);
        assertNull(groupDataHandler.getGroup(groupId));
    }

    @Test
    public void testDestroyWithInsufficientRights() throws Exception {
        authenticateAccess(AccessLevelEnum.WRITE);
        IGroupDataHandler groupDataHandler = ServiceLocator.getService(IGroupDataHandler.class);
        String groupId = GroupDataHandlerMock.EXISTING_GROUP0;
        assertNotNull(groupDataHandler.getGroup(groupId));
        request.setMethod("DELETE");
        bean.setId(groupId);
        bean.renderRequest(request, response);
        assertBeanHasNoErrors();
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);
        assertNotNull(groupDataHandler.getGroup(groupId));
    }


    @Test
    public void testGetAttributeName() throws Exception {
        assertEquals(bean.getAttributeName(), "Group");
        assertEquals(bean.getPluralAttributeName(), "Groups");
    }

    @Test
    public void testHasErrors() throws Exception {
        assertFalse(bean.hasErrors());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleRequestWithRequestIsNull() throws Exception {
        bean.renderRequest(null, response);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleRequestWithResponseIsNull() throws Exception {
        bean.renderRequest(request, null);
    }

/*    @Test(expected = IllegalArgumentException.class)
    public void testSetIdWhenIdIsNull() throws Exception {
        bean.setId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNameWhenNameIsNull() throws Exception {
        bean.setName(null);
    }
    */
}