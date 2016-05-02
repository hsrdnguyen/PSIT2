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
import ch.avocado.share.service.exceptions.ObjectNotFoundException;

import java.io.Serializable;

/**
 * Bean to allow requesting and granting request to a file.
 */
public class FileAccessBean implements Serializable {

    /**
     * The identifier of the file
     */
    private String fileId;

    /**
     * The e-mail address of the user who requests the access.
     */
    private String requesterUserMail;
    private String ownerUserId;
    private String requesterUserId;

    /**
     * Please make sure that {@link #setFileId(String)} and {@link #requesterUserMail}
     * @return
     */
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
            e.printStackTrace();
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
            try {
                file = fileDataHandler.getFile(fileId);
            } catch (ObjectNotFoundException e) {
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
        IUserDataHandler userDataHandler;

        User fileOwner;
        File file;

        try {
            userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
            fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            try {
                file = fileDataHandler.getFile(fileId);
                fileOwner = userDataHandler.getUser(ownerUserId);
            } catch (ObjectNotFoundException e) {
                return false;
            }
            AccessLevelEnum requestingUserCurrentLevel = securityHandler.getAccessLevel(requesterUserId, fileId);
            AccessLevelEnum ownerUserLevel = securityHandler.getAccessLevel(fileOwner, file);
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

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public void setRequesterUserMail(String requesterUserMail) {
        this.requesterUserMail = requesterUserMail;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
