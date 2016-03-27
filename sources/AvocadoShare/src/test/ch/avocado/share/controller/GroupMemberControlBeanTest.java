package ch.avocado.share.controller;

import ch.avocado.share.model.data.*;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.Mock.GroupDataHandlerMock;
import ch.avocado.share.service.Mock.SecurityHandlerMock;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

/**
 * Tests for {@link GroupMemberControlBean}
 */
public class GroupMemberControlBeanTest extends BeanTestBase {

    private GroupMemberControlBean bean;


    @Before
    public void setUpBean() throws ServiceNotFoundException {
        bean = new GroupMemberControlBean();
    }


    @Test
    public void testGetTargetById() throws Exception {
        Group expected = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        assertNotNull(expected);
        Group got = bean.getTargetById(GroupDataHandlerMock.EXISTING_GROUP0);
        assertNotNull(got);
        assertEquals(expected.getId(), got.getId());
        assertEquals(expected.getName(), got.getName());
    }

    @Test
    public void testGetTemplateFolder() {
        assertEquals(bean.getTemplateFolder(), "member_templates/");
    }

    @Test
    public void testCreateAccess() throws Exception {
        Group target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        User accessingUser = securityHandlerMock.getUsersWithAccessIncluding(AccessLevelEnum.OWNER, target)[0];
        bean.setAccessingUser(accessingUser);
        User user = securityHandlerMock.getUserWithAccess(AccessLevelEnum.NONE);
        assertNotNull(target);
        assertNotNull(user);
        bean.setTargetId(target.getId());
        assertEquals(bean.getTargetId(), target.getId());
        bean.setLevel(AccessLevelEnum.WRITE);
        bean.setUserId(user.getId());
        bean.createAccess();
        assertEquals(AccessLevelEnum.WRITE, getSecurityHandler().getAccessLevel(user, target));
    }

    @Test
    public void testCreateAccessWithExistingRights() throws Exception {
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        User accessingUser = securityHandlerMock.getUsersWithAccessIncluding(AccessLevelEnum.OWNER, target)[0];
        bean.setAccessingUser(accessingUser);
        assertNotNull(target);
        for(User user: getSecurityHandler().getUsersWithAccessIncluding(AccessLevelEnum.READ, target)) {
            assertNotNull(user);
            bean.setTargetId(target.getId());
            assertEquals(bean.getTargetId(), target.getId());
            bean.setLevel(AccessLevelEnum.WRITE);
            bean.setUserId(user.getId());
            try {
                bean.createAccess();
                fail("Not failed for user with access rights " + user.getId());
            } catch (HttpBeanException e) {
                assertNotNull(e.getDescription());
                assertNotEquals(e.getStatusCode(), STATUS_OK);
                assertTrue(e.getDescription().toLowerCase().contains("existiert"));
            }
            if(!user.getId().equals(accessingUser.getId())) {
                // Test if replace works with same parameters
                bean.replaceAccess();
                assertEquals(AccessLevelEnum.WRITE, getSecurityHandler().getAccessLevel(user, target));
            }
        }

        // Test if create works when applied with user who has no access
        User user = securityHandlerMock.getUserWithAccess(AccessLevelEnum.NONE);
        bean.setUserId(user.getId());
        bean.createAccess();
    }

    @Test
    public void testCreateAccessForSameGroup() throws Exception {
        Group group = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        User accessingUser = securityHandlerMock.getUsersWithAccessIncluding(AccessLevelEnum.OWNER, group)[0];
        bean.setAccessingUser(accessingUser);
        assertNotNull(group);
        assertNotNull(group.getId());
        bean.setTargetId(group.getId());
        bean.setGroupId(group.getId());
        assertEquals(bean.getTargetId(), group.getId());
        bean.setLevel(AccessLevelEnum.WRITE);
        try{
            bean.createAccess();
            fail();
        }catch (HttpBeanException e){
            assertTrue(e.getDescription().toLowerCase().contains("gleich"));
            assertNotEquals(e.getStatusCode(), STATUS_OK);
        }
    }

    @Test
    public void testReplaceAccess() throws Exception {
        // only tested in testCreateAccessWithExistingRights for now.
    }


