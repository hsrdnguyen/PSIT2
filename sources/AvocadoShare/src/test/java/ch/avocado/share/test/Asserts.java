package ch.avocado.share.test;

import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.User;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 18.04.16.
 */
public class Asserts {
    public static void assertCategoriesEquals(Collection<Category> expected, Collection<Category> actual) {
        List<String> expectedNames = getCategoryNames(expected);
        List<String> actualNames = getCategoryNames(actual);
        for (Iterator<String> iterator = expectedNames.iterator(); iterator.hasNext(); ) {
            String expectedName = iterator.next();
            assertTrue("Category \"" + expectedName + "\" not in actual categories", actualNames.contains(expectedName));
            iterator.remove();
        }
        if(!expectedNames.isEmpty()) {
            String missingNames = "";
            for(String name: expectedNames) {
                missingNames += "\"" + name + "\" ";
            }
            fail("Missing categories: " + missingNames);
        }
    }

    private static List<String> getCategoryNames(Collection<Category> categories) {
        if(categories == null) throw new NullPointerException("categories is null");
        ArrayList<String> names = new ArrayList<>(categories.size());
        for(Category category: categories) {
            names.add(category.getName());
        }
        return names;
    }

    static public void assertEqualUsers(User expected, User actual) {
        assertFalse("They are the same object", actual == expected);
        assertEquals("Id", expected.getId(), actual.getId());
        assertEquals("Creation Date", actual.getCreationDate(), actual.getCreationDate());
        assertEquals("Prename", expected.getPrename(), actual.getPrename());
        assertEquals("Surname", expected.getSurname(), actual.getSurname());
        assertEquals("Description", expected.getDescription(), actual.getDescription());
        assertEquals("Password digest", expected.getPassword().getDigest(), actual.getPassword().getDigest());
        assertEquals("Avatar", expected.getAvatar(), actual.getAvatar());
        if(expected.getMail() != null && actual.getMail() != null) {
            assertEquals("Email address verification", expected.getMail().getVerification(), actual.getMail().getVerification());
            assertEquals("Email verified", expected.getMail().isVerified(), actual.getMail().isVerified());
            assertEquals("Email address", expected.getMail().getAddress(), actual.getMail().getAddress());
        }
        assertEquals("Mail", expected.getMail(), actual.getMail());
        assertEquals("Reset verification", expected.getPassword().getResetVerification(), actual.getPassword().getResetVerification());
    }



    public static void assertIsRedirected(HttpServletResponse response) {
        String redirectUrl = response.getHeader("location");
        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertNotNull(redirectUrl);
    }

    public static void assertIsLoggedOut(MockHttpServletRequest request) {
        UserSession session = new UserSession(request);
        assertFalse("Not logged out", session.isAuthenticated());
    }

    public static void assertIsLoggedIn(MockHttpServletRequest request) {
        UserSession session = new UserSession(request);
        assertTrue("Not logged in", session.isAuthenticated());
    }
}
