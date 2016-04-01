package ch.avocado.share.controller;

import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IGroupDataHandler;

public class GroupMemberControlBean extends MemberControlBean<Group> {

    @Override
    protected Group getTargetById(String id) throws HttpBeanException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        return groupDataHandler.getGroup(id);
    }
}
