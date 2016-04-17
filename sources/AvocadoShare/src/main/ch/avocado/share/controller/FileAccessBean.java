package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.ISecurityHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import org.apache.taglibs.standard.lang.jstl.test.beans.Factory;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;

import java.io.Serializable;

/**
 * Created by bergm on 29/03/2016.
 */
public class FileAccessBean implements Serializable {

    private String fileId;
    private String requesterUserMail;
    private String ownerUserId;
    private String requesterUserId;

    public boolean requestAccess() {
        if (fileId == null) throw new IllegalArgumentException("fileId is null");
        if (requesterUserMail == null) throw new IllegalArgumentException("requestingUserMail is null");
        IMailingService mailingService;
        IFileDataHandler fileDataHandler;
        IUserDataHandler userDataHandler;
        ISecurityHandler securityHandler;
        try {
            mailingService = ServiceLocator.getService(IMailingService.class);
            fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            return false;
        }

        File file;
        User user;
        User owningUser;
        AccessLevelEnum currentLevel;
        try {
            user = userDataHandler.getUserByEmailAddress(requesterUserMail);
            if (user == null) {
                return false;
            }
            file = fileDataHandler.getFile(fileId);
            if (file == null) {
                return false;
            }
            if (file.getOwnerId() == null) {
                System.out.println("File has no owner");
                return false;
            }
            currentLevel = securityHandler.getAccessLevel(user, file);
            owningUser = userDataHandler.getUser(file.getOwnerId());
        } catch (DataHandlerException e) {
            e.printStackTrace();
            return false;
        }
        if (currentLevel.containsLevel(AccessLevelEnum.READ)) {
            return false;
        }
        return mailingService.sendRequestAccessEmail(user, owningUser, file);
    }

    public boolean grantAccess() {
        if (requesterUserId == null) throw new IllegalArgumentException("requesterUserId is null");
        if (ownerUserId == null) throw new IllegalArgumentException("ownerUserId is null");
        if (fileId == null) throw new IllegalArgumentException("fileId is null");

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
            file = fileDataHandler.getFile(fileId);
            if (file == null) return false;
            AccessLevelEnum requestingUserCurrentLevel = securityHandler.getAccessLevel(requesterUserId, fileId);
            AccessLevelEnum ownerUserLevel = securityHandler.getAccessLevel(ownerUserId, fileId);
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
        }
        return false;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getRequesterUserId() {
        return requesterUserId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public String getRequesterUserMail() {
        return requesterUserMail;
    }

    public void setRequesterUserMail(String requesterUserMail) {
        this.requesterUserMail = requesterUserMail;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
