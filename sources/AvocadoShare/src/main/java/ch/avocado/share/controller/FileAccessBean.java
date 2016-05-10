package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.exceptions.MailingServiceException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
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
    private User objectOwner;
    private String requesterUserId;

    /**
     * Please make sure that {@link #setFileId(String)} and {@link #requesterUserMail}
     * @return
     */
    public boolean requestAccess() {
        if (fileId == null) throw new NullPointerException("fileId is null");
        if (requesterUserMail == null) throw new NullPointerException("requestingUserMail is null");
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
            file = fileDataHandler.getFile(fileId);

            if (file.getOwnerId() == null) {
                System.out.println("File has no owner");
                return false;
            }
            currentLevel = securityHandler.getAccessLevel(user, file);
            owningUser = userDataHandler.getUser(file.getOwnerId());
        } catch (DataHandlerException e) {
            e.printStackTrace();
            return false;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (currentLevel.containsLevel(AccessLevelEnum.READ)) {
            return false;
        }
        try {
            return mailingService.sendRequestAccessEmail(user, owningUser, file);
        } catch (MailingServiceException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean grantAccess() {
        if (requesterUserId == null) throw new NullPointerException("requesterUserId is null");
        if (objectOwner == null) throw new NullPointerException("objectOwner is null");
        if (fileId == null) throw new NullPointerException("fileId is null");

        IFileDataHandler fileDataHandler;
        ISecurityHandler securityHandler;
        IUserDataHandler userDataHandler;

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
        } catch (ObjectNotFoundException e){
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

    public void setRequesterUserMail(String requesterUserMail) {
        this.requesterUserMail = requesterUserMail;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
