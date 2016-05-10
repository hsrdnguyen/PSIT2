package ch.avocado.share.service;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;

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
    Group getGroup(String Id) throws DataHandlerException, ObjectNotFoundException;

    /**
     * Returns a list of all groups with identifiers found in the list.
     * If the list contains an identifier multiple times the group will be multiple times in the result.
     * If an identifier is not the identifier of a list there will nothing added to the list.
     * @param idCollection a list of identifiers
     * @return a list of groups
     * @throws DataHandlerException if something goes wrong this
     */
    List<Group> getGroups(Collection<String> idCollection) throws DataHandlerException;

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
    void updateGroup(Group group) throws DataHandlerException, ObjectNotFoundException;

    /**
     * Deletes a group.
     * @param group
     * @return
     * @throws DataHandlerException
     */
    void deleteGroup(Group group) throws DataHandlerException, ObjectNotFoundException;

    /**
     * Loads a group from the database with the given name
     * @param name name of the group that should be loaded
     * @return Loaded group
     * @throws DataHandlerException
     */
    Group getGroupByName(String name) throws DataHandlerException, ObjectNotFoundException;


    /**
     * searches all groups
     * @param searchString String to search after
     * @return groups that match the given String
     * @throws DataHandlerException
     */
    List<Group> searchGroups(String searchString) throws DataHandlerException;
}
