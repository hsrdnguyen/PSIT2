package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.HttpServletException;
import ch.avocado.share.service.*;
import ch.avocado.share.service.exceptions.*;

import javax.sql.rowset.serial.SerialRef;
import java.io.Serializable;

/**
 * Bean to allow requesting and granting request to a file.
 */
public class ObjectAccessBean implements Serializable {

    /**
     * The identifier of the file
     */
    private String fileId;

    private User objectOwner;
    private String requesterUserId;


    public boolean grantAccess() {
        if (requesterUserId == null) throw new NullPointerException("requesterUserId is null");
        if (objectOwner == null) throw new NullPointerException("objectOwner is null");
        if (fileId == null) throw new NullPointerException("fileId is null");

        IFileDataHandler fileDataHandler;
        ISecurityHandler securityHandler;

        File file;

        try {
            fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            try {
                file = fileDataHandler.getFile(fileId);
            } catch (ObjectNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            AccessLevelEnum requestingUserCurrentLevel = securityHandler.getAccessLevel(requesterUserId, fileId);
            AccessLevelEnum ownerUserLevel = securityHandler.getAccessLevel(objectOwner, file);
            // check if the owner has manage rights.
            if (ownerUserLevel.containsLevel(AccessLevelEnum.MANAGE)) {
                // check if the requester doesn't have rights already
                if (!requestingUserCurrentLevel.containsLevel(AccessLevelEnum.READ)) {
                    // set rights
                    return securityHandler.setAccessLevel(requesterUserId, fileId, AccessLevelEnum.READ);
                } else {
                    return true;
                }
            }
        } catch (DataHandlerException e) {
            e.printStackTrace();
            return false;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void setObjectOwner(User objectOwnerId) {
        this.objectOwner = objectOwnerId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

}
