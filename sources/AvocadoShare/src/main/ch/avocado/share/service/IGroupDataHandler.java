package ch.avocado.share.service;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.exceptions.DataHandlerException;

/**
 * Created by coffeemakr on 21.03.16.
 */
public interface IGroupDataHandler {
    Group getGroup(String Id) throws DataHandlerException;
    String addGroup(Group group) throws DataHandlerException;
    boolean updateGroup(Group group) throws DataHandlerException;
    boolean deleteGroup(Group group) throws DataHandlerException;
    Group getGroupByName(String name) throws DataHandlerException;
}
