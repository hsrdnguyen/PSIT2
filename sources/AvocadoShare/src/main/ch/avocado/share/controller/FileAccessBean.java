package ch.avocado.share.controller;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IMailingService;
import ch.avocado.share.service.IUserDataHandler;

import java.io.Serializable;

/**
 * Created by bergm on 29/03/2016.
 */
public class FileAccessBean implements Serializable {

    private String fileId;
    private String requestingUserMail;
    private String ouserId;
    private String ruserId;

    public boolean requestAccess()
    {
        if(fileId == null) throw new IllegalArgumentException("fileId is null");
        if(requestingUserMail == null) throw new IllegalArgumentException("requestingUserMail is null");

        try {
            IMailingService mailingService = ServiceLocator.getService(IMailingService.class);
            IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);

            User user = userDataHandler.getUserByEmailAddress(requestingUserMail);
            File file = fileDataHandler.getFileById(fileId);
            User owningUser = userDataHandler.getUser(file.getOwnerId());
            mailingService.sendRequestAccessEmail(user, owningUser, file);
            return true;

        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean grantAccess()
    {
        if(ruserId == null) throw new IllegalArgumentException("ruserId is null");
        if(ouserId == null) throw new IllegalArgumentException("ouserId is null");
        if(fileId == null) throw new IllegalArgumentException("fileId is null");

        try {
            IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
            IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);

            File file = fileDataHandler.getFileById(fileId);

            if (file.getOwnerId().equals(ouserId)){
                fileDataHandler.grantAccess(fileId, ruserId);
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getOuserId() {
        return ouserId;
    }

    public void setOuserId(String ouserId) {
        this.ouserId = ouserId;
    }

    public String getRuserId() {
        return ruserId;
    }

    public void setRuserId(String ruserId) {
        this.ruserId = ruserId;
    }

    public String getRequestingUserMail() {
        return requestingUserMail;
    }

    public void setRequestingUserMail(String requestingUserMail) {
        this.requestingUserMail = requestingUserMail;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
