package ch.avocado.share.model.data;

import com.sun.org.apache.regexp.internal.RE;
import org.junit.Test;

import static ch.avocado.share.model.data.AccessLevelEnum.*;
import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 06.05.16.
 */
public class AccessLevelEnumTest {

    @Test
    public void testContainsLevel() {
        assertFalse(NONE.containsLevel(READ));
        assertFalse(NONE.containsLevel(WRITE));
        assertFalse(NONE.containsLevel(MANAGE));
        assertFalse(NONE.containsLevel(OWNER));

        assertTrue(READ.containsLevel(NONE));
        assertTrue(READ.containsLevel(READ));
        assertFalse(READ.containsLevel(WRITE));
        assertFalse(READ.containsLevel(MANAGE));
        assertFalse(READ.containsLevel(OWNER));

        assertTrue(WRITE.containsLevel(NONE));
        assertTrue(WRITE.containsLevel(READ));
        assertTrue(WRITE.containsLevel(WRITE));
        assertFalse(WRITE.containsLevel(MANAGE));
        assertFalse(WRITE.containsLevel(OWNER));

        assertTrue(MANAGE.containsLevel(NONE));
        assertTrue(MANAGE.containsLevel(READ));
        assertTrue(MANAGE.containsLevel(WRITE));
        assertTrue(MANAGE.containsLevel(MANAGE));
        assertFalse(WRITE.containsLevel(OWNER));

        assertTrue(OWNER.containsLevel(NONE));
        assertTrue(OWNER.containsLevel(READ));
        assertTrue(OWNER.containsLevel(WRITE));
        assertTrue(OWNER.containsLevel(MANAGE));
        assertTrue(OWNER.containsLevel(OWNER));
    }

    @Test(expected = NullPointerException.class)
    public void testContainsLevelNull() {
        NONE.containsLevel(null);
    }
}