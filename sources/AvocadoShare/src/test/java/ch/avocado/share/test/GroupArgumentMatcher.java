package ch.avocado.share.test;

import ch.avocado.share.model.data.Group;
import org.mockito.ArgumentMatcher;


/**
 * Argument matcher for Groups
 */
public class GroupArgumentMatcher implements ArgumentMatcher<Group> {

    private Group group;

    public GroupArgumentMatcher(Group group) {
        this.group = group;
    }

    @Override
    public boolean matches(Object o) {
        if(!(o instanceof Group)) {
            return false;
        }
        Group otherGroup = (Group) o;
        return otherGroup.getId().equals(group.getId());
    }
}