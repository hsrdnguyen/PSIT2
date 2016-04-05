package ch.avocado.share.service;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.Collection;
import java.util.List;

/**
 * Created by coffeemakr on 21.03.16.
 */
public interface IGroupDataHandler {
    Group getGroup(String Id) throws DataHandlerException;
    List<Group> getGroups(Collection<String> ids) throws DataHandlerException;
    String addGroup(Group group) throws DataHandlerException;
    boolean updateGroup(Group group) throws DataHandlerException;
    boolean deleteGroup(Group group) throws DataHandlerException;
    Group getGroupByName(String name) throws DataHandlerException;
}
