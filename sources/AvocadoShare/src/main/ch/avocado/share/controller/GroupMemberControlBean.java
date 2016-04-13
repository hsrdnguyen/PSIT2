package ch.avocado.share.controller;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.Group;
import ch.avocado.share.model.exceptions.HttpBeanException;
import ch.avocado.share.service.IGroupDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import javax.servlet.http.HttpServletResponse;

public class GroupMemberControlBean extends MemberControlBean<Group> {

    @Override
    protected Group getTargetById(String id) throws HttpBeanException {
        IGroupDataHandler groupDataHandler = getService(IGroupDataHandler.class);
        try {
            return groupDataHandler.getGroup(id);
        } catch (DataHandlerException e) {
            throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                        ErrorMessageConstants.DATAHANDLER_EXPCEPTION);
        }
    }
}