    private void createAccessWithPost(AccessIdentity owner, AccessControlObjectBase target, AccessLevelEnum level) throws Exception {
        request.setMethod("POST");
        if(owner instanceof User) {
            bean.setUserId(owner.getId());
        }
        if(owner instanceof Group) {
            bean.setGroupId(owner.getId());
        }
        bean.setTargetId(target.getId());
        bean.setLevel(level);
        bean.renderRequest(request, response);
    }

    @Test
    public void testDoPostForUserRights() throws Exception {
        User owner = securityHandlerMock.getUserWithAccess(AccessLevelEnum.NONE);
        authenticateAccess(AccessLevelEnum.OWNER);
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        createAccessWithPost(owner, target, AccessLevelEnum.WRITE);
        assertStatusCodeEquals(STATUS_OK, response);
        assertEquals(getSecurityHandler().getAccessLevel(owner, target), AccessLevelEnum.WRITE);
    }

    @Test
    public void testDoPostForGroupRights() throws Exception {
        Group owner = securityHandlerMock.getGroupWithAccess(AccessLevelEnum.NONE);
        authenticateAccess(AccessLevelEnum.OWNER);
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        createAccessWithPost(owner, target, AccessLevelEnum.WRITE);
        assertStatusCodeEquals(STATUS_OK, response);
        assertEquals(getSecurityHandler().getAccessLevel(owner, target), AccessLevelEnum.WRITE);
    }

    @Test
    public void testDoPostMissingLevel() throws Exception {
        User owner = securityHandlerMock.getUserWithAccess(AccessLevelEnum.NONE);
        authenticateAccess(AccessLevelEnum.OWNER);
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        request.setMethod("POST");
        bean.setUserId(owner.getId());
        bean.setTargetId(target.getId());
        bean.renderRequest(request, response);
        assertStatusCodeEquals(HttpServletResponse.SC_BAD_REQUEST, response);
        assertEquals(AccessLevelEnum.NONE, getSecurityHandler().getAccessLevel(owner, target));
        assertTrue(response.getErrorMessage().toLowerCase().contains("level"));
    }

    @Test
    public void testDoPostMissingOwner() throws Exception {
        User owner = securityHandlerMock.getUserWithAccess(AccessLevelEnum.NONE);
        authenticateAccess(AccessLevelEnum.OWNER);
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        request.setMethod("POST");
        bean.setLevel(AccessLevelEnum.READ);
        bean.setTargetId(target.getId());
        bean.renderRequest(request, response);
        assertStatusCodeEquals(HttpServletResponse.SC_BAD_REQUEST, response);
        assertEquals(AccessLevelEnum.NONE, getSecurityHandler().getAccessLevel(owner, target));
        assertTrue(response.getErrorMessage().toLowerCase().contains("group"));
        assertTrue(response.getErrorMessage().toLowerCase().contains("user"));
    }

    @Test
    public void testDoPostUnauthenticated() throws Exception {
        User owner = securityHandlerMock.getUserWithAccess(AccessLevelEnum.NONE);
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        createAccessWithPost(owner, target, AccessLevelEnum.WRITE);
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);
        assertEquals(AccessLevelEnum.NONE, getSecurityHandler().getAccessLevel(owner, target));
    }

    @Test
    public void testDoPostWithWriteRights() throws Exception {
        User owner = securityHandlerMock.getUserWithAccess(AccessLevelEnum.NONE);
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        authenticateAccess(AccessLevelEnum.WRITE);
        createAccessWithPost(owner, target, AccessLevelEnum.READ);
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);
        assertEquals(AccessLevelEnum.NONE, getSecurityHandler().getAccessLevel(owner, target));
    }


    @Test
    public void testDoGet() throws Exception {
        request.setMethod("GET");
        authenticateAccess(AccessLevelEnum.READ);
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        bean.setTargetId(target.getId());
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_OK, response);
    }

    @Test
    public void testDoGetUnauthenticated() throws Exception {
        request.setMethod("GET");
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        bean.setTargetId(target.getId());
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);
    }


    @Test
    public void testDoPut() throws Exception {
        request.setMethod("PUT");
        AccessControlObjectBase target = getGroupDataHandler().getGroup(GroupDataHandlerMock.EXISTING_GROUP0);
        bean.setTargetId(target.getId());
        bean.renderRequest(request, response);
        assertStatusCodeEquals(STATUS_FORBIDDEN, response);

    }

    @Test
    public void testDoPatch() throws Exception {

    }

    @Test
    public void testDoDelete() throws Exception {

    }

    @Test
    public void testSetMethod() throws Exception {

    }
}