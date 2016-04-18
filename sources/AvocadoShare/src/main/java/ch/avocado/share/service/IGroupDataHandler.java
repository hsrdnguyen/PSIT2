package ch.avocado.share.service;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.Collection;
import java.util.List;

/**
 * Group data handler
 */
public interface IGroupDataHandler {
    /**
     * Get a group by its identfier.
     * @param Id The identifier (not null)
     * @return The group or null if the group could not be found.
     * @throws DataHandlerException If an error occurs while querying the group.
     */
    Group getGroup(String Id) throws DataHandlerException;

    /**
     * Returns a list of all groups with identifiers found in the list.
     * If the list contains an identifier multiple times the group will be multiple times in the result.
     * If an identifier is not the identifier of a list there will nothing added to the list.
     * @param ids a list of identifiers
     * @return a list of groups
     * @throws DataHandlerException if something goes wrong this
     */
    List<Group> getGroups(Collection<String> ids) throws DataHandlerException;

    /**
     * Add a group
     * @param group The group to be ADDED
     * @return the identifier of the group
     * @throws DataHandlerException
     */
    String addGroup(Group group) throws DataHandlerException;

    /**
     * Updates a group. All attributes except the identifier can be changed.
     * @param group The group.
     * @return {@code true} if the group was found and updated. {@code false} if there is no such group.
     * @throws DataHandlerException
     */
    boolean updateGroup(Group group) throws DataHandlerException;

    /**
     * Deletes a group.
     * @param group
     * @return
     * @throws DataHandlerException
     */
    boolean deleteGroup(Group group) throws DataHandlerException;
    Group getGroupByName(String name) throws DataHandlerException;
}